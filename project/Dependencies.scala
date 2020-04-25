import sbt._

object Dependencies {
  val catsEffectVersion = "2.1.3"
  val fs2Version = "2.2.1"
  val reactiveMongoVersion = "0.20.3"
  val log4catsVersion = "1.0.1"
  val declineVersion = "1.0.0"
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % catsEffectVersion
  lazy val decline = "com.monovore" %% "decline" % declineVersion
  lazy val declineEffect = "com.monovore" %% "decline-effect" % declineVersion
  lazy val fs2core = "co.fs2" %% "fs2-core" % fs2Version
  lazy val fs2io = "co.fs2" %% "fs2-io" % fs2Version
  lazy val log4catsCore = "io.chrisdavenport" %% "log4cats-core"    % log4catsVersion  // Only if you want to Support Any Backend
  lazy val log4catsSlf4j = "io.chrisdavenport" %% "log4cats-slf4j"   % log4catsVersion  // Direct Slf4j Support - Recommended  
  lazy val reactiveMongo = "org.reactivemongo" %% "reactivemongo" % reactiveMongoVersion
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1"
}
