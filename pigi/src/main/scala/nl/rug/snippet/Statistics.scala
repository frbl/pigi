package nl.rug.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import scala.util.Random
import nl.rug.model.Repository

class Entry(pname: String, purl: String, pcomplexity: Int) {

	var name = pname;
	var url = purl;
	var complexity = pcomplexity;

}

class Statistics {
	
	private val r = new Random()
	
	// Fake data from the database
	var entries = List[Entry]().zipWithIndex
	
	var average = 0;
	
	var revision = 0;
	
	var image = prepareImage(entries);
	
	def update() = {
		
		val repositories: List[Repository] = Repository.findAll
		
		// Empty the list by creating a new object?
		entries = List[Entry]().zipWithIndex
		
		average = 0;
		
		var tempComp: Int = 0;
		
		var index: Int = repositories.length;
		
		repositories.foreach(repository=> {
			// Create random complexities
			tempComp = r.nextInt(40);
			entries ::= (new Entry(repository.name, repository.url, tempComp),index)
			average += (tempComp / repositories.length)
			index -= 1
		})
			
		revision += 1;
		
	  image = prepareImage(entries);
	
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
	
}
