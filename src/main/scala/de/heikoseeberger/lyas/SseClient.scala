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

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Get
import akka.http.scaladsl.model.{ HttpRequest, HttpResponse, Uri }
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.scaladsl.{
  Flow,
  GraphDSL,
  Keep,
  Merge,
  Sink,
  Source,
  Unzip
}
import akka.stream.{ ActorMaterializer, Materializer, SourceShape }
import de.heikoseeberger.akkasse.MediaTypes.`text/event-stream`
import de.heikoseeberger.akkasse.{ EventStreamUnmarshalling, ServerSentEvent }
import de.heikoseeberger.akkasse.headers.`Last-Event-ID`
import scala.concurrent.duration.Duration
import scala.concurrent.{ Await, ExecutionContext, Future }

/**
  * Gets a source of Server Sent Events from the given URI and streams it to
  * the given handler. Once a source is completed, a next one is obtained,
  * thereby sending an appropriate `Last-Evend-ID` header if possible. Returns
  * a source of materialized values of the handler.
  *
  *{{{
  * + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
  *                                               +---------------------+
  * |                                             |       trigger       |                                                           |
  *                                               +----------o----------+
  * |                                                        |                                                                      |
  *                                            Option[String]|
  * |                                                        v                                                                      |
  *              Option[String]                   +----------o----------+
  * |            +------------------------------->o        merge        |                                                           |
  *              |                                +----------o----------+       + - - - - - - - - - - - - - - - - - - - - - - - - +
  * |            |                                           |                    +--------------+                                  |
  *              |                             Option[String]|                  | |    events    |                                |
  * |            |                                           v                    +-------o------+                                  |
  *   +----------o----------+                     +----------o----------+       |         |                                       |
  * | | currentLastEventId  |                     |      getEvents      |                 |ServerSentEvent                          |
  *   +----------o----------+                     +----------o----------+       |         v                                       |
  * |            ^                                           |                    +-------o------+                                  |
  *              |               Source[ServerSentEvent, Any]|                  | | LastElement  x Future[Option[ServerSentEvent]]|
  * |            |                                           v                    +-------o------+                                  |
  *              |                                +----------o----------+  run  |         |                                       |
  * |            |                                |       handle        |-------          |ServerSentEvent                          |
  *              |                                +----------o----------+       |         v                                       |
  * |            |                                           |                    +-------o------+                                  |
  *              |       (Future[Option[ServerSentEvent]], A)|                  | |   handler    x A                              |
  * |            |                                           v                    +--------------+                                  |
  *              |                                +----------o----------+       + - - - - - - - - - - - - - - - - - - - - - - - - +
  * |            +--------------------------------o        unzip        |                                                           |
  *              Future[Option[ServerSentEvent]]  +----------o----------+
  * |                                                        |                                                                      |
  *                                                        A |
  * |                                                        |                                                                      |
  *                                                          v
  * + - - - - - - - - - - - - - - - - - - - - - - - - - - - -o- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
  *}}}
  */
object SseClient {

  def apply[A](
      uri: Uri,
      handler: Sink[ServerSentEvent, A],
      send: HttpRequest => Future[HttpResponse],
      lastEventId: Option[String] = None
  )(implicit ec: ExecutionContext, mat: Materializer): Source[A, NotUsed] = {

    // Flow[Option[String], (Future[Option[ServerSentEvent]], A), NotUsed]
    def getAndHandleEvents = {
      ???
    }

    // Flow[Future[Option[ServerSentEvent]], Option[String], NotUsed]
    def currentLastEventId =
      ???

    // Graph with shape SourceShape[A]
    ???
  }
}

object SseClientApp {

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    val config  = system.settings.config
    val address = config.getString("lyas.sse-server.address")
    val port    = config.getInt("lyas.sse-server.port")

    val client = SseClient(Uri(s"http://$address:$port"),
                           Sink.foreach(println),
                           Http().singleRequest(_),
                           Some("10"))
    client.runWith(Sink.ignore)

    Await.ready(system.whenTerminated, Duration.Inf)
  }
}
