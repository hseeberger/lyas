/*
 * Copyright 2017 Heiko Seeberger
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
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Keep, Sink, Source }

/**
  * Prints "Learn you Akka Streams for great good!" in fancy ways.
  */
object Main {

  def main(args: Array[String]): Unit = {
    println("-- Main started --")

    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    Source
      .repeat("Learn you Akka Streams for great good!")
      .take(7)
      .zip(Source.fromIterator(() => Iterator.from(0)))
      .map {
        case (s, n) =>
          val i = " " * n
          f"$i$s%n"
      }
      .toMat(Sink.foreach(print))(Keep.right)
      .run()
      .onComplete(_ => system.terminate())
  }
}
