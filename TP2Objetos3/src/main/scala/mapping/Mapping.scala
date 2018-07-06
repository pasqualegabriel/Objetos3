package mapping

import java.sql.ResultSet

trait Mapping[T] {

  def tableName: String

  def ** : List[Column[_]]

  def get(resQuery: ResultSet): T
}
