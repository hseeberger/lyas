lazy val lyas =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin,
                   GitVersioning,
                   JavaAppPackaging,
                   DockerPlugin)

libraryDependencies ++= Vector(
  Library.akkaHttp,
  Library.akkaLog4j,
  Library.akkaSse,
  Library.log4jCore,
  Library.akkaHttpTestkit % "test",
  Library.scalaTest       % "test"
)

initialCommands := """|import de.heikoseeberger.lyas._
                      |import akka.actor._
                      |import akka.stream._
                      |import akka.stream.scaladsl._
                      |""".stripMargin

daemonUser.in(Docker) := "root"
maintainer.in(Docker) := "Heiko Seeberger"
version.in(Docker)    := "latest"
dockerBaseImage       := "java:8"
dockerRepository      := Some("hseeberger")
