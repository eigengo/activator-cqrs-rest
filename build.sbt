import CommonSettings._

version in ThisBuild := generateVersion("1", "0", snapshot = true)

publishLocal := {}

publish := {}

lazy val root = Project("cqrs-rest", file("."))
  .settings(commonSettings:_*)
  .aggregate(write, read, router_api, router)

// The domain lives in write.
lazy val write = Project("cqrs-rest-write", file("write"))
  .dependsOn(router_api)
  .settings(commonSettings:_*)

// The read side is for non-domain functionality and may contain mashups
lazy val read = Project("cqrs-rest-read", file("read"))
  .settings(commonSettings:_*)

// The read side is for query functionality
lazy val query = Project("cqrs-rest-query", file("query"))
  .dependsOn(router_api)
  .settings(commonSettings:_*)

// The router combines the APIs
lazy val router = Project("cqrs-rest-router", file("router"))
  .dependsOn(router_api)
  .settings(commonSettings:_*)

// The router API that the reads and writes can use to register their APIs
lazy val router_api = Project("cqrs-rest-router-api", file("router-api"))
  .settings(commonSettings:_*)

publishArtifact := false

lazy val runAll = inputKey[Unit]("Runs all modules")

runAll := {
  val s: TaskStreams = streams.value
  println("*************** Starting Router...")
  ((run in Compile) in router).evaluated
  Thread.sleep(5000L)
  println("*************** Router running. Starting write, read and query...")
  ((run in Compile) in read).evaluated
  ((run in Compile) in write).evaluated
  // run query many times...
  ((run in Compile) in query).evaluated
  ((run in Compile) in query).evaluated
  ((run in Compile) in query).evaluated
  ((run in Compile) in query).evaluated
  ((run in Compile) in query).evaluated
  ((run in Compile) in query).evaluated
  ((run in Compile) in query).evaluated
  println("*************** All running. http://localhost:8080/$VERSION/")
}

logLevel in runAll := Level.Info
