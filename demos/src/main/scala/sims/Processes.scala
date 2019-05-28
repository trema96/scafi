/*
 * Copyright (C) 2016-2017, Roberto Casadei, Mirko Viroli, and contributors.
 * See the LICENCE.txt file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package sims

import it.unibo.scafi.incarnations.BasicSimulationIncarnation._
import it.unibo.scafi.simulation.frontend.{Launcher, Settings}

object ProcessesMain extends Launcher {
  Settings.Sim_ProgramClass = "sims.Proc1"
  Settings.ShowConfigPanel = false
  Settings.Sim_NbrRadius = 0.25
  Settings.Sim_NumNodes = 100
  launch()
}

/**
  * This program emulates some features of 'aggregate processes' with plain HFC.
  * We define two one-shot gradient computations which have:
  * 1) limited spatial extension (domain)
  * 2) limited temporal extension (life)
  * The second gradient also restricts the shape so as not to superimpose with the first one.
  * Notes:
  * - The process lifecycle is managed through a branch
  * - The second process doesn't even start if launched from the domain of the first process.
  * - There are no 'fence nodes': the separation between internal and external nodes is sharp
  */
class Proc1 extends AggregateProgram with SensorDefinitions with CustomSpawn with Gradients
  with TimeUtils with StateManagement {

  override def main() = {
    val inf = Double.PositiveInfinity
    val in = "in"
    val out = "out"

    def limitedGradient(src: Boolean, size: Double, time: Int, start: => Boolean = false, iff: => Boolean = true) =
      rep((out,inf,time,true)){ case (status,g,t,notYetStarted) => {
      branch(start | (src | excludingSelf.anyHood(nbr{status}==in
        & nbr{g}+nbrRange<size))
        & iff
        && (t>0 || notYetStarted)){
        (in,classicGradient(src),T(time),false)
      }{ (out, Double.PositiveInfinity, time, notYetStarted) }
    }}

    val g1 = limitedGradient(sense1, 20, 500, start = captureChange(sense1) && sense1)

    val g2 = limitedGradient(sense2, 30, 500, start = captureChange(sense2) && sense2, iff = g1._2!=0)

    (g1._1,g2._1)
  }
}

class MultiGradient extends AggregateProgram with SensorDefinitions with CustomSpawn with Gradients
  with TimeUtils with StateManagement {

  def isSrc = sense1 || sense2 || sense3 || sense4

  import SpawnInterface._

  override def main() = {
    /*
    def h(src: ID, limit: Double, g: Double): (Double,Status) =
      (g, if(src==mid && !isSrc) Terminated else if(g>limit) External else Bubble)

    spawn[ID,Double,Double](srcId => limit => handleTermination(h(srcId,limit,classicGradient(srcId==mid))),
      params = if(isSrc) Set(mid) else Set.empty,
      args = 20.0)
      */
    val maxExtension = 20.0

    sspawn[ID,Double,Double](srcId => limit => classicGradient(srcId==mid) match {
      case g if srcId==mid && !isSrc => (g, Terminated)
      case g if g>limit => (g, External)
      case g => (g, Output)
    },
      params = if(isSrc) Set(mid) else Set.empty,
      args = maxExtension)
  }

  implicit class RichType[T](value: T){
    def map[V](f: T=>V): V = f(value)
  }
}

class ReplGossip extends AggregateProgram with SensorDefinitions with CustomSpawn with ReplicatedGossip with Gradients {

  def isSrc = sense1 || sense2 || sense3 || sense4

  import SpawnInterface._
  import scala.concurrent.duration._

  override def main() = {
    replicated[Boolean,Double](classicGradient(_))(isSrc, (5 seconds).toNanos, 3)
  }
}