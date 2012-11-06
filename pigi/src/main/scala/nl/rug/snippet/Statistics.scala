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
	
	private var _image = prepareImage(entries)
	def image = _image
	
	private val averageComplexities = new AverageComplexities
	
	// Update variables for the first time
	update()
	
	def update() = {
		
		// Get the repositories from the database
    val repositories: List[RepositoryCassandra] = averageComplexities.getRepositories
		
		// Empty the list by creating a new object
		_entries = List[Entry]();
		
		repositories.foreach(repository=> {

			// Get the average complexities from the db
			val complexities = averageComplexities.getAverageComplexities(repository.url, 0, 100)
			
			var revisions: List[Long] = List[Long]();
			
			// Get the values from all entries in the complexities list
			while(complexities.hasNext) {
			
				revisions ::= complexities.next.getValue
			
			}
			
			//Revisions are added at the beginning for some reason
			revisions = revisions.reverse
			
			// Create entry and set the good values
			var entry = new Entry
			entry.name = repository.name;
			entry.url = repository.url;
			entry.lastRevision = revisions.length;
			entry.revisions = revisions;
			entry.averageComplexity = getAverage(revisions);
			
			// Add the entry to the list
			_entries ::= entry;
			
		})
		
	  _image = prepareImage(entries);
	
	}
	
	/**
		Simple function to retrieve the average over a list of longs
	 */
	def getAverage(revisions :List[Long]) :Long = {
		
		var avg :Long = 0;
		
		var n :Long = 0;
		
		revisions.foreach(r => {
			
			avg += r
			
		  n +=1
		
		})
		
		// There are no revisions
		if(n == 0) {
			avg = 0
			return avg
		}
		return avg/n;
		
	}
	
	/**
		Function to prepare an image for the (old) googlechart api. It uses the 
		average complexities calculated in the update function.
	*/
	def prepareImage(entries:List[Entry]):String = {
		
		var imageurl = "https://chart.googleapis.com/chart?" +
												"cht=p&amp;chco=7EB5D6&amp;chxs=0,274257,11.5&amp;chd=t:";
		
		// Some data needs to be appended in this url. store this here
		var append = "";
		
		// Add each entry to the url
		entries.foreach(entry => {
		
			imageurl += entry.averageComplexity + ","
		
			append += entry.name + "|"
		
			}
		
		)
		
		imageurl= imageurl.dropRight(1);
		
		imageurl +="&amp;chs=340x225&amp;chl=";
		
		imageurl += append
		
		return imageurl.dropRight(1);
		
	}
	
}
