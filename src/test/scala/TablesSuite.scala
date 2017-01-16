import com.knoldus.kafka.persistance.Users
import org.scalatest._

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.meta._


class TablesSuite extends FunSuite with BeforeAndAfter {

  val users = TableQuery[Users]

  implicit var session: Session = _

  def createSchema(): Unit = users.ddl.create

  def insertItems(): Int = users += (111, "John", "12:12:12")

  before {
    session = Database.forURL("jdbc:h2:mem:test1", driver = "org.h2.Driver").createSession()
  }

  test("Creating the Schema works") {
    createSchema()

    val tables = MTable.getTables.list

    assert(tables.size == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("users")) == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("nonusers")) == 0)
  }

  test("Inserting a Supplier works") {
    createSchema()

    val insertCount = insertItems()
    assert(insertCount == 1)
  }

  test("Query Suppliers works") {
    createSchema()
    insertItems()
    val results = users.list
    assert(results.size == 1)
    assert(results.head._1 == 111)
  }

  after {
    session.close()
  }

}