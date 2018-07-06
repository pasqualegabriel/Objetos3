package mapping.dogs.example

import java.sql.ResultSet

import mapping.user.example.Users
import mapping.{IntColumn, Mapping, StringColumn}

object Dogs2 extends Mapping[Dog2] {

  override def tableName = "Dogs2"

  def name2 = StringColumn("name2", tableName).foreignKey(Users.nick)

  def age2 = IntColumn("age2", tableName)

  def ** = List(name2, age2)

  override def get(resQuery: ResultSet) = Dog2(name2.get(resQuery), age2.get(resQuery))
}