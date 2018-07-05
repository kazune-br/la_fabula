
package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import models.User
import models.DBAccessUser
import models.daos.UserDAOImpl._

import scala.collection.mutable
import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Give access to the user object.
 */
@Singleton
class UserDAOImpl @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends UserDAO {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */
  private class UserTable(tag: Tag) extends Table[DBAccessUser](tag, "users") {

    /** The UUID column, which is the primary key*/
    def userId = column[String]("user_id") // O.AutoInc

    def loginInfoProviderID = column[String]("login_info_provider_id")

    def loginInfoProviderKey = column[String]("login_info_provider_key")

    def firstName = column[Option[String]]("first_name")

    def lastName = column[Option[String]]("last_name")

    def fullName = column[Option[String]]("full_name")

    def email = column[Option[String]]("email")

    def avatarURL = column[Option[String]]("avatar_url")

    def activated = column[Boolean]("activated")

    def pk = primaryKey("pk", (userId, loginInfoProviderID, loginInfoProviderKey))

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (userId, loginInfoProviderID, loginInfoProviderKey, firstName, lastName, fullName, email, avatarURL, activated) <> ((DBAccessUser.apply _).tupled, DBAccessUser.unapply)
  }

  private val allUsers = TableQuery[UserTable]

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo) = db.run {
    allUsers.filter(_.loginInfoProviderKey === loginInfo.providerKey).result.headOption.map {
      case Some(user) =>
        Option(User(
          UUID.fromString(user.userID),
          LoginInfo(user.loginInfoProviderID, user.loginInfoProviderKey),
          user.firstName,
          user.lastName,
          user.fullName,
          user.email,
          user.avatarURL,
          user.activated
        ))
      case None =>
        None
    }
  }
  // users.find { case (_, user) => user.loginInfo == loginInfo }.map(_._2)

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID) = Future.successful(users.get(userID))

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User) = {
    println(db.run(allUsers += DBAccessUser(
      user.userID.toString,
      user.loginInfo.providerID,
      user.loginInfo.providerKey,
      user.firstName,
      user.lastName,
      user.fullName,
      user.email,
      user.avatarURL,
      user.activated)))
    users += (user.userID -> user)
    Future.successful(user)
  }

  def save(dummy: String, user: User) = {
    // Future<not completed>
    println(db.run(allUsers += DBAccessUser(
      user.userID.toString,
      user.loginInfo.providerID,
      user.loginInfo.providerKey,
      user.firstName,
      user.lastName,
      user.fullName,
      user.email,
      user.avatarURL,
      user.activated)))
    users += (user.userID -> user)
    Future.successful(user)
  }
}

/**
 * The companion object.
 */
object UserDAOImpl {

  /**
   * The list of users.
   */
  val users: mutable.HashMap[UUID, User] = mutable.HashMap()
}