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
import akka.stream.{ Materializer, SourceShape }
import de.heikoseeberger.akkasse.MediaTypes.`text/event-stream`
import de.heikoseeberger.akkasse.ServerSentEvent
import de.heikoseeberger.akkasse.headers.`Last-Event-ID`
import scala.concurrent.{ ExecutionContext, Future }

/**
  * Obtains a continuous source of [[ServerSentEvent]]s from the given URI and
  * streams it into the given handler. Once a source is completed, a next one is
  * obtained thereby sending a Last-Evend-ID header if there is a last event id.
  */
object SseClient {

  def apply[A](uri: Uri,
               handler: Sink[ServerSentEvent, A],
               send: HttpRequest => Future[HttpResponse],
               lastEventId: Option[String] = None): Source[A, NotUsed] = {
    ???
  }
}
