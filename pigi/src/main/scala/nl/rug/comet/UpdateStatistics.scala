package nl.rug.comet

import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import nl.rug.snippet.Statistics
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.js.JsCmds._
import _root_.net.liftweb.util._
import Helpers._
import scala.actors.Actor._
import _root_.scala.xml._
import scala.xml.Text


case object Tick

class UpdateStatistics extends CometActor {
  Schedule.schedule(this, Tick, 1 seconds)
	
	var count = 0;
	
  def render = {
		println("render")
		"#time *" #> timeNow.toString
	}

  override def highPriority = {
    case Tick =>
			println("tick");
			count += 1;
			
			partialUpdate(SetHtml("time", Text(timeNow.toString)))
      Schedule.schedule(this, Tick, 1 seconds)
  }
}