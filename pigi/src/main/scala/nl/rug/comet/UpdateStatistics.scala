package nl.rug.comet

import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.util._
import net.liftweb.http.js.JsCmds.SetHtml
import nl.rug.snippet._
import net.liftweb._
import net.liftweb.http.js._
import JsCmds._
import JE._

case object Tick


class UpdateStatistics extends CometActor {

  Schedule.schedule(this, Tick, 3 seconds) 
	
	var statistics: Statistics = new Statistics;
	
	statistics.update()
	
  def render = {
		val entries = statistics.entries
	
		".instance" #> entries.map { entry => {
				".instance [onclick]" #> ("document.location='statistics/repository/"+entry.id+"';") &
	      ".index *" #> entry.id &
				".name *" #> entry.name &
	      ".url *"  #> entry.url &
				".revision *" #> entry.lastRevision &
				".complexity *" #> entry.complexity
	    }
	  } &
		"#time *" #> timeNow.toString &
		"#chart [src]" #> statistics.image
	}
	
  override def lowPriority = {
    case Tick =>
			println("Recieved Tick!")
			reRender(true)
			
			statistics.update()
			
			partialUpdate(Call("addComplexity", statistics.revision, statistics.average));
      Schedule.schedule(this, Tick, 3 seconds)
  }
}