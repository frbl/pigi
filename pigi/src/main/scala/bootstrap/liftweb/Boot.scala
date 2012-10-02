package bootstrap.liftweb

import _root_.net.liftweb.util._
import _root_.net.liftweb.util.Helpers._
import _root_.net.liftweb.common._
import _root_.net.liftweb.http._
import _root_.net.liftweb.http.provider._
import _root_.net.liftweb.sitemap._
import _root_.net.liftweb.sitemap.Loc._
import net.liftweb.mapper.ProtoUser
import Helpers._
import _root_.net.liftweb.mapper.{DB, ConnectionManager, Schemifier, DefaultConnectionIdentifier, StandardDBVendor}
import _root_.java.sql.{Connection, DriverManager}
import _root_.nl.rug.model._


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
	
    if (!DB.jndiJdbcConnAvailable_?) {
      val vendor = 
	new StandardDBVendor(Props.get("db.driver") openOr "org.h2.Driver",
			     Props.get("db.url") openOr 
			     "jdbc:h2:lift_proto.db;AUTO_SERVER=TRUE",
			     Props.get("db.user"), Props.get("db.password"))

      LiftRules.unloadHooks.append(vendor.closeAllConnections_! _)

      DB.defineConnectionManager(DefaultConnectionIdentifier, vendor)
    }

    // where to search snippet
    LiftRules.addToPackages("nl.rug")
    
		Schemifier.schemify(true, Schemifier.infoF _, User)

		/*
		//Stuff to put information in the database
		val repository: Repository = Repository.create
		repository.url("svn://test")
		repository.name("repository")
		repository.description("description repository")
		val saved: Boolean = repository.save
		println(saved);
		*/
		
		val MustBeLoggedIn = If(() => User.loggedIn_?, "You must be logged in to see this page.")
		
		def userLinkText = User.currentUser.map(_.shortName).openOr("not logged in").toString
		
    // Build SiteMap
    def sitemap() = SiteMap(
      Menu("Home") / "index" >> LocGroup("main"), 
			Menu("Statistics") / "statistics" >> MustBeLoggedIn >> LocGroup("main"),
			User.loginMenuLoc.open_!,
			User.createUserMenuLoc.open_!,
			Menu("user",userLinkText)  / "" >> MustBeLoggedIn >> LocGroup("user"), 
				User.logoutMenuLoc.open_!,
				User.editUserMenuLoc.open_!,
				User.changePasswordMenuLoc.open_!
			);
		
    //LiftRules.setSiteMapFunc(() => User.sitemapMutator(sitemap()))
		LiftRules.setSiteMap(sitemap)
    /*
     * Show the spinny image when an Ajax call starts
     */
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)

    /*
     * Make the spinny image go away when it ends
     */
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.early.append(makeUtf8)

    LiftRules.loggedInTest = Full(() => User.loggedIn_?)

    S.addAround(DB.buildLoanWrapper)
		
		LiftRules.htmlProperties.default.set(
      (r: Req) => new Html5Properties(r.userAgent))

  }

  /**
   * Force the request to be UTF-8
   */
  private def makeUtf8(req: HTTPRequest) {
    req.setCharacterEncoding("UTF-8")
  }
}
