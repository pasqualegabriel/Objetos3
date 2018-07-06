package mapping.dogs.example

import java.sql.ResultSet

import mapping.{IntColumn, Mapping, StringColumn}

object Dogs extends Mapping[Dog] {

  override def tableName = "Dogs"

  def name = StringColumn("name", tableName)

  def age = IntColumn("age", tableName)

  def ** = List(name, age)

  override def get(resQuery: ResultSet) = Dog(name.get(resQuery), age.get(resQuery))
}

