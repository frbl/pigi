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

/**
	Class to manage comet communication with the Front end
 */
class UpdateStatistics extends CometActor {
	
	// Use the statistics class as data provisioner
	private var statistics: Statistics = new Statistics;

	// Schedule the first tick after 1 second
	Schedule.schedule(this, Tick, 1 seconds) 
	
  def render = {
		val entries = statistics.entries

		".instance" #> entries.map { entry => {
				".instance [onclick]" #> ("document.location='statistics/repository/"+entry.name+"';") &
				".name *" #> entry.name &
	      ".url *"  #> entry.url &
				".revision *" #> entry.lastRevision &
				".complexity *" #> entry.averageComplexity
	    }
	  } &
		"#time *" #> timeNow.toString &
		"#chart [src]" #> statistics.image
	}

	/**
		This function is called whenever the scheduler 'rings'
	 */
  override def lowPriority = {
	
    case Tick =>

			println("[Debug] Recieved Tick!")
			
			reRender(true)
			
			if(statistics == null) statistics = new Statistics
			
			statistics.update()

			// Update the data in the javascript function, and redraw
			partialUpdate(encode() & JsRaw("drawMe();"))

      Schedule.schedule(this, Tick, 5 seconds)

  }

	/**
		Function to call the javascript addComplexity function
		*/
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