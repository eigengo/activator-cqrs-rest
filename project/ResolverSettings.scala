import sbt._

/**
 * Settings here do not apply on Jenkins. Adding a resolver here without informing DevOps may cause build
 * failures: DevOps overrides the resolvers, and uses local Artifactory as a proxy. If the dependency
 * you need is not found in the Artifactory, the build will fail. In that case, contact DevOps with the
 * repository URL.
 * To set this, run with ``-Dsbt.resolver.url=<REPO-URL>``
 */
object ResolverSettings {
 
  /**
   * The local / development resolvers: it includes the default ones + Scala Linter.
   */
  lazy val resolvers = Seq(
    Resolver.mavenLocal,
    Resolver.sonatypeRepo("releases"),
    Resolver.typesafeRepo("releases"),
    Resolver.typesafeRepo("snapshots"),
    Resolver.sonatypeRepo("snapshots"),
   "Linter" at "http://hairyfotr.github.io/linteRepo/releases",
   "krasserm" at "http://dl.bintray.com/krasserm/maven")
}