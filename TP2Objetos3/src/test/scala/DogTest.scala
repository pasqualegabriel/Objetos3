import api.{H2Queries, Persistence, QueryConditional}
import dataBase.H2
import mapping.dogs.example.{Dog, Dog2, Dogs, Dogs2}
import mapping.user.example.{User, Users}
import org.junit.Assert._
import org.junit._

class DogTest {

  val persistence = Persistence(H2Queries())
  val dogsQuery: QueryConditional[Dog, Dogs.type] = persistence.query[Dog, Dogs.type](Dogs)

  @Test
  def testCreateTableDogs(): Unit = {

    val queryCreateTable = persistence.createTable[Dog, Dogs.type](Dogs)

    assertEquals("CREATE TABLE IF NOT EXISTS Dogs (name varchar(255), age int);",
      queryCreateTable.sql)
  }

  @Test
  def testInitialQuery(): Unit = {

    assertEquals("select name, age from Dogs;", dogsQuery.sql)
  }

  @Test
  def testMap(): Unit = {

    val nameQuery = dogsQuery.map(_.name)
    val ageQuery  = dogsQuery.map(_.age)

    assertEquals("select name from Dogs;", nameQuery.sql)
    assertEquals("select age from Dogs;" , ageQuery.sql)
  }

  @Test
  def testRun(): Unit = {

    val pepita = Dog("Pepita", 2)

    dogsQuery.add(pepita)

    val nameQuery: List[String] = dogsQuery.filter(_.name =!= "any").map(_.name).run

    assertTrue(nameQuery.contains("Pepita"))

    val ageQuery: List[Int] = dogsQuery.map(_.age).filter(_.age > 1).run

    assertTrue(ageQuery.contains(2))

    val q: List[Dog] = dogsQuery.run

    assertEquals(1, q.size)
    assertTrue(q.contains(pepita))
  }

  @Test
  def testMapMas(): Unit = {

    val nameQuery = dogsQuery.filter(_.name === "Pepita").map(_.name)
    val ageQuery  = dogsQuery.map(_.age + 1).filter(_.age > 2)

    assertEquals("select name from Dogs where name = 'Pepita';", nameQuery.sql)
    assertEquals("select age + 1 from Dogs where age > 2;" , ageQuery.sql)
  }

  @Test
  def TestFilterEdadMayorADos(): Unit = {

    val puppiesQueryAge  = dogsQuery.filter(_.age  > 2)
    val puppiesQueryName = dogsQuery.filter(_.name > "p")

    assertEquals("select name, age from Dogs where age > 2;"   , puppiesQueryAge.sql)
    assertEquals("select name, age from Dogs where name > 'p';", puppiesQueryName.sql)
  }

  @Test
  def testMapAndFilter(): Unit = {

    val puppiesNameQuery1 = dogsQuery.filter(_.age + 2).map(_.name)
    val puppiesNameQuery2 = dogsQuery.map(_.name).filter(_.age > 2)
    val puppiesAgeQuery   = dogsQuery.filter(_.age > 2).map(_.age)
    val ageQuery          = dogsQuery.filter(_.age > 2).map(_.age).filter(_.name === "Pepita")

    assertEquals("select name from Dogs where age + 2;", puppiesNameQuery1.sql)
    assertEquals("select name from Dogs where age > 2;", puppiesNameQuery2.sql)
    assertEquals("select age from Dogs where age > 2;" , puppiesAgeQuery.sql)
    assertEquals("select age from Dogs where (age > 2 and name = 'Pepita');" , ageQuery.sql)
  }

  @Test
  def testAnd(): Unit = {

    val ageQuery  = dogsQuery.filter(_.age > 2).map(_.age).filter(_.name === "Pepita")
    val nameQuery = dogsQuery.filter(_.age > 2)
                          .map(_.age)
                          .filter(_.name === "Pepita")
                          .filter(_.age > 2)
                          .filter(_.age =!= 1)

    assertEquals("select age from Dogs where (age > 2 and name = 'Pepita');" , ageQuery.sql)
    assertEquals("select age from Dogs where " +
      "(((age > 2 and name = 'Pepita') and age > 2) and age <> 1);" , nameQuery.sql)
  }


  @Test
  def testAddDog(): Unit = {

    val pepita = Dog("Pepita", 2)

    val addQuery = dogsQuery.filter(_.name === "Pepita").add(pepita)

    val dogs = dogsQuery.run

    assertEquals("insert into Dogs values ('Pepita', 2);", addQuery.sql)
    assertTrue(dogs.contains(pepita))
  }

  @Test
  def testUpdateDog(): Unit ={

    val dog = Dog("Pepita", 2)

    dogsQuery.add(dog)

    val pepita = dogsQuery.filter(_.name === "Pepita")

    val updateQuery = pepita.update(_.age, 9)

    val dogs = dogsQuery.run


    assertEquals("update Dogs set age=9 where name = 'Pepita';",updateQuery.sql)

    val dogPepita = dogs.filter(_.name=="Pepita").head

    assertEquals(9, dogPepita.age)
  }


  @Test
  def testDeleteDog(): Unit ={

    val pepita = Dog("Pepita", 2)

    dogsQuery.add(pepita)

    val deleteQuery = dogsQuery.delete(pepita)

    val dogs = dogsQuery.run

    assertEquals("delete from Dogs where name = 'Pepita' and age = 2;", deleteQuery.sql)

    assertFalse(dogs.contains(pepita))
  }

  @Test
  def testSortByDESC(): Unit = {

    val azul = Dog("Azul", 1)
    val blanca = Dog("Blanca", 2)
    val celeste = Dog("Celeste", 3)

    dogsQuery.add(azul).add(blanca).add(celeste)

    val sortQuery = dogsQuery.sortBy(_.name.desc)

    assertEquals("select name, age from Dogs order by name desc;",sortQuery.sql)

    val dogs = sortQuery.run

    assertEquals("select name, age from Dogs order by name desc;",sortQuery.sql)

    assertEquals(3, dogs.size)

    assertEquals(dogs.head.name, "Celeste")
    assertEquals(dogs(1).name, "Blanca")
    assertEquals(dogs.last.name, "Azul")

  }

  @Test
  def testSortByASC(): Unit ={

    val azul = Dog("Azul",1)
    val blanca = Dog("Blanca",2)
    val celeste = Dog("Celeste",3)

    dogsQuery.add(azul).add(blanca).add(celeste)

    val sortQuery = dogsQuery.sortBy(_.name.asc)

    val dogs = sortQuery.run

    assertEquals("select name, age from Dogs order by name asc;", sortQuery.sql)

    assertEquals(3, dogs.size)

    assertEquals(dogs.head.name, "Azul")
    assertEquals(dogs(1).name, "Blanca")
    assertEquals(dogs.last.name, "Celeste")

  }

  @Test
  def testTakeTwo(): Unit = {

    val azul = Dog("Azul",1)
    val blanca = Dog("Blanca",2)
    val celeste = Dog("Celeste",3)

    dogsQuery.add(azul).add(blanca).add(celeste)

    val takeQuery = dogsQuery.sortBy(_.name.asc).take(2)

    assertEquals("select name, age from Dogs order by name asc limit 2;",takeQuery.sql)

    val dogs= takeQuery.run

    assertEquals(2,dogs.size)
    assertFalse(dogs.contains(celeste))

  }

  @Test
  def testDropTwo(): Unit ={

    val azul = Dog("Azul",1)
    val blanca = Dog("Blanca",2)
    val celeste = Dog("Celeste",3)

    dogsQuery.add(azul).add(blanca).add(celeste)

    val dropQuery = dogsQuery.drop(2)

    assertEquals("select name, age from Dogs offset 2;",dropQuery.sql)

    val dogs= dropQuery.run

    assertEquals(1,dogs.size)
    assertEquals("Celeste",dogs.head.name)
  }

  @Test
  def TestDropOneANDTakeOne(): Unit = {

    val azul = Dog("Azul",1)
    val blanca = Dog("Blanca",2)
    val celeste = Dog("Celeste",3)

    dogsQuery.add(azul).add(blanca).add(celeste)

    val takeQuery = dogsQuery.sortBy(_.name.asc).drop(1).take(1)

    assertEquals("select name, age from Dogs order by name asc limit 1 offset 1;",takeQuery.sql)

    val dogs= takeQuery.run

    assertEquals(1,dogs.size)
    assertFalse(dogs.contains(celeste))

  }

  @Test
  def testUnion(): Unit = {


    val fidos     = dogsQuery.filter(_.name === "fido")
    val cachorros = dogsQuery.filter(_.age > 2)

    val fidosYCachorros = fidos ++ cachorros

    fidosYCachorros.run

    assertEquals("select name, age from ((select name, age from Dogs where name = 'fido') union (select name, age from Dogs where age > 2));", fidosYCachorros.sql)
  }

  @Test
  def testTripleUnion(): Unit = {
    
    val fidos     = dogsQuery.filter(_.name === "fido")
    val cachorros = dogsQuery.filter(_.age > 2)
    val pepitas   = dogsQuery.filter(_.name === "pepita")

    val fidosYCachorros = fidos ++ cachorros

    val fidosCachorrosPepita = fidosYCachorros ++ pepitas

    fidosCachorrosPepita.run

    assertEquals("select name, age from ((select name, age from ((select name, age from Dogs where name = 'fido') union " +
      "(select name, age from Dogs where age > 2))) union (select name, age from Dogs where name = 'pepita'));",
      fidosCachorrosPepita.sql)
  }

  @Test
  def testTripleUnionWithFilter(): Unit = {


    val fidos = dogsQuery.filter(_.name === "fido")
    val cachorros = dogsQuery.filter(_.age > 2)
    val pepitas = dogsQuery.filter(_.name === "pepita")

    val fidosYCachorros = fidos ++ cachorros

    val fidosCachorrosPepita = fidosYCachorros ++ pepitas

    val pepis = fidosCachorrosPepita.filter(_.name==="pepita")

    assertEquals("select name, age from ((select name, age from ((select name, age from Dogs where name = 'fido') union" +
      " (select name, age from Dogs where age > 2))) union " +
      "(select name, age from Dogs where name = 'pepita')) where name = 'pepita';",pepis.sql)
  }

  /**ARITHMETIC OPERATORS TESTS*/

  @Test
  def testMapWithAdd(): Unit = {

    val pepita = Dog("Pepita",1)
    val pepe   = Dog("Pepe", 2)

    dogsQuery.add(pepita).add(pepe)

    val query= dogsQuery.map(_.age + 1)

    assertEquals("select age + 1 from Dogs;", query.sql)

    val dogsAges = query.run

    assertEquals(2, dogsAges(0))
    assertEquals(3, dogsAges(1))

  }

  @Test
  def testMapWithLess(): Unit ={

    val pepita = Dog("Pepita",2)
    val pepe   = Dog("Pepe", 3)

    dogsQuery.add(pepita).add(pepe)

    val query= dogsQuery.map(_.age - 1)

    assertEquals("select age - 1 from Dogs;",query.sql)

    val dogsAges = query.run

    assertEquals(1,dogsAges(0))
    assertEquals(2,dogsAges(1))

  }

  @Test
  def testMapWithMultiply(): Unit ={


    val pepita = Dog("Pepita",1)
    val pepe   = Dog("Pepe", 2)

    dogsQuery.add(pepita).add(pepe)

    val query= dogsQuery.map(_.age * 3)

    assertEquals("select age * 3 from Dogs;",query.sql)

    val dogsAges = query.run

    assertEquals(3,dogsAges(0))
    assertEquals(6,dogsAges(1))

  }

  @Test
  def testMapWithDivide(): Unit = {

    val pepita = Dog("Pepita",8)
    val pepe   = Dog("Pepe", 12)

    dogsQuery.add(pepita).add(pepe)

    val query= dogsQuery.map(_.age / 2)

    assertEquals("select age / 2 from Dogs;",query.sql)

    val dogsAges = query.run

    assertEquals(4,dogsAges(0))
    assertEquals(6,dogsAges(1))

  }

  @Test
  def testMapWithModule(): Unit ={

    val pepita = Dog("Pepita",20)
    val pepe   = Dog("Pepe", 6)

    dogsQuery.add(pepita).add(pepe)

    val query= dogsQuery.map(_.age % 2)

    assertEquals("select age % 2 from Dogs;",query.sql)

    val dogsAges = query.run

    assertEquals(0,dogsAges(0))
    assertEquals(0,dogsAges(1))
  }

  /**COMPARATOR TESTS*/

  @Test
  def testMapWithEqualTo(): Unit ={

    val pepita = Dog("Pepita",20)
    val pepe   = Dog("Pepe", 6)

    dogsQuery.add(pepita).add(pepe)

    val query= dogsQuery.filter(_.name === "Pepita").filter(_.age === 20)


    assertEquals("select name, age from Dogs where (name = 'Pepita' and age = 20);",query.sql)

    val dogs = query.run

    assertEquals(1, dogs.size)
    assertEquals("Pepita", dogs(0).name)
    assertEquals(20, dogs(0).age)

  }

  @Test
  def testMapWithNotEqualTo(): Unit ={

    val pepita = Dog("Pepita",20)                                                                 
    val pepe   = Dog("Pepe", 6)                                                                       
                                                                                                      
    dogsQuery.add(pepita).add(pepe)                                                                   
                                                                                                      
    val query= dogsQuery.filter(_.name =!= "Pepita").filter(_.age =!= 20)
                                                                                                      
    assertEquals("select name, age from Dogs where (name <> 'Pepita' and age <> 20);",query.sql)
                                                                                                      
    val dogs = query.run                                                                              
                                                                                                      
    assertEquals(1, dogs.size)                                                                        
    assertEquals("Pepe", dogs(0).name)
    assertEquals(6, dogs(0).age)

  }

  @Test
  def testMapWithGreaterThan(): Unit ={

    val pepita = Dog("Pepita",20)
    val pepe   = Dog("Pepe", 6)
    val azul  = Dog("Azul",3)

    dogsQuery.add(pepita).add(pepe).add(azul)

    val query= dogsQuery.filter(_.name > "D").filter(_.age > 1)


    assertEquals("select name, age from Dogs where (name > 'D' and age > 1);",query.sql)

    val dogs = query.run

    assertEquals(2,dogs.size)
    assertTrue(dogs.contains(pepita))
    assertTrue(dogs.contains(pepe))

  }

  @Test
  def testMapWithLessThan(): Unit ={

    val pepita = Dog("Pepita",20)
    val pepe   = Dog("Pepe", 6)
    val azul  = Dog("Azul",3)

    dogsQuery.add(pepita).add(pepe).add(azul)

    val query= dogsQuery.filter(_.name < "D").filter(_.age < 4)


    assertEquals("select name, age from Dogs where (name < 'D' and age < 4);",query.sql)

    val dogs = query.run

    assertEquals(1,dogs.size)
    assertTrue(dogs.contains(azul))
  }

  @Test
  def testMapWithGreaterThanOrEqualTo(): Unit ={

    val pepita = Dog("Pepita",20)
    val pepe   = Dog("Pepe", 6)
    val azul  = Dog("Azul",3)

    dogsQuery.add(pepita).add(pepe).add(azul)

    val query= dogsQuery.filter(_.name >= "D").filter(_.age >= 4)


    assertEquals("select name, age from Dogs where (name >= 'D' and age >= 4);",query.sql)

    query.run
  }

  @Test
  def testMapWithLessThanOrEqualTo(): Unit ={

    val pepita = Dog("Pepita",20)
    val pepe   = Dog("Pepe", 6)
    val azul  = Dog("Azul",3)

    dogsQuery.add(pepita).add(pepe).add(azul)

    val query= dogsQuery.filter(_.name <= "D").filter(_.age <= 4)


    assertEquals("select name, age from Dogs where (name <= 'D' and age <= 4);",query.sql)

    query.run

  }


  @Test
  def testFlashero(): Unit ={

    val query = dogsQuery.filter(_.name === "Pepita").drop(2).take(3).sortBy(_.age.asc)

    val query2 = dogsQuery.filter(_.age >= 1).take(2)

    val union = query ++ query2

    val union2 = union.filter(_.name === "Pepita").drop(2).take(3).sortBy(_.name.desc)

    union.run

    assertEquals("select name, age from ((select name, age from Dogs where name = 'Pepita' order by age asc limit 3 offset 2) union " +
      "(select name, age from Dogs where age >= 1 limit 2)) where name = 'Pepita' order by name desc limit 3 offset 2;", union2.sql)

  }

 @Test
  def testSchemaAddAll(): Unit = {

    val aDog  = Dog("Juan",1)
    val aUser = User(1.2,"pepitakpa","root")

    Schema.addTable(Dogs, Dog)
    Schema.addTable(Users,User)

    val masterList = List(aDog, aUser)

    Schema.addAll(masterList)

    val dogs = dogsQuery.run

    assertTrue(dogs.exists(_.name == aDog.name))
  }


  @Test
  def testForeignKey(): Unit ={


  val queryCreateTable = persistence.createTable[Dog2, Dogs2.type](Dogs2)

  assertEquals("CREATE TABLE IF NOT EXISTS Dogs2 (name2 varchar(255), age2 int, FOREIGN KEY (name2) REFERENCES Users(nick));",
  queryCreateTable.sql)

  }

  @Test
  def testForeignKeyExc(): Unit ={

    val dogsQuery2: QueryConditional[Dog2, Dogs2.type] = persistence.query[Dog2, Dogs2.type](Dogs2)

    val dog = Dog2("Pepa",1)
    val user = User(1,"Pepa","")

    val userQuery: QueryConditional[User, Users.type] = persistence.query[User, Users.type](Users)
    userQuery.add(user)

    dogsQuery2.add(dog)
  }

 @After
 def tearDown(): Unit = H2.dropTables()

}




