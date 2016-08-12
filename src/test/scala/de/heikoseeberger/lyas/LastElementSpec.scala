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

import akka.stream.scaladsl.{ Keep, Sink, Source }
import akka.stream.testkit.scaladsl.TestSink

class LastElementSpec extends BaseSpec {

  "A stream via LastElement" should {
    "materialize to the last element emitted by a finite nonempty successful source" in {
      val (lastElement, probe) = Source(Vector(1, 2, 3))
        .viaMat(new LastElement())(Keep.right)
        .toMat(TestSink.probe)(Keep.both)
        .run()
      probe.request(3).expectNext(1, 2, 3).expectComplete()
      lastElement.map(_ shouldBe Some(3))
    }

    "materialize to `None` for an empty successful source" in {
      val (lastElement, probe) = Source(Vector.empty[Int])
        .viaMat(new LastElement())(Keep.right)
        .toMat(TestSink.probe)(Keep.both)
        .run()
      probe.request(3).expectComplete()
      lastElement.map(_ shouldBe None)
    }

    "materialize to the last element emitted by a source before it failed" in {
      import system.dispatcher
      val (lastElement, lastEmitted) = Source
        .fromIterator(
          () =>
            Iterator.iterate(1)(n =>
              if (n >= 3) sys.error("FAILURE") else n + 1)
        )
        .viaMat(new LastElement())(Keep.right)
        .toMat(Sink.fold[Option[Int], Int](None)((_, n) => Some(n)))(Keep.both)
        .run()
      lastElement.zip(lastEmitted).map { case (l1, l2) => l1 shouldBe l2 }
    }
  }
}
