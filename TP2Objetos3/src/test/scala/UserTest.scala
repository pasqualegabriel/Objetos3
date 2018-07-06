import api.{H2Queries, Persistence, QueryConditional}
import dataBase.H2
import mapping.user.example.{User, Users}
import org.junit.Assert._
import org.junit._

class UserTest {

  val persistence = Persistence(H2Queries())

  val userQuery: QueryConditional[User, Users.type] = persistence.query[User, Users.type](Users)

  @Test
  def `query create table User`(): Unit = {

    val queryCreateTable = persistence.createTable[User, Users.type](Users)

    assertEquals("CREATE TABLE IF NOT EXISTS Users (id double, nick varchar(255), password varchar(255));",
      queryCreateTable.sql)
  }

  @Test
  def `initial query user`(): Unit = {

    assertEquals("select id, nick, password from Users;", userQuery.sql)
  }

  @Test
  def `map user`(): Unit = {

    val idQuery = userQuery.map(_.id)
    val nickQuery  = userQuery.map(_.nick)

    assertEquals("select id from Users;", idQuery.sql)
    assertEquals("select nick from Users;" , nickQuery.sql)
  }

  @Test
  def `run user`(): Unit = {

    val pepita = User(2.6, "Pepita", "pass")
    val goku   = User(6.2, "Foku"  , "pass")

    userQuery.add(pepita)
    userQuery.add(goku)

    val nickQuery: List[String] = userQuery.filter(_.nick =!= "any").map(_.nick).run

    assertTrue(nickQuery.contains("Pepita"))

    val idQuery: List[Double] = userQuery.map(_.id).filter(_.id > 2.6).run

    assertTrue(idQuery.contains(6.2) && !idQuery.contains(2.6))

    val users: List[User] = userQuery.run

    assertEquals(2, users.size)
    assertTrue(users.contains(pepita) && users.contains(goku))
  }

  @Test
  def `user map +`(): Unit = {

    val nameQuery = userQuery.filter(_.nick === "Pepita").map(_.nick)
    val ageQuery  = userQuery.map(_.id + 1).filter(_.id > 2)

    assertEquals("select nick from Users where nick = 'Pepita';", nameQuery.sql)
    assertEquals("select id + 1.0 from Users where id > 2.0;" , ageQuery.sql)
  }

  @Test
  def `filter age > 2`(): Unit = {

    val userQueryId   = userQuery.filter(_.id > 2)
    val userQueryPass = userQuery.filter(_.password > "p")

    assertEquals("select id, nick, password from Users where id > 2.0;"      , userQueryId.sql)
    assertEquals("select id, nick, password from Users where password > 'p';", userQueryPass.sql)
  }

  @Test
  def `user map and filter`(): Unit = {

    val q1 = userQuery.filter(_.id + 2).map(_.nick)
    val q2 = userQuery.map(_.nick).filter(_.id > 2)
    val q3 = userQuery.filter(_.id > 2).map(_.id)
    val q4 = userQuery.filter(_.id > 2).map(_.id).filter(_.nick === "Pepita")

    assertEquals("select nick from Users where id + 2.0;", q1.sql)
    assertEquals("select nick from Users where id > 2.0;", q2.sql)
    assertEquals("select id from Users where id > 2.0;" , q3.sql)
    assertEquals("select id from Users where (id > 2.0 and nick = 'Pepita');" , q4.sql)
  }

  @Test
  def `add dog`(): Unit = {

    val pepita = User(2.6, "Pepita", "pass")

    val addQuery = userQuery.add(pepita)

    val users = userQuery.run

    assertEquals("insert into Users values (2.6, 'Pepita', 'pass');", addQuery.sql)
    assertTrue(users.contains(pepita))
  }

  @After
  def `tear down`(): Unit = H2.dropTables()

}




