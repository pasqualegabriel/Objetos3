package api

import dataBase.H2
import mapping.{Column, Mapping}

trait DataBase {

  def execute(query: String): Unit

  def run[QueryType, TableType <: Mapping[_]](query: String, mapping: TableType, column: Column[QueryType]):List[QueryType]

  def dropTables(): Unit

}

case class Query[QueryType, TableType <: Mapping[_]]
        (mapping: TableType, stringSql: StringQuery, genericQuery: GenericQuery, column: Column[QueryType], dataBase: DataBase = H2) {

  def run: List[QueryType] = {
    dataBase.run[QueryType, TableType](stringSql.query, mapping, column)
  }

  def runQuery(): Unit = dataBase.execute(stringSql.query)

  def sql: String = stringSql.query

  def update[ResultType](f: TableType => Column[ResultType], newValue: ResultType): Query[QueryType,TableType] = {
    makeQueryAndRun(
      QueryParts(
        OperationSQL.UPDATE,
        f(mapping).**,
        stringSql,
        newValue = newValue)
    )
  }

  def delete[O](anObject: O): Query[QueryType, TableType]= {
    makeQueryAndRun(QueryParts(OperationSQL.DELETE, mapping.**, stringSql,anObject = anObject))
  }
  def add[O](anObject: O): Query[QueryType, TableType] = {
    makeQueryAndRun(QueryParts(OperationSQL.ADD, mapping.**, stringSql,anObject = anObject))
  }
  def makeQueryAndRun[ObjectT,ResultType,O,V,QueryType2,TableType2 <: Mapping[_], H <: Column[_]]
  (queryParts: QueryParts[O,V,QueryType2,TableType2,H]) : Query[QueryType,TableType] ={
    val newStringQuery: StringQuery = genericQuery.get(queryParts)
    val query = Query[QueryType, TableType](mapping, newStringQuery, genericQuery, column)
    query.runQuery()
    query
  }

}

class QueryConditional[QueryType, TableType <: Mapping[_]]
(aMapping: TableType, aStringSql: StringQuery, aGenericQuery: GenericQuery,
 aColumn: Column[QueryType]) extends Query[QueryType, TableType](aMapping, aStringSql, aGenericQuery, aColumn){

  def map[ResultType](f: TableType => Column[ResultType]):  QueryConditional[ResultType, TableType] = {
    val newColumn: Column[ResultType] = f(mapping)
    val newStringQuery: StringQuery = genericQuery.get(QueryParts(OperationSQL.MAP, newColumn.**, stringSql))
    new QueryConditional[ResultType, TableType](mapping, newStringQuery, genericQuery, newColumn)
  }

  def makeQueryConditional[ResultType,O,V,QueryType2,TableType2 <: Mapping[_], H <: Column[_]]
  (queryParts: QueryParts[O,V,QueryType2,TableType2,H]) : QueryConditional[QueryType,TableType] ={
    val newStringQuery: StringQuery = genericQuery.get(queryParts)
    new QueryConditional[QueryType, TableType](mapping, newStringQuery, genericQuery, column)
  }


  def filter[ResultType](f: TableType => Column[ResultType]): QueryConditional[QueryType, TableType] = {
    makeQueryConditional(QueryParts(OperationSQL.FILTER, f(mapping).**, stringSql))
  }

  def sortBy[ResultType](f: TableType => Column[ResultType]): QueryConditional[QueryType,TableType] = {
    makeQueryConditional(QueryParts(OperationSQL.SORT_BY, f(mapping).**, stringSql))
  }

  def ++(aQuery: Query[QueryType, TableType]): QueryConditional[QueryType, TableType] = {
    makeQueryConditional(QueryParts(OperationSQL.UNION, mapping.**, stringSql,aQuery=aQuery))
  }

  def drop(aLimit: Integer): QueryConditional[QueryType, TableType] = {
    makeQueryConditional(QueryParts(OperationSQL.DROP, mapping.**, stringSql,aLimit = aLimit))
  }

  def take(aLimit: Integer): QueryConditional[QueryType, TableType] = {
    makeQueryConditional(QueryParts(OperationSQL.TAKE, mapping.**, stringSql,aLimit =aLimit))
  }


}
