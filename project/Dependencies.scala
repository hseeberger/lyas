import sbt._

object Version {
  final val AkkaHttp  = "10.0.0"
  final val AkkaLog4j = "1.2.0"
  final val AkkaSse   = "2.0.0-M5"
  final val Log4j     = "2.7"
  final val Scala     = "2.12.0"
  final val ScalaTest = "3.0.1"
}

object Library {
  val akkaHttp        = "com.typesafe.akka"        %% "akka-http"         % Version.AkkaHttp
  val akkaLog4j       = "de.heikoseeberger"        %% "akka-log4j"        % Version.AkkaLog4j
  val akkaSse         = "de.heikoseeberger"        %% "akka-sse"          % Version.AkkaSse
  val akkaHttpTestkit = "com.typesafe.akka"        %% "akka-http-testkit" % Version.AkkaHttp
  val log4jCore       = "org.apache.logging.log4j" %  "log4j-core"        % Version.Log4j
  val scalaTest       = "org.scalatest"            %% "scalatest"         % Version.ScalaTest
}
