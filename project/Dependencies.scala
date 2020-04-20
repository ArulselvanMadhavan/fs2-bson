import sbt._

object Dependencies {
  val catsEffectVersion = "2.1.3"
  val fs2Version = "2.2.1"
  val reactiveMongoVersion = "0.20.3"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
  lazy val fs2core = "co.fs2" %% "fs2-core" % fs2Version
  lazy val fs2io = "co.fs2" %% "fs2-io" % fs2Version
  // lazy val fs2reactiveStreams = "co.fs2" %% "fs2-reactive-streams" % fs2Version
  lazy val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % reactiveMongoVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"
}
