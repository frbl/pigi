package nl.rug.snippet

import _root_.scala.xml.{NodeSeq, Text}
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import scala.util.Random
import nl.rug.model.RepositoryCassandra
import nl.rug.model.Repository
import nl.rug.model.Revision
import nl.rug.model.AverageComplexities

class Entry() {
	
	var id: Int = 0
	var name: String = "Name";
	var url: String = "Url";
	var averageComplexity: Long = 0;
	var lastRevision: Int = 0;
	var revisions: List[Long] = List[Long]();

}

class Statistics {

	// place to store the entries from the database
	private var _entries = List[Entry]()
	def entries = _entries
	
	var image = prepareImage(entries)
	
	val averageComplexities = new AverageComplexities
	
	def update() = {
    
    val repositories: List[RepositoryCassandra] = averageComplexities.getRepositories
		
		// Empty the list by creating a new object?
		_entries = List[Entry]();
		
		repositories.foreach(repository=> {
			
			val complexities = averageComplexities.getAverageComplexities(repository.url, 0, 100)
			
			var revisions: List[Long] = List[Long]();
			
			while(complexities.hasNext) {
				revisions ::= complexities.next.getValue
			}
			
			//Revisions are added at the beginning for some reason
			revisions = revisions.reverse
			
			// Create random complexities
			var entry = new Entry
			entry.id = 0;
			entry.name = repository.name;
			entry.url = repository.url;
			
			
			// Check if it has more revisions
			//revisions = repository.revisions.toList;
			//if(revisions.length > 0) {
			//	entry.complexity = revisions.last.averageComplexity;
			//	entry.lastRevision = revisions.last.revisionNumber.toInt;
			//}
			entry.lastRevision = revisions.length;
			_entries ::= entry;
			entry.revisions = revisions;
			
			entry.averageComplexity = getAverage(revisions);
			
		})
		
	  image = prepareImage(entries);
	
	}
	
	def getAverage(revisions :List[Long]) :Long = {
		var avg :Long = 0;
		var n :Long = 0;
		revisions.foreach(r => {
			avg += r
		  n +=1
		})
		return avg/n;
	}
	
	def prepareImage(entries:List[Entry]):String = {
		var imageurl = "https://chart.googleapis.com/chart?cht=p3&amp;chd=t:";
		var append = "";
		entries.foreach(entry => {
			imageurl += entry.averageComplexity + ","
			append += entry.name + "|"
			}
		)
		
		imageurl= imageurl.dropRight(1);
		
		imageurl +="&amp;chs=250x100&amp;chl=";
		
		imageurl += append
		
		return imageurl.dropRight(1);
		
	}
	
}
