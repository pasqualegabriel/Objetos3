package api

import mapping.{Column, Mapping}

case class Persistence(bd: GenericQuery) {

  def createTable[T, M <: Mapping[T]](mapping: M): Query[T, M] = {
    val sqlQuery = bd.get(QueryParts(OperationSQL.CREATE_TABLE, mapping.**))
    val query = Query[T, M](mapping, sqlQuery, bd, null)
    query.runQuery()
    query
  }
  def query[T, M <: Mapping[_]](mapping: M): QueryConditional[T, M] = {
    new QueryConditional[T, M](mapping, bd.get(QueryParts(OperationSQL.SELECT, mapping.**)), bd, null)
  }
}

trait GenericQuery {
  def get[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryParts: QueryParts[O,V,QueryType,TableType,T]): StringQuery
}

case class H2Queries() extends GenericQuery {

  def queries[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]]
  (queryPart: QueryParts[O,V,QueryType,TableType,T]):
   StringQuery = queryPart.key match {
    case OperationSQL.CREATE_TABLE =>  CreateTable  (queryPart)
    case OperationSQL.SELECT       =>  SelectTable  [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.MAP          =>  MapTable     [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.FILTER       =>  FilterTable  [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.ADD          =>  AddObject    [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.UPDATE       =>  UpdateValues [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.DELETE       =>  DeleteRow    [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.SORT_BY      =>  SortBy       [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.TAKE         =>  Limit        [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.UNION        =>  Union        [O,V,QueryType,TableType, T](queryPart)
    case OperationSQL.DROP         =>  Drop         [O,V,QueryType,TableType, T](queryPart)
  }

  override def get[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]]
  (queryPart: QueryParts[O,V,QueryType,TableType,T]): StringQuery =queries[O,V,QueryType,TableType, T](queryPart)
}

trait StringQuery {

  def projection: String = ""

  def tableName: String  = ""

  def condition: String  = ""

  def orderBy: String    = ""

  def limit: String      = ""

  def drop: String       = ""

  def oldUnion: Boolean  = false

  def union: StringQuery = null

  def where: String      = condition match {
    case "" => ""
    case _  => " where " + condition
  }

  def query: String    = union match {
    case null => projection + tableName + where + orderBy + limit + drop + ";"
    case _    => projection + "((" + tableName.dropRight(1) + ") union (" + union.query.dropRight(1) + "));"
  }
}

case class CreateTable[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

    override def projection: String =
    "CREATE TABLE IF NOT EXISTS " + queryPart.tableName + " (" + queryPart.columnNamesFK + ")"

}

case class SelectTable[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = "select " + queryPart.columnNames2 + " from "

  override def tableName:  String =  queryPart.tableName
}

case class MapTable[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = "select " + queryPart.nameAndOperator + " from "

  override def tableName: String  = queryPart.calulateTableName

  override def condition: String  = queryPart.oldQuery.condition


  override def orderBy: String = queryPart.oldQuery.orderBy

  override def limit: String = queryPart.oldQuery.limit

  override def oldUnion: Boolean = false

  override def drop: String = queryPart.oldQuery.drop

}

case class FilterTable[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = queryPart.oldQuery.projection

  override def tableName: String  = queryPart.calulateTableName

  override def condition:  String = queryPart.oldQuery.condition match {
    case "" => queryPart.nameAndOperator
    case q  => "(" + q + " and " + queryPart.nameAndOperator + ")"
  }
  override def orderBy: String = queryPart.oldQuery.orderBy

  override def limit: String = queryPart.oldQuery.limit

  override def oldUnion: Boolean = false

  override def drop: String = queryPart.oldQuery.drop


}

case class AddObject[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = "insert into "

  override def tableName: String  = queryPart.tableName +
    " values (" + queryPart.getFields  + ")"
}

case class UpdateValues[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = "update "

  def columnToChange: String = queryPart.nameAndOperator + "=" + queryPart.newValue

  override def tableName: String = queryPart.tableName + " set " + columnToChange

  override def condition: String = queryPart.oldQuery.condition

}

case class DeleteRow[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = "delete from "

  override def tableName: String = queryPart.tableName

  override def condition: String = queryPart.condition
}

case class SortBy[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = queryPart.oldQuery.projection

  override def tableName: String = queryPart.calulateTableName

  override def condition: String = queryPart.oldQuery.condition

  override def orderBy: String = " order by " + queryPart.columnName + " " + queryPart.sortBy

  override def limit: String = queryPart.oldQuery.limit

  override def oldUnion: Boolean = false

  override def drop: String = queryPart.oldQuery.drop

}


case class Union[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = queryPart.oldQuery.projection

  override def tableName: String = queryPart.oldQuery.query

  override def union: StringQuery = queryPart.aQuery.stringSql

  override def oldUnion: Boolean = true

}

case class Drop[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery{

  override def projection: String = queryPart.oldQuery.projection

  override def tableName: String = queryPart.calulateTableName

  override def condition: String = queryPart.oldQuery.condition

  override def orderBy: String = queryPart.oldQuery.orderBy

  override def limit: String = queryPart.oldQuery.limit

  override def oldUnion: Boolean = false

  override def drop: String = " offset " + queryPart.aLimit

}
case class Limit[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](queryPart: QueryParts[O,V,QueryType,TableType,T]) extends StringQuery {

  override def projection: String = queryPart.oldQuery.projection

  override def tableName: String = queryPart.calulateTableName

  override def condition: String = queryPart.oldQuery.condition

  override def orderBy: String = queryPart.oldQuery.orderBy

  override def limit: String     = " limit " + queryPart.aLimit

  override def oldUnion: Boolean = false

  override def drop: String      = queryPart.oldQuery.drop
}