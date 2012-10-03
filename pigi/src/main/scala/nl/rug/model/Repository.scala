package nl.rug {
package model {

import net.liftweb.mapper._

class Repository extends KeyedMapper[String, Repository] {
  def getSingleton = Repository
  
  def primaryKeyField = url

  object url extends MappedStringIndex(this, 140)
  {
    override def writePermission_? = true   // if u want to set it via your code, keep this true
    override def dbAutogenerated_? = false
    override def dbNotNull_? = true
    override def dbColumnName="url"
  }

  object name extends MappedString(this, 140)

  object description extends MappedText(this)

}

object Repository extends Repository with KeyedMetaMapper[String, Repository]

}
}