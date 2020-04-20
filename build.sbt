import Dependencies._

ThisBuild / scalaVersion     := "2.13.1"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "dev.composter.fs2"
ThisBuild / organizationName := "bson"

val commonSettings = Seq(
  organizationName := "BSON parser using fs2",
  startYear := Some(2020),
  licenses += ("Apache-2.0", new URL("https://www.apache.org/licenses/LICENSE-2.0.txt")),
  libraryDependencies ++= List(fs2core, fs2io, reactiveMongo, scalaTest % Test),
  scalafmtOnCompile := true
)

lazy val root = (project in file("."))
  .aggregate(
    `fs2-bson-core`
  )

lazy val `fs2-bson-core` = project.in(file("modules/core")).settings(commonSettings: _*)

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
