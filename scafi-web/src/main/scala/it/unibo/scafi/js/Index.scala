package it.unibo.scafi.js

import it.unibo.scafi.incarnations.BasicSimulationIncarnation
import it.unibo.scafi.incarnations.BasicSimulationIncarnation._
import org.scalajs.dom

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.timers.{SetIntervalHandle, clearInterval, setInterval}

/**
  * from the main body, scala js produce a javascript file.
  * it is an example of a ScaFi simulation transcompilated in javascript.
  */
@JSExportTopLevel("Index")
object Index {
  import org.scalajs.dom._

  class FooProgram extends AggregateProgram with StandardSensors {
    override def main(): Any = rep(Double.PositiveInfinity){ case g =>
      mux(sense[Boolean]("source")){ 0.0 }{
        minHoodPlus { nbr(g) + nbrRange() }
      }
    }
  }

  def appendPar(targetNode: dom.Node, text: String): Unit = {
    val parNode = document.createElement("p")
    parNode.textContent = text
    targetNode.appendChild(parNode)
  }

  var handle: Option[SetIntervalHandle] = None
  var net: NETWORK = _
  var program: CONTEXT => EXPORT = _

  @JSExport
  def main(args: Array[String]): Unit = {
    appendPar(document.body, "Hello Scala.js")

    val btn = document.createElement("button")
    btn.setAttribute("onClick", "switchSimulation()")
    btn.textContent = "Start simulation";
    document.body.appendChild(btn)

    println("Index.main !!!")

    val div = document.createElement("div")
    div.id = "netDiv"
    document.body.appendChild(div)

    val g: Graph = NetUtils.graph()
    Network.draw(g, DrawOptions("#netDiv"))

    val nodes = ArrayBuffer((0 to 100):_*)
    net = BasicSimulationIncarnation.simulatorFactory.simulator(
      idArray = nodes,
      nbrMap = mutable.Map(nodes.map(id => id->(id-3 to id+3+1).toSet.filter(x => x>=0 && x<100)):_*),
      nbrSensors = {
        case NBR_RANGE => { case (id,idn) => 1 }
      },
      localSensors = {
        case "source" => { case id => id == 10 || id == 50 || id == 70 }
      }
    )
    program = new FooProgram()
    import scalajs.js.timers._
  }

  @JSExportTopLevel("switchSimulation")
  def addClickedMessage(): Unit = handle match {
    case Some(h) => { clearInterval(h); handle = None }
    case None => handle = Some(setInterval(100) { println(net.exec(program)) })
  }
}
