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
import akka.http.scaladsl.model.Uri
import akka.stream.scaladsl.Sink
import de.heikoseeberger.akkasse.ServerSentEvent

class SseClientSpec extends BaseSpec {

  private final val Text = "Learn you Akka Streams for great Good!"

  "SseClient" should {
    "get and handle an event stream" in {
      val address    = "localhost"
      val port       = 12346
      val nrOfProbes = 10
      val expected   = Text
      SseServer(address, port, 100)

      val handler = {
        def append(ds: String, e: ServerSentEvent) = ds + e.data.get
        Sink.fold("")(append)
      }
      SseClient(Uri(s"http://$address:$port"),
                handler,
                Http().singleRequest(_))
        .take(nrOfProbes)
        .mapAsync(1)(identity)
        .runFold("")(_ + _)
        .map(_ shouldBe expected)
    }
  }
}
