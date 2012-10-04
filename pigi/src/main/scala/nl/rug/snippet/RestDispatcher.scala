package nl.rug{
	package snippet{

		import net.liftweb.http.rest.RestHelper

		object RestDispatcher extends RestHelper {
	
			serve {
				
				case "statistics" :: "repository" :: _ Get _ => <b>Test Static</b>
				
			}
	
		}
		
	}
	
}