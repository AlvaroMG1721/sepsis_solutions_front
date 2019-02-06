package controllers

import play.api._
import play.api.mvc._
import models.process.EmployeeProcess
import models.process.EmployeeProcessImpl
import java.util.UUID
import models.domains.Employee
import java.util.Date
import scala.concurrent.Future
import scala.concurrent.duration._
import anorm._
import models._
import play.api._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._
import views._
import java.util.concurrent.TimeoutException
import play.api.libs.concurrent.Promise
import models.domains.Employee
import models.domains.Employee
import models.domains.Page
import models.domains.Employee
import models.domains.EmployeeForm

class Application(empProcess: EmployeeProcess) extends Controller {

  val logger: Logger = Logger(this.getClass())

  implicit val timeout = 10.seconds

  /**
   * Describe the employee form (used in both edit and create screens).
   */
  val employeeForm = Form(
    mapping(
      "id" -> ignored(UUID.randomUUID.toString(): String),
      "name" -> nonEmptyText,
      "address" -> nonEmptyText,
      "dob" -> date("yyyy-MM-dd"),
      "joiningDate" -> date("yyyy-MM-dd"),
      "designation" -> nonEmptyText)(EmployeeForm.apply)(EmployeeForm.unapply))

  /**
   * Handle default path requests, redirect to employee list
   */
  def index = Action { Home }

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.Application.list())

  /**
   * Display the paginated list of alerts.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on employee names
   */
  def list(page: Int, orderBy: Int, filter: String) = Action.async { implicit request =>
    val futurePage: Future[Page[Employee]] = TimeoutFuture(empProcess.list(page = page, orderBy = orderBy, filter = ("%" + filter + "%")))
    futurePage.map(page => Ok(html.list(page, orderBy, filter))).recover {
      case t: TimeoutException =>
        Logger.error("Problem found in employee list process")
        InternalServerError(t.getMessage)
    }
  }

  object TimeoutFuture {

    def apply[A](block: => A)(implicit timeout: FiniteDuration): Future[A] = {

      val promise = scala.concurrent.Promise[A]()

      // if the promise doesn't have a value yet then this completes the future with a failure
      Promise.timeout(Nil, timeout).map(_ => promise.tryFailure(new TimeoutException("This operation timed out")))

      // this tries to complete the future with the value from block
      Future(promise.success(block))

      promise.future
    }

  }

}

object Application extends Application(EmployeeProcessImpl)