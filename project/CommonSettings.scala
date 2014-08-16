import sbt._
import Keys._

/**
 * Defines settings for the projects:
 *
 * - Scalastyle default
 * - Scalac, Javac: warnings as errors, target JDK 1.7
 * - Fork for run
 */
object CommonSettings {
 
  /**
   * Common project settings
   */
  val commonSettings: Seq[Def.Setting[_]] =
    Seq(
      organization := "org.eigengo.cqrsrest",
      scalaVersion := "2.11.2",
      scalacOptions in Compile ++= Seq("-encoding", "UTF-8", "-target:jvm-1.7", "-deprecation", "-unchecked", "-Ywarn-dead-code", "-Xfatal-warnings", "-feature", "-language:postfixOps"),
      scalacOptions in (Compile, doc) <++= (name in (Compile, doc), version in (Compile, doc)) map DefaultOptions.scaladoc,
      javacOptions in (Compile, compile) ++= Seq("-source", "1.7", "-target", "1.7", "-Xlint:unchecked", "-Xlint:deprecation", "-Xlint:-options"),
      javacOptions in doc := Seq(),
      javaOptions += "-Xmx2G",
      outputStrategy := Some(StdoutOutput),
      // for One-JAR
      exportJars := true,
      // this will be required for monitoring until Akka monitoring SPI comes in
      fork := true,
      resolvers := ResolverSettings.resolvers
    ) 

  def generateVersion(major: String, minor: String, snapshot: Boolean) = {
    // Include the git version sha in the build version for repeatable historical builds.
    val gitHeadCommitSha = settingKey[String]("current git commit SHA")
    val incremental = Process("git rev-parse HEAD").lines.head
    s"$major.$minor-$incremental${if (snapshot) "-SNAPSHOT"}"
  }
}
