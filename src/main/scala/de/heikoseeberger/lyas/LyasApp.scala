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
import akka.stream.scaladsl.{ Keep, Sink, Source }
import akka.stream.{ ActorMaterializer, ThrottleMode }
import scala.concurrent.Await
import scala.concurrent.duration.{ Duration, DurationInt }

object LyasApp {

  private final val Title =
    """| _                                          _   _   _          ___ _
       || |   ___ __ _ _ _ _ _    _  _ ___ _  _    /_\ | |_| |____ _  / __| |_ _ _ ___ __ _ _ __  ___
       || |__/ -_) _` | '_| ' \  | || / _ \ || |  / _ \| / / / / _` | \__ \  _| '_/ -_) _` | '  \(_-<
       ||____\___\__,_|_| |_||_|  \_, \___/\_,_| /_/ \_\_\_\_\_\__,_| |___/\__|_| \___\__,_|_|_|_/__/
       |                          |__/
       |  __                             _      ___              _ _
       | / _|___ _ _   __ _ _ _ ___ __ _| |_   / __|___  ___  __| | |
       ||  _/ _ \ '_| / _` | '_/ -_) _` |  _| | (_ / _ \/ _ \/ _` |_|
       ||_| \___/_|   \__, |_| \___\__,_|\__|  \___\___/\___/\__,_(_)
       |              |___/
       |""".stripMargin

  def main(args: Array[String]): Unit = {
    println(Title)

    implicit val system = ActorSystem()
    implicit val mat    = ActorMaterializer()
    import system.dispatcher

    val lyas = Source
      .repeat("Learn you Akka Streams for great Good!")
      .zip(Source.fromIterator(() => Iterator.from(0)))
      .take(7)
      .mapConcat {
        case (s, n) =>
          val i = " " * n
          f"$i$s%n"
      }
      .throttle(42, 500.millis, 1, ThrottleMode.Shaping)
      .toMat(Sink.foreach(print))(Keep.right)

    val done = lyas.run()
    done.onComplete(_ => system.terminate())

    Await.ready(system.whenTerminated, Duration.Inf)
  }
}
