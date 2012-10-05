package nl.rug {
package model {

import net.liftweb._
import util._
import Helpers._
import common._
import json._

import scala.xml.Node

/**
 * Piginode case class
 */
case class PigiNode(id: String, name: String, 
                address: String,
                port: Int,
								leader: Boolean)

/**
 * Piginode companion object
 */
object PigiNode {
  private implicit val formats =
    net.liftweb.json.DefaultFormats

  private var pigiNodes: List[PigiNode] = parse(data).extract[List[PigiNode]]

	private var listeners: List[PigiNode => Unit] = Nil

  def apply(in: JValue): Box[PigiNode] = Helpers.tryo{in.extract[PigiNode]}

  def unapply(id: String): Option[PigiNode] = PigiNode.find(id)

  def unapply(in: JValue): Option[PigiNode] = apply(in)

  /**
   * The default unapply method for the case class.
   * We needed to replicate it here because we
   * have overloaded unapply methods
   */
  def unapply(in: Any): Option[(String, String, 
                                String,
                                Int,
																Boolean)] = {
    in match {
      case pigiNode: PigiNode => Some((pigiNode.id, pigiNode.name, 
														pigiNode.address,
                            pigiNode.port,
														pigiNode.leader))
      case _ => None
    }
  }

  // Convert the node to xml
  implicit def toXml(pigiNode: PigiNode): Node = 
    <pigiNode>{Xml.toXml(pigiNode)}</pigiNode>

	 // Convert a sequence of nodes to xml
  implicit def toXml(pigiNodes: Seq[PigiNode]): Node = 
    <pigiNodes>{
      pigiNodes.map(toXml)
    }</pigiNodes>


  // Convert the node to JSON
  implicit def toJson(pigiNode: PigiNode): JValue = 
    Extraction.decompose(pigiNode)

  // Convert a sequence of nodes to json
  implicit def toJson(pigiNodes: Seq[PigiNode]): JValue = 
    Extraction.decompose(pigiNodes)

  //Get all pigninodes (convert from list to seq)
  def inventoryItems: Seq[PigiNode] = pigiNodes

  // The raw data
  private def data = 
"""[
  {"id": "1", "name": "PC1",
  "address": "10.0.0.100",
  "port": 2414,
	"leader": false,
  },
  {"id": "2", "name": "xServe",
  "address": "92.134.12.32",
  "port": 2414,
	"leader": false,
  },
  {"id": "3", "name": "Cass1",
  "address": "145.32.52.1",
  "port": 2414,
	"leader": false,
  },
  {"id": "4", "name": "Cass2",
  "address": "145.32.52.1",
  "port": 2415,
	"leader": true,
  },
]
"""

  // Find a node from the list by id
  def find(id: String): Box[PigiNode] = synchronized {
    pigiNodes.find(_.id == id)
  }

	// add a node to the list
  def add(pigiNode: PigiNode): PigiNode = {
    synchronized {
      pigiNodes = pigiNode :: pigiNodes.filterNot(_.id == pigiNode.id)
      updateListeners(pigiNode)
    }
  }

	// Get the leader node (maybe use ints and pick the highest)
  def getLeader(): Box[PigiNode] = {
    
		pigiNodes.find(_.leader == true)
    
  }

  // Delete a node and return it
  def delete(id: String): Box[PigiNode] = synchronized {
    var ret: Box[PigiNode] = Empty

    val Id = id // an upper case stable ID for pattern matching

    pigiNodes = pigiNodes.filter {
      case i@PigiNode(Id, _, _, _, _) => 
        ret = Full(i) // side effect
        false
      case _ => true
    }

    ret.map(updateListeners)
  }

  // Updates all listeners in the list (no clue which are the listeners)
  private def updateListeners(pigiNode: PigiNode): PigiNode = {
    synchronized {
      listeners.foreach(f => 
        Schedule.schedule(() => f(pigiNode), 0 seconds))

      listeners = Nil
    }
    pigiNode
  }

  // On change listener
  def onChange(f: PigiNode => Unit) {
    synchronized {
      // prepend the function to the list of listeners
      listeners ::= f
    }
  }
    
}

}
}