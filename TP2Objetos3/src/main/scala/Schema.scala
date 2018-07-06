import api.{H2Queries, Persistence, QueryConditional}
import mapping.Mapping

object Schema {

  var tablesList: List[(_ <: Mapping[_], String)] = Nil

  val persistence = Persistence(H2Queries())

  def addAll[O <: Object](aList: List[O]): Unit = {

    aList.foreach { e =>
      val table = tablesList.find(_._2.dropRight(1) == e.getClass.getSimpleName).get._1
      val dogsQuery: QueryConditional[e.type, table.type] = persistence.query[e.type, table.type](table)
      dogsQuery.add(e)
    }
  }

  def addTable[T <: Mapping[_]](table: T, o: Object): Unit = {
    tablesList :+= (table, o.getClass.getSimpleName)
  }

}



