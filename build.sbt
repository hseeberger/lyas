lazy val lyas = project
  .in(file("."))
  .enablePlugins(AutomateHeaderPlugin, GitVersioning)

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
