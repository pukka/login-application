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

  val ChangeForm = Form(
    mapping(
      "id" -> longNumber,
      "date" -> text,
      "time" -> text,
      "work" -> nonEmptyText
    )(EveryThing.apply)(EveryThing.unapply)
  )

  def CreateTask = Action { implicit request =>
    session.get("UserData").map { UserData =>
      val NumberData:Long = UserNumber.Number(UserData)
      TaskForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("ERROR", NumberData, EveryThing.all(NumberData), errors, ChangeForm)),
        success => {
          val data: Task = TaskForm.bindFromRequest.get
          val result = data.addTask(NumberData)
          val title = "タスクが追加されました。"
          Ok(views.html.index(title, NumberData, EveryThing.all(NumberData), TaskForm, ChangeForm))
        }
      )
    }.getOrElse {
      Redirect(routes.Application.index).withNewSession
    }
  }

  def UpData(TaskNumber:Long) = Action { implicit request =>
    session.get("UserData").map { UserData =>
      val NumberData:Long = UserNumber.Number(UserData)
      ChangeForm.bindFromRequest.fold(
        errors => BadRequest(views.html.index("ERROR", NumberData, EveryThing.all(NumberData), TaskForm, errors)),
        success => {
          val data:EveryThing = ChangeForm.bindFromRequest.get
          val result = EveryThing.TaskChange(TaskNumber, data)
          val title = "タスクが変更されました。"
          Ok(views.html.index(title, NumberData, EveryThing.all(NumberData), TaskForm, ChangeForm))
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
