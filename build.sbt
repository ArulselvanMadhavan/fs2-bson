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
    `fs2-bson-core`,
    `fs2-bson-examples`
  )

lazy val `fs2-bson-core` = project.in(file("modules/core")).settings(commonSettings: _*)

lazy val `fs2-bson-examples` =
  project.in(file("modules/examples"))
  .settings(commonSettings: _*)
    .settings(libraryDependencies ++= List(decline, declineEffect, log4catsCore, log4catsSlf4j))
.settings(addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)
)
  .dependsOn(`fs2-bson-core`)
