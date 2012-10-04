package nl.rug {
package model {

import net.liftweb.mapper._

/**
	* Repository model maps to the database table Repository. Extending the 
	* LongkeyedMapper super class enables the model to have a long int as primary
	* key.
	* 
	* Extending IdPK lets the model auto generate an ID field, which is also auto 
	*	increment.
	*
	*	The one to many is for mapping this class to the revision model. It states
	* that the model its linked to uses a long to identify. (I GUESS)
	*/
class Repository extends LongKeyedMapper[Repository] with IdPK with OneToMany[Long, Repository]{
  
	// Returns the Repository object on the bottom of this class.
	def getSingleton = Repository
	
	object name extends MappedString(this, 255)
	{
		//Field cannot be null
    override def dbNotNull_? = true
  }

  object url extends MappedString(this, 255)
  {
		//Field cannot be null
    override def dbNotNull_? = true
  }

  object description extends MappedText(this)
  {
		//Field cannot be null
    override def dbNotNull_? = true
  }
	
	//This model has many revision objects. When it is deleted, also cascade the others.
	object revisions extends MappedOneToMany(Revision, Revision.repository) with Owned[Revision] with Cascade[Revision]

}

object Repository extends Repository with LongKeyedMetaMapper[Repository]

}
}