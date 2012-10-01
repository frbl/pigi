package nl.rug.comet

import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.util._
import net.liftweb.http.js.JsCmds.SetHtml
import scala.xml.Text
import net.liftweb._
import scala.util.Random
import net.liftweb.http.js._
import JsCmds._
import JE._

case object Tick

class Entry(pname: String, purl: String, pcomplexity: Int) {
	
	var name = pname;
	var url = purl;
	var complexity = pcomplexity;
	
}

class UpdateStatistics extends CometActor {
	var j = 0;
  Schedule.schedule(this, Tick, 1 seconds)
	
	val r = new Random()
	
  def render = {
		// Fake data from the database
		val entries = List(
			new Entry("WebKit","svn://webkit.com/root", r.nextInt(40)),
			new Entry("wxWidgets","svn://wxWidgets.org/", r.nextInt(60)),
			new Entry("ReneZ","svn://assembla/sds", r.nextInt(30)),
			new Entry("Pigi","svn://pigid/hoi", r.nextInt(30)),
			new Entry("Pigi","svn://test/hoi", r.nextInt(30))
		).zipWithIndex
	
		// Create random complexities
		entries.foreach(entry => {
			entry._1.complexity = r.nextInt(40);
		})
		
		partialUpdate(Call("addComplexity", j, entries(0)._1.complexity));
		
		val image = prepareImage(entries);
		
		".instance *" #> entries.map { case(entry, id) => {
        ".index *" #> id &
				".name *" #> entry.name &
        ".url *"  #> entry.url &
				".complexity *" #> entry.complexity
      }
    } &
		"#time *" #> timeNow.toString &
		"#chart [src]" #> image
	}
	
	def prepareImage(entries:List[(Entry, Int)]):String = {
		var imageurl = "https://chart.googleapis.com/chart?cht=p3&amp;chd=t:";
		
		entries.foreach(x => {
			imageurl += x._1.complexity + ","
			}
		)
		
		imageurl= imageurl.dropRight(1);
		
		imageurl +="&amp;chs=250x100&amp;chl=";
		
		entries.foreach(x => {
			imageurl += x._1.name + "|"
			}
		)
		return imageurl.dropRight(1);
	}

  override def lowPriority = {
    case Tick =>
			reRender(true)
			j+=1;
			
      Schedule.schedule(this, Tick, 1 seconds)
  }
}