name := "cqrs-rest-router"

val Akka  = "2.3.5"
val Spray = "1.3.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka"          %% "akka-actor"         % Akka     % "compile",
  "com.typesafe.akka"          %% "akka-cluster"       % Akka     % "compile",
  "com.typesafe.akka"          %% "akka-slf4j"         % Akka     % "compile",
  "ch.qos.logback"              % "logback-classic"    % "1.1.2"  % "compile",
  "io.spray"                   %% "spray-routing"      % Spray    % "compile",
  "io.spray"                   %% "spray-can"          % Spray    % "compile",
  "io.spray"                   %% "spray-client"       % Spray    % "compile",
  "org.json4s"                 %% "json4s-native"      % "3.2.10" % "compile",
  "org.scalatest"              %% "scalatest"          % "2.2.0"  % "compile",
  "com.typesafe.scala-logging" %% "scala-logging"      % "3.0.0"  % "compile",
  "com.typesafe.akka"          %% "akka-testkit"       % Akka     % "test",
  "io.spray"                   %% "spray-testkit"      % Spray    % "test"
)

fork in Test := false

fork in run := true

parallelExecution in Test := false

testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-h", target(_ / "html-reports").value.getPath)