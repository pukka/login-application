package controllers

import play.api._
import play.api.mvc._
import play.api.data._

import play.api.data.Forms._

import models._

import play.api.libs.iteratee.Enumerator

object Application extends Controller {
  val LoginForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    )verifying("Invalid name or password", result => result match {
      case(name, password) => User.LoginCheck(name,password).isDefined
    })
  )

  val CreateForm = Form(
    mapping(
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    )(User.apply)(User.unapply)
  )

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

  def index = Action { implicit request =>
    val title = "Login Test"
    
    session.get("UserData").map { UserData =>
      val NumberData:Long = UserNumber.Number(UserData)
      val hello:String = "Hello, " + UserData + " !"
      val allTask = EveryThing.all(NumberData)
      Ok(views.html.index(hello, NumberData, allTask, TaskForm, ChangeForm))
    }.getOrElse {
      Ok(views.html.login(title, LoginForm))
    }
  }

  def login = Action { implicit request =>
    LoginForm.bindFromRequest.fold (
      errors => BadRequest(views.html.login("ERROR",errors)),
      success => {
        val data:(String,String) = LoginForm.bindFromRequest.get
        val name:String = data._1
        
        Redirect(routes.Application.index).withSession (
            session + ("UserData" -> name)
        )
      }
    )
  }
 
  def logout = Action {
    Redirect(routes.Application.index).withNewSession
  }

  def NewUser = Action { implicit request =>
    val title = "新規ユーザー登録"
    Ok(views.html.newuser(title, CreateForm))
  }

  def CreateUser = Action { implicit request =>
    CreateForm.bindFromRequest.fold (
      errors => BadRequest(views.html.newuser("ERROR",errors)),
      success => {
        val data: User = CreateForm.bindFromRequest.get
        val result = data.addData
        val title = "再度ログインをお願いします。"
        Ok(views.html.login(title, LoginForm))
      }
    )
  }
}
