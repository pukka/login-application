package controllers

import models._
import controllers._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._

object TaskController extends Controller {

  val TaskForm = Form(
    mapping(
      "id" -> longNumber,
      "year" -> text,
      "month" -> text,
      "date" -> text,
      "hour" -> text,
      "minute" -> text,
      "work" -> nonEmptyText
    )(Task.apply)(Task.unapply)
  )

  def CreateTask = Action { implicit request =>
    session.get("UserData").map { UserData =>
      val NumberData:Long = UserNumber.Number(UserData)
      TaskForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("ERROR", NumberData, EveryThing.all(NumberData), errors)),
        success => {
          val data: Task = TaskForm.bindFromRequest.get
          val result = data.addTask(NumberData)
          val title = "タスクが追加されました。"
          Ok(views.html.index(title, NumberData, EveryThing.all(NumberData), TaskForm))
        }
      )
    }.getOrElse {
      Redirect(routes.Application.index).withNewSession
    }
  }

  def DeleteTask(id:Long) = Action {
    UserNumber.delete(id)
    Redirect(routes.Application.index)
  }
}
