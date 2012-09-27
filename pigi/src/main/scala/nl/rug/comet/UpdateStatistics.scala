package nl.rug.comet

import net.liftweb.util.Schedule
import net.liftweb.util.Helpers._
import net.liftweb.http.CometActor
import net.liftweb.util._
import net.liftweb.http.js.JsCmds.SetHtml
import scala.xml.Text
import net.liftweb._
import scala.util.Random

case object Tick

class Entry(pname: String, purl: String, pcomplexity: Int) {
	
	var name = pname;
	var url = purl;
	var complexity = pcomplexity;
	
}

class UpdateStatistics extends CometActor {
  Schedule.schedule(this, Tick, 3 seconds)
	
	val r = new Random()
	
  def render = {
		// Fake data from the database
		val entries = List(
			new Entry("WebKit","svn://webkit.com/root", r.nextInt(40)),
			new Entry("wxWidgets","svn://wxWidgets.org/", r.nextInt(60)),
			new Entry("ReneZ","svn://assembla/sds", r.nextInt(30)),
			new Entry("Pigi","svn://pigid/hoi", r.nextInt(30))
		).zipWithIndex
	
		// Create random complexities
		entries.foreach(entry => {
			println(entry._1.name)
			entry._1.complexity = r.nextInt(40);
		})
		
		// prepare the image url to include
		//val image = ("https://chart.googleapis.com/chart?cht=p3&chd=t:"+entries(0)._1.complexity+","+
		//																																entries(1)._1.complexity+","+
		//																																entries(2)._1.complexity+
		//																																"&chs=250x100&chl=" +
		//																																entries(0)._1.name+"|"+
		//																																entries(1)._1.name+"|"+
		//																																entries(2)._1.name);
		
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
		var imageurl = "https://chart.googleapis.com/chart?cht=p3&chd=t:";
		
		entries.foreach(x => {
			imageurl += x._1.complexity + ","
			}
		)
		
		imageurl= imageurl.dropRight(1);
		
		imageurl +="&chs=250x100&chl=";
		
		entries.foreach(x => {
			imageurl += x._1.name + "|"
			}
		)
		return imageurl.dropRight(1);
	}

  override def highPriority = {
    case Tick =>
			println("tick");
			reRender(true)
			partialUpdate(SetHtml("time", Text(timeNow.toString)))
      Schedule.schedule(this, Tick, 3 seconds)
  }
}