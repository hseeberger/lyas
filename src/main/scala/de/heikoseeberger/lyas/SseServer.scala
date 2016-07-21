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

object SseServerApp {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()

    val config     = system.settings.config
    val address    = config.getString("lyas.sse-server.address")
    val port       = config.getInt("lyas.sse-server.port")
    val rate       = config.getInt("lyas.sse-server.rate")
    val nrOfEvents = config.getInt("lyas.sse-server.nr-of-events")
    SseServer(address, port, rate, nrOfEvents)

    Await.ready(system.whenTerminated, Duration.Inf)
  }
}

object SseServer {

  def apply(address: String, port: Int, rate: Int, nrOfEvents: Int)(
      implicit system: ActorSystem
  ): Unit = {
    implicit val mat = ActorMaterializer()
    val log          = Logging(system, getClass)

    import system.dispatcher
    Http()
      .bindAndHandle(route(rate, nrOfEvents, log), address, port)
      .onComplete {
        case Success(ServerBinding(a)) =>
          log.info("Listenting on {}", a)
        case Failure(c) =>
          log.error(c, "Can't bind to {}:{}", address, port)
          system.terminate()
      }
  }

  private def route(rate: Int, nrOfEvents: Int, log: LoggingAdapter) = {
    import Directives._
    import EventStreamMarshalling._

    def toServerSentEvent(n: Int) = {
      val id = n.toString
      ServerSentEvent(data = s"SSE-$id", id = Some(id))
    }

    pathSingleSlash {
      get {
        optionalHeaderValueByName(`Last-Event-ID`.name) { lastEventId =>
          log.debug("Event stream requested for Last-Event-ID: {}",
                    lastEventId.getOrElse(""))
          try {
            val fromSeqNo = lastEventId.getOrElse("0").trim.toInt + 1
            complete {
              Source
                .fromIterator(() => Iterator.from(fromSeqNo))
                .take(nrOfEvents)
                .throttle(rate, 1.second, 1, ThrottleMode.Shaping)
                .map(toServerSentEvent)
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
