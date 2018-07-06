package mapping

import java.sql.ResultSet

abstract class Column[T](val aName: String, val tableName: String, val anOperator: String = "", val aSortBy: String = "", val aForeignKey: String = "") extends Mapping[T] {

  def get(resQuery: ResultSet): T

  def date[O](anObject: O): T = {
    val field = anObject.getClass.getDeclaredField(aName)
    field.setAccessible(true)
    field.get(anObject).asInstanceOf[T]
  }

  def name: String

  def nameAndOperator: String = name + anOperator

  def columnType: String

  def columnName: String = name + " " + columnType

  override def ** : List[Column[_]] = List(this)

  /**SORT BY*/

  def asc: Column[T]

  def desc: Column[T]

  /**COMPARATORS*/

  def >(value: T): Column[T]

  def <(value: T): Column[T]

  def >=(value : T): Column[T]

  def <=(value : T): Column[T]

  def ===(value: T): Column[T]

  def =!=(value: T): Column[T]



}

case class StringColumn(name: String, aTableName: String, operator: String = "", sortBy: String = "", foreignKey: String = "")
  extends Column[String](name, aTableName, operator, sortBy, foreignKey) {

  def foreignKey(column: StringColumn): Column[String] = this.copy(foreignKey = ", FOREIGN KEY (" + this.name + ") REFERENCES " + column.aTableName + "(" + column.name +")")

  override def columnType: String = "varchar(255)"

  override def get(resQuery: ResultSet): String = resQuery.getString(name)

  override def date[O](anObject: O): String =  "'" + super.date[O](anObject) + "'"

  def operate(anOperator: String, aString: String): Column[String] =
    this.copy(operator = " " + anOperator + " '" + aString + "'")

  /**SORT BY*/

  override def asc: Column[String]  = copy(sortBy = "asc")

  override def desc: Column[String] = this.copy(sortBy = "desc")

  /**COMPARATORS*/

  override def ===(aString: String): Column[String] = operate("=" , aString)

  override def =!=(aString: String): Column[String] = operate("<>", aString)

  override def >  (aString: String): Column[String] = operate(">" , aString)

  override def <  (aString: String): Column[String] = operate("<" , aString)

  override def >= (aString: String): Column[String] = operate(">=" , aString)

  override def <= (aString: String): Column[String] = operate("<=" , aString)


}

case class IntColumn(name: String, aTableName: String, operator: String = "", sortBy: String = "")
  extends Column[Int](name, aTableName, operator, sortBy) {




  override def columnType: String = "int"

  override def get(resQuery: ResultSet): Int = resQuery.getInt(name + operator)

  /**SORT BY*/

  override def asc: Column[Int] = this.copy(sortBy = "asc")

  override def desc: Column[Int] = this.copy(sortBy = "desc")

  /**COMPARATORS*/

  override def ===(aInt: Int): Column[Int] = this.copy(operator = " = " + aInt)

  override def =!=(aInt: Int): Column[Int] = this.copy(operator = " <> " + aInt)

  override def >  (aInt: Int): Column[Int] = this.copy(operator = " > " + aInt)

  override def <  (aInt: Int): Column[Int] = this.copy(operator = " < " + aInt)

  override def >= (aInt: Int): Column[Int] = this.copy(operator = " >= " + aInt)

  override def <= (aInt: Int): Column[Int] = this.copy(operator = " <= " + aInt)

  /**ARITHMETIC OPERATORS*/

  def +(aInt: Int): Column[Int]   = this.copy(operator = " + " + aInt)

  def -(aInt: Int): Column[Int]   = this.copy(operator = " - " + aInt)

  def *(aInt: Int): Column[Int]   = this.copy(operator = " * " + aInt)

  def /(aInt: Int): Column[Int]   = this.copy(operator = " / " + aInt)

  def %(aInt: Int): Column[Int]   = this.copy(operator = " % " + aInt)

}

case class DoubleColumn(name: String, aTableName: String, operator: String = "", sortBy: String = "")
  extends Column[Double](name, aTableName, operator,sortBy) {

  /**SORT BY*/

  override def asc: Column[Double] = this.copy(sortBy = "asc")

  override def desc: Column[Double] = this.copy(sortBy = "desc")
 
  override def columnType: String = "double"

  override def get(resQuery: ResultSet): Double = resQuery.getDouble(name)

  /**COMPARATORS*/

  override def ===(aDouble: Double): Column[Double] = this.copy(operator = " = " + aDouble)

  override def =!=(aDouble: Double): Column[Double] = this.copy(operator = " <> " + aDouble)

  override def >(aDouble: Double): Column[Double]   = this.copy(operator = " > " + aDouble)

  override def < (aDouble: Double): Column[Double] = this.copy(operator = " < " + aDouble)

  override def >=(aDouble: Double): Column[Double] = this.copy(operator = " >= " + aDouble)

  override def <=(aDouble: Double): Column[Double] = this.copy(operator = " <= " + aDouble)

  /**ARITHMETIC OPERATORS*/

  def +(aDouble: Double): Column[Double]   = this.copy(operator = " + " + aDouble)

  def -(aInt: Int): Column[Double]         = this.copy(operator = " - " + aInt)

  def *(aInt: Int): Column[Double]         = this.copy(operator = " * " + aInt)

  def /(aInt: Int): Column[Double]         = this.copy(operator = " / " + aInt)

  def %(aInt: Int): Column[Double]         = this.copy(operator = " % " + aInt)


}

