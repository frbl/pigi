package nl.rug {
package model {

import _root_.net.liftweb.mapper._
import _root_.net.liftweb.util._
import _root_.net.liftweb.common._
import net.liftweb.sitemap.Loc.LocGroup
import net.liftweb.sitemap.Loc.LocParam
import net.liftweb.sitemap.Loc.Template
import net.liftweb.sitemap.Loc.Hidden

/**
 * The singleton that has methods for accessing the database
 */
object User extends User with MetaMegaProtoUser[User] {
  override def dbTableName = "users" // define the DB table name
  override def screenWrap = Full(<lift:surround with="default" at="content">
			       <lift:bind /></lift:surround>)
  // define the order fields will appear in forms and output
  override def fieldOrder = List(id, firstName, lastName, email,
  locale, timezone, password, textArea)

  // comment this line out to require email validations
  override def skipEmailValidation = true
	
	override def logoutMenuLocParams = LocGroup("user") :: super.logoutMenuLocParams
	override def editUserMenuLocParams = LocGroup("user") :: super.editUserMenuLocParams
	override def changePasswordMenuLocParams = LocGroup("user") :: super.changePasswordMenuLocParams

	override def loginMenuLocParams = LocGroup("user") :: super.loginMenuLocParams
	override def createUserMenuLocParams = LocGroup("user") :: super.createUserMenuLocParams
}

/**
 * An O-R mapped "User" class that includes first name, last name, password and we add a "Personal Essay" to it
 */
class User extends MegaProtoUser[User] {
  def getSingleton = User // what's the "meta" server

  // define an additional field for a personal essay
  object textArea extends MappedTextarea(this, 2048) {
    override def textareaRows  = 10
    override def textareaCols = 50
    override def displayName = "Personal Essay"
  }
}

}
}
