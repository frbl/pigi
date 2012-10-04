package nl.rug {
package model {

import net.liftweb.mapper._

/**
	* Revision model maps to the database table Revision. Extending the 
	* LongkeyedMapper super class enables the model to have a long int as primary
	* key.
	* 
	* Extending IdPK lets the model auto generate an ID field, which is also auto 
	*	increment. Hence, no need to perform def primaryKeyField = id.
	*/
class Revision extends LongKeyedMapper[Revision] with IdPK {
  
	def getSingleton = Revision
	
  object revisionNumber extends MappedLong(this)
	{
		//Field cannot be null
    override def dbNotNull_? = true
	}

  object averageComplexity extends MappedInt(this)

	object repository extends MappedLongForeignKey(this, Repository)

}

object Revision extends Revision with LongKeyedMetaMapper[Revision]

}
}