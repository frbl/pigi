package nl.rug.comet

import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.util._
import net.liftweb.http.js.JsCmds.SetHtml
import scala.xml.Text
import net.liftweb._

case object Tick

class UpdateStatistics extends CometActor {
  Schedule.schedule(this, Tick, 1 seconds)
	
  def render = {
		println("render")
		"#time *" #> timeNow.toString
	}

  override def highPriority = {
    case Tick =>
			println("tick");
			
			partialUpdate(SetHtml("time", Text(timeNow.toString)))
      Schedule.schedule(this, Tick, 1 seconds)
  }
}