package nl.rug.comet

import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.util._
import net.liftweb.http.js.JsCmds.SetHtml
import nl.rug.snippet._
import net.liftweb._
import common.Full
import net.liftweb.http.js._
import JsCmds._
import JE._

case object Tick


class UpdateStatistics extends CometActor {

	var statistics: Statistics = new Statistics;
	
	statistics.update()
	
	Schedule.schedule(this, Tick, 1 seconds) 
	
  def render = {
		val entries = statistics.entries

		".instance" #> entries.map { entry => {
				".instance [onclick]" #> ("document.location='statistics/repository/"+entry.id+"';") &
				".name *" #> entry.name &
	      ".url *"  #> entry.url &
				".revision *" #> entry.lastRevision &
				".complexity *" #> entry.averageComplexity
	    }
	  } &
		"#time *" #> timeNow.toString &
		"#chart [src]" #> statistics.image
	}

  override def lowPriority = {
	
    case Tick =>

			println("[Debug] Recieved Tick!")
			
			reRender(true)
			
			statistics.update()

			partialUpdate(encode() & JsRaw("drawMe();"))

      Schedule.schedule(this, Tick, 5 seconds)

  }

	private def encode(): JsCmd = {
		
		val entries = statistics.entries
		
		var jsRaw :JsCmd = JsRaw("");
		
		entries.foreach { entry => {
			
			var i = 1;
			
			entry.revisions.foreach { revision => {
				
				jsRaw = jsRaw & JsRaw("addComplexity(\""+entry.url+"\", " + i+ ", " + revision +");")
				
				i+=1
				
			}}
			
		}}
		
		return jsRaw
		
	}

}