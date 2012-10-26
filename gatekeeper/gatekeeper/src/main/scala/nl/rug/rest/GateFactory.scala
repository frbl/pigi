package nl.rug {
package rest {

import net.liftweb._
import common._
import http._
import rest._
import json._
import scala.xml._

import nl.rug.model._


object GateFactory extends RestHelper {
	serve {
		//case Req("gate" :: _, "xml", GetRequest) => <b>10.0.0.100</b> 
		//case Req("gate" :: _, "json", GetRequest) => JString("10.0.0.1")
		//case "gate" :: _ XmlGet _ => <ip>10.0.0.1</ip> 
		//case "gate" :: _ JsonGet _ => JString(".0.0.1")
		case JsonGet("gate" :: _, _) => JString("ip: 10.0.0.1")
		case XmlGet("gate" :: _, _) => <ip>10.0.0.1</ip> 
		
	}
	
	serve {
		case "entrypoint" :: repository :: Nil JsonGet _ =>
	  	for {
	    	// find the item, and if it's not found,
	    	// return a nice message for the 404
	    	node <- PigiNode.getNodeInRepository(repository)
			} yield node: JValue
	}
	
	serve {
		case "find" :: "node" :: repository :: Nil XmlGet _ =>
			for {
				node <- PigiNode.find(repository) ?~ "Node Not Found"
	    } yield node: Node
	
		case "add" :: "node" :: ip :: Nil XmlGet _ => <ip>{ip}</ip>
		  //for {
				//println(ip)
		    //node <- PigiNode.find(nodeId) ?~ "Node Not Found"
		  //} yield node: Node
	  }
}

}
}