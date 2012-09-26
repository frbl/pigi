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
  Schedule.schedule(this, Tick, 1 seconds)
	
	val r = new Random()
	
	var entries = List(
		new Entry("WebKit","svn://webkit.com/root", 3),
		new Entry("wxWidgets","svn://wxWidgets.org/", 8),
		new Entry("ReneZ","svn://assembla/sds", 92)
	).zipWithIndex
	
  def render = {
	
		val image = ("https://chart.googleapis.com/chart?cht=p3&chd=t:"+r.nextInt(40)+","+r.nextInt(60)+","+r.nextInt(30)+"&chs=250x100&chl=WebKit|wxWidgets|ReneZ");
		
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

  override def highPriority = {
    case Tick =>
			println("tick");
			reRender(true)
			partialUpdate(SetHtml("time", Text(timeNow.toString)))
      Schedule.schedule(this, Tick, 1 seconds)
  }
}