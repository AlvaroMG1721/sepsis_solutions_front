package models.process

import javax.persistence._
import models.domains.Employee
import play.api.Logger
import models.domains.Page

trait PatientDAO {
  def listAlerts(page: Int, orderBy: Int, filter: String): Either[String, Page[Employee]]
}

object PatientDAOImpl extends PatientDAO {

  val logger: Logger = Logger(this.getClass())

  val emf: EntityManagerFactory = Persistence.createEntityManagerFactory("cassandra_employees")

  /**
   * Return a page of (Patient) with score clasified as alert.
   */
  def listAlerts(page: Int = 0, orderBy: Int = 1, filter: String = "%"): Either[String, Page[Employee]] = {
    try {
      val pageSize: Int = 10
      val offset = pageSize * page

      val em = emf.createEntityManager()

      val query = s"""SELECT e FROM Employee e"""
      val countQuery = s"""SELECT e FROM Employee e'"""

      val employees: List[Employee] = em.createQuery(query).getResultList().toArray.toList.asInstanceOf[List[Employee]]
      val totalRows: Long = employees.size

      Right(Page(employees, page, offset, totalRows))
    }
    catch {
      case ex: Exception => logger.error("Error : ", ex); Left(ex.getMessage())
    }
  }


}
