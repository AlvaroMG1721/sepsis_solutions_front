package models.process

import javax.persistence._
import models.domains.Patient
import models.domains.Page
import play.api.Logger

trait PatientProcess {
  def listAlerts(page: Int, orderBy: Int, filter: String): Page[Employee]
}

class PatientProcessImpl(patientDAO: PatientDAO) extends PatientProcess {

  val logger: Logger = Logger(this.getClass())

  def listAlerts(page: Int, orderBy: Int, filter: String): Page[Employee] = {
    patientDAO.list(page = page, orderBy = orderBy, filter = filter) match {
      case Right(page) => page
      case Left(error) => logger.error(error); Page(Nil, page, 0, 0)
    }
  }
}

object PatientProcessImpl extends PatientProcessImpl(PatientDAOImpl)
