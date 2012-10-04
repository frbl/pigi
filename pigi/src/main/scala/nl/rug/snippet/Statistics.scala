package nl.rug.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import scala.util.Random
import nl.rug.model.Repository
import nl.rug.model.Revision

class Entry() {
	
	var id: Int = 0
	var name: String = "Name";
	var url: String = "Url";
	var complexity: Int = 0;
	var lastRevision: Int = 0;

}

class Statistics {
	
	private val r = new Random()
	
	// Fake data from the database
	var entries = List[Entry]()
	
	var average = 0;
	
	var revision = 0;
	
	var image = prepareImage(entries);
	
	def update() = {
		
		val repositories: List[Repository] = Repository.findAll;
		
		// Empty the list by creating a new object?
		entries = List[Entry]();
		
		average = 0;
		
		var tempComplexity: Int = 0;
		
		var revisions = List[Revision]();
		
		repositories.foreach(repository=> {
			// Create random complexities
			var entry = new Entry
			entry.id = repository.id.toInt;
			entry.name = repository.name;
			entry.url = repository.url;
			entry.complexity = r.nextInt(40);
			
			// Check if it has more revisions
			revisions = repository.revisions.toList;
			if(revisions.length > 0) {
				entry.complexity = revisions.last.averageComplexity;
				entry.lastRevision = revisions.last.revisionNumber.toInt;
			}
			
			entries ::= entry;
			average += (entry.complexity / repositories.length);
			
		})
		
		
		revision += 1;
		
	  image = prepareImage(entries);
	
	}
	

	def prepareImage(entries:List[Entry]):String = {
		var imageurl = "https://chart.googleapis.com/chart?cht=p3&amp;chd=t:";
		
		entries.foreach(x => {
			imageurl += x.complexity + ","
			}
		)
		
		imageurl= imageurl.dropRight(1);
		
		imageurl +="&amp;chs=250x100&amp;chl=";
		
		entries.foreach(x => {
			imageurl += x.name + "|"
			}
		)
		return imageurl.dropRight(1);
	}
	
}
