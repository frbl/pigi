Cassandra datamodel
===================
This shows a possible solution for the cassandra datamodel. Feel free to add new versions. Place new versions above the 
previous version.

Description
-----------
The cassandra datamodel must cover the following:
* The application can have multiple repositories
* A repository has a name and a URL
* Each of the repositories can have multiple revisions
* For each of the revisions a revision number and a complexity of that revision should be provided.
* A revision number/complexity are calculated for a file in the repository
  
If possible the datamodel should cover the following:
* The combination of file, revision and complexity is unique

Datamodel v0.1.1
----------------

There are two options we can try.   
1. The SuperColumns etc... which will be DataModel v0.2  
2. The relational kind, which will be DataModel v0.1 or the one below  

---
		Repositories :{ // Column Family
			https://subversion.assembla.com/svn/ReneZ/ : { //row key - svn url
			Name: ReneZ
			Description: Spelletje
			}
		}

		Files : { // Column Family
			Controller/Controller.java : { //row key - FilePath + Name
			//Columns
			1 : { // Columnname (Revision)
				//Columnvalues
				Repository: https://subversion.assembla.com/svn/ReneZ/,  //row key into Repositories
				Complexity: 2
			}
			3 : {
				Repository: https://subversion.assembla.com/svn/ReneZ/,
				Complexity: 4
			}
			16 : {
				Repository: https://subversion.assembla.com/svn/ReneZ/,
				Complexity: 9
			}
		}
	}


Datamodel v0.2
--------------
Im not sure if this model is allowed, but lets give it a try :)

Legend:  
* KeySpace: ComplexityAnalysis  
* Column family: Repository  

---
      {
        "ComplexityAnalysis":{
          "Repository":{
            "url": "<urloftherepository>",
            "name": "<nameoftherepository>",
            "description": "<descriptionoftherepository>",
            "revision":{
              "file": "<filename>",
              "revisionnumer": 1,
              "complexity": 1
            },
            "revision":{
              "file": "<filename>",
              "revisionnumer": 2,
              "complexity": 1
            }
          }
        }
      }
      
Datamodel v0.1
--------------
Relational model type.

Legend:  
* KeySpace: ComplexityAnalysis  
* Column family: Repository, Revision 

---
      {
      "ComplexityAnalysis":{
        "Repository":{
          "url": "<urloftherepository>",
          "name": "<nameoftherepository>",
          "description": "<descriptionoftherepository>"
          },
        "revision":{
          "url": "<url of the repository>",
          "file": "<filename>",
          "revisionnumer": 1,
          "complexity": 1
          }
        }
      }
      
Datamodel v0.x
--------------
<comments>

Legend:  
* KeySpace:   
* Column family:   

---
      <json overview of the datamodel>
    