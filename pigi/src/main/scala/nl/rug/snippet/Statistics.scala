package nl.rug.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import nl.rug.lib._
import Helpers._
import _root_.java.util.Date

class Statistics{
	
	def update(count: Int) = {
		
		"time *" #> count
	}
  // bind the date into the element with id "time"

  /*
   lazy val date: Date = DependencyFactory.time.vend // create the date via factory

   def howdy = "#time *" #> date.toString
   */
}
