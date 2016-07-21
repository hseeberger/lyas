/*
 * Copyright 2016 Heiko Seeberger
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.heikoseeberger.lyas

import akka.actor.ActorSystem
import akka.event.{ Logging, LoggingAdapter }
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.Source
import akka.stream.{ ActorMaterializer, ThrottleMode }
import de.heikoseeberger.akkasse.{ EventStreamMarshalling, ServerSentEvent }
import de.heikoseeberger.akkasse.headers.`Last-Event-ID`
import scala.concurrent.Await
import scala.concurrent.duration.{ Duration, DurationInt }
import scala.util.{ Failure, Success }

/**
  * Provides the HTTP endpoint `/` from which a finite source of Server Sent
  * Events can be obtained. The events have an increasing integral ID. The
  * server respects the `Last-Event-ID` header by providing a stream whose first
  * event has an ID increaded by one.
  */
object SseServer {

  private final val Text = "Learn you Akka Streams for great Good!"

  def apply(address: String, port: Int, charsPerSecond: Int)(
      implicit system: ActorSystem
  ): Unit = {
    import system.dispatcher

    implicit val mat = ActorMaterializer()
    val log          = Logging(system, getClass)

    Http()
      .bindAndHandle(route(charsPerSecond, log), address, port)
      .onComplete {
        case Success(ServerBinding(a)) =>
          log.info("Listenting on {}", a)
        case Failure(c) =>
          log.error(c, "Can't bind to {}:{}", address, port)
          system.terminate()
      }
  }

  private def route(charsPerSecond: Int, log: LoggingAdapter) = {
    import Directives._
    import EventStreamMarshalling._

    def seqNos(fromSeqNo: Int) =
      Source.fromIterator(() => Iterator.from(fromSeqNo))

    def toServerSentEvent(c: Char, n: Int) = {
      val id = n.toString
      ServerSentEvent(data = c.toString, id = Some(id))
    }

    pathSingleSlash {
      get {
        optionalHeaderValueByName(`Last-Event-ID`.name) { lastEventId =>
          log.debug("Event stream requested for Last-Event-ID: {}",
                    lastEventId.getOrElse(""))
          try {
            val fromSeqNo = lastEventId.map(_.trim.toInt + 1).getOrElse(0)
            complete {
              Source(Text.substring(fromSeqNo % Text.length))
                .zipWith(seqNos(fromSeqNo))(toServerSentEvent)
                .throttle(charsPerSecond, 1.second, 1, ThrottleMode.Shaping)
            }
          } catch {
            case _: NumberFormatException =>
              complete {
                BadRequest -> "Integral number expected for Last-Event-ID header!"
              }
          }
        }
      }
    }
  }
}

object SseServerApp {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()

    val config         = system.settings.config
    val address        = config.getString("lyas.sse-server.address")
    val port           = config.getInt("lyas.sse-server.port")
    val charsPerSecond = config.getInt("lyas.sse-server.chars-per-second")
    SseServer(address, port, charsPerSecond)

    Await.ready(system.whenTerminated, Duration.Inf)
  }
}
