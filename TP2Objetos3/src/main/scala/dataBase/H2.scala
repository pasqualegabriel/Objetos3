package dataBase

import java.sql._

import api.DataBase
import mapping.{Column, Mapping}

object H2 extends DataBase {

  val JDBC_DRIVER = "org.h2.Driver"
  val DB_URL = "jdbc:h2:~/tp2Objetos3"
  val USER = "sa"
  val PASS = ""


  def run[QueryType, TableType <: Mapping[_]](stringSql: String, mapping: TableType, column: Column[QueryType]): List[QueryType] = {

    Class.forName(JDBC_DRIVER)

    val conn = DriverManager.getConnection(DB_URL, USER, PASS)
    val stmt = conn.createStatement

    val resQuery: ResultSet = stmt.executeQuery(stringSql)

    var res: List[QueryType] = Nil
    while (resQuery.next()) {
      if (column != null) {
        val r: QueryType = column.get(resQuery)
        res :+= r
      }
      else {
        val r2: QueryType = mapping.asInstanceOf[Mapping[QueryType]].get(resQuery)
        res :+= r2
      }
    }
    stmt.close()
    conn.close()
    res
  }

  def dropTables(): Unit = {

    Class.forName(JDBC_DRIVER)

    val conn = DriverManager.getConnection(DB_URL, USER, PASS)

    val stmt = conn.createStatement
    stmt.executeUpdate("SET REFERENTIAL_INTEGRITY FALSE")
    val tables = stmt.executeQuery("show tables;")
    var ts: List[String] = Nil
    while (tables.next()) ts ::= tables.getString("TABLE_NAME")
    ts.foreach(e => stmt.executeUpdate("TRUNCATE TABLE " + e + ";"))
    stmt.executeUpdate("SET REFERENTIAL_INTEGRITY TRUE")

    stmt.close()
    conn.close()
  }

  def execute(aQuery: String): Unit = {

    Class.forName(JDBC_DRIVER)

    val conn = DriverManager.getConnection(DB_URL, USER, PASS)

    val stmt = conn.createStatement
    stmt.execute(aQuery)

    stmt.close()
    conn.close()
  }

}