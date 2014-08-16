import sbt._
import Keys._
import sbtassembly.Plugin
import sbtassembly.Plugin.AssemblyKeys

/**
 * This object includes the publishing mechanism. We simply publish to the ``artifacts`` directory,
 * which Jenkins build uses to automatically push the built artefacts to Artifactory.
 */
object PublishSettings {
  
  /**
   * The settings for the publish process. Notice that we do not publish the Linter:
   * ``publishArtifact in (Compile, packageDoc) := false``.
   */
  lazy val publishSettings: Seq[Def.Setting[_]] = Seq(
    publishArtifact in (Compile, packageDoc) := false,
    publishTo := Some(Resolver.file("file", new File("artifacts")))
  )

  import AssemblyKeys._

  /**
   * Defines the assembly settings, which drive the way in which the microservice JAR is produced
   */
  lazy val assemblySettings: Seq[Def.Setting[_]] =
    Plugin.assemblySettings ++
    Seq(
      artifact in (Compile, assembly) ~= { art =>
        art.copy(`classifier` = Some("assembly"))
      }
    ) ++
    addArtifact(artifact in (Compile, assembly), assembly).settings
}
