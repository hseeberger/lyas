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

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ HttpRequest, Uri }
import akka.stream.scaladsl.Sink
import de.heikoseeberger.akkasse.ServerSentEvent

class SseClientSpec extends BaseSpec {

  "SseClient" should {
    "get and handle an event stream" in {
      val address    = "localhost"
      val port       = 12346
      val nrOfEvents = 7
      val nrOfProbes = 10
      SseServer(address, port, 100, nrOfEvents)

      val handler = {
        def append(ds: Vector[String], e: ServerSentEvent) = ds :+ e.data
        Sink.fold(Vector.empty[String])(append)
      }
      SseClient(Uri(s"http://$address:$port"), handler, send, Some("10"))
        .take(nrOfProbes)
        .mapAsync(1)(identity)
        .runFold(Vector.empty[String])(_ ++ _)
        .map(
          _ shouldBe 1
            .to(nrOfEvents * nrOfProbes)
            .map(_ + 10)
            .map(n => s"SSE-$n")
        )
    }
  }

  private def send(request: HttpRequest) = Http().singleRequest(request)
}
