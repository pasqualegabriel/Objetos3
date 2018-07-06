package api

import api.OperationSQL.OperationSQL
import mapping.{Column, Mapping}

case class  QueryParts[O,V,QueryType,TableType <: Mapping[_], T <: Column[_]](
                                                        key: OperationSQL =null,
                                                        columns: List[T]=null, oldQuery: StringQuery=null, anObject: O=null,
                                                        newValue: V=null, aLimit: Integer=null, aQuery: Query[QueryType,TableType]=null){



  def columnNames :     String = columns.map(_.columnName).mkString(", ")

  def columnNamesFK: String = columns.map(_.columnName).mkString(", ") + columns.map(_.aForeignKey).mkString

  def tableName :       String = columns.head.tableName

  def condition :       String = columns.map(column => column.name + " = " +column.date(anObject).toString).mkString(" and ")

  def columnName :      String = columns.head.aName

  def columnNames2:     String = columns.map(_.name).mkString(", ")

  def nameAndOperator : String = columns.head.nameAndOperator

  def sortBy:           String =  columns.head.aSortBy

  def getFields:        String =  columns.map(_.date(anObject)).mkString(", ")

  def calulateTableName: String = if (oldQuery.oldUnion) "((" + oldQuery.tableName.dropRight(1) + ") union (" + oldQuery.union.query.dropRight(1) + "))"
                                  else oldQuery.tableName

}


//implementar abstract syntax tree

