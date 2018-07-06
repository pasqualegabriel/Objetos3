package mapping.user.example

import java.sql.ResultSet

import mapping.{DoubleColumn, Mapping, StringColumn}

object Users extends Mapping[User] {

  override def tableName: String = "Users"

  def id = DoubleColumn("id", tableName)

  def nick = StringColumn("nick", tableName)

  def password = StringColumn("password", tableName)

  def ** = List(id, nick, password)

  override def get(resQuery: ResultSet): User = User(id.get(resQuery), nick.get(resQuery), password.get(resQuery))
}