package models

case class DBAccessUser(
  userID: String, // UUID
  loginInfoProviderID: String, // LoginInfo
  loginInfoProviderKey: String, // LoginInfo
  firstName: Option[String],
  lastName: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  activated: Boolean) {

}
