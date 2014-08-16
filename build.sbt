import CommonSettings._

version in ThisBuild := generateVersion("1", "0", snapshot = true)

publishLocal := {}

publish := {}

lazy val root = Project("cqrs-rest", file("."))
  .settings((commonSettings ++ PublishSettings.publishSettings):_*)
  .aggregate(write, read, router)

// The domain lives in write.
lazy val write = Project("cqrs-rest-write", file("write"))
  .settings((commonSettings ++ PublishSettings.publishSettings ++ PublishSettings.assemblySettings):_*)

// The read side is for non-domain functionality and may contain mashups
lazy val read = Project("cqrs-rest-read", file("read"))
  .settings((commonSettings ++ PublishSettings.publishSettings ++ PublishSettings.assemblySettings):_*)

// The router combines the APIs
lazy val router = Project("cqrs-rest-router", file("router"))
  .settings((commonSettings ++ PublishSettings.publishSettings ++ PublishSettings.assemblySettings):_*)

publishArtifact := false
