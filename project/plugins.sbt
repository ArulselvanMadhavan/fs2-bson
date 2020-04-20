resolvers += Classpaths.sbtPluginReleases
resolvers += "Typesafe Repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.11")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"   % "2.3.4")

