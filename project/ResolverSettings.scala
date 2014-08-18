import sbt._

/**
 * The resolvers for the project's dependencies: the standard ones + Scala Linter + 
 * Akka Persistence in Cassanra.
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