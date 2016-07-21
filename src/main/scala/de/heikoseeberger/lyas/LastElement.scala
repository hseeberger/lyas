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

import akka.stream.stage.{
  GraphStageLogic,
  GraphStageWithMaterializedValue,
  InHandler,
  OutHandler
}
import akka.stream.{ Attributes, FlowShape, Inlet, Outlet }
import scala.concurrent.{ Future, Promise }

/**
  * This stage materializes to the last element pushed before upstream
  * completion, if any, thereby recovering from any failure. Pushed elements are
  * just passed along.
  */
final class LastElement[A]
    extends GraphStageWithMaterializedValue[FlowShape[A, A], Future[Option[A]]] {

  override val shape = ???

  override def createLogicAndMaterializedValue(attributes: Attributes) = ???
}
