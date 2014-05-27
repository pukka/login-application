package controllers

import models._
import controllers._

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._


/**
 * タスクの管理を扱うオブジェクト
 */
object TaskController extends Controller {

  /**
   * ログインフォーム
   */
  val LoginForm = Form (
    tuple (
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    ) verifying ( "Invalid name or password" , result => result match {
      case ( name, password ) => User.LoginCheck ( name, password ).isDefined
    })
  )

  /**
   * タスク追加のマッピング
   */
  val TaskForm = Form (
    mapping (
      "id" -> longNumber,
      "year" -> text,
      "month" -> text,
      "date" -> text,
      "hour" -> text,
      "minute" -> text,
      "work" -> nonEmptyText
    ) ( Task.apply ) ( Task.unapply )
  )

  /**
   * タスク変更のマッピング
   */
  val ChangeForm = Form (
    mapping (
      "id" -> longNumber,
      "date" -> text,
      "time" -> text,
      "work" -> nonEmptyText
    ) ( AllTask.apply ) ( AllTask.unapply )
  )

  /**
   * ログイン後のページを表示するメソッド
   * セッションを確認後、ユーザーの名前と番号を確認し、
   * 問題がなければ、会員ページを表示
   * セッション情報がなければ、ログインページへリダイレクトする
   */
  def index = Action { implicit request =>
    val title = "Login Test"

    session.get ( "UserData" ) .map { UserData =>
      val NumberData:Long = UserNumber.Number ( UserData )
      val hello:String = "ようこそ, " + UserData + " !"
      val allTask = AllTask.all ( NumberData )
      Ok ( views.html.index ( hello, NumberData, allTask, TaskForm, ChangeForm ) )
    } .getOrElse {
      Ok ( views.html.login ( title, LoginForm ) )
    }
  }

  /**
   * ログインをチェックするメソッド
   * フォームに正しくデータがマッピングできていれば、
   * cookieにセッション情報を保存後、TaskController.indexにリダイレクト
   * エラーがあれば、BadRequestで、views.html.loginへ戻す
   */
  def login = Action { implicit request =>
    LoginForm.bindFromRequest.fold (
      errors => BadRequest ( views.html.login ( "ERROR", errors ) ) ,
      success => {
        val data: ( String, String ) = LoginForm.bindFromRequest.get
        val name: String = data._1

        Redirect ( routes.TaskController.index ) .withSession (
            session + ( "UserData" -> name )
        )
      }
    )
  }

  /**
   * ログアウトするためのメソッド
   * セッションを解放し、TaskController.indexメソッドにリダイレクト
   */
  def logout = Action {
    Redirect ( routes.TaskController.index ) .withNewSession
  }

  /**
   * タスク追加のメソッド
   * セッションがsuccessであれば、
   * modelsのdata.addTaskメソッドにフォーム内容を渡す
   */
  def createTask = Action { implicit request =>
    session.get ( "UserData" ) .map { UserData =>
      val NumberData:Long = UserNumber.Number ( UserData )
      TaskForm.bindFromRequest.fold (
        errors => BadRequest ( views.html.index ( "ERROR", NumberData, AllTask.all ( NumberData ), errors, ChangeForm ) ) ,
        success => {
          val data: Task = TaskForm.bindFromRequest.get
          val result = data.addTask ( NumberData )
          val title = "タスクが追加されました。"
          Ok ( views.html.index ( title, NumberData, AllTask.all ( NumberData ), TaskForm, ChangeForm ) )
        }
      )
    }.getOrElse {
      Redirect ( routes.TaskController.index ) .withNewSession
    }
  }

  /**
   * タスク変更のメソッド
   * セッションがsuccessであれば、
   * modelsのAllTask.changeメソッドにフォーム内容を渡す
   */
  def upData ( TaskNumber: Long ) = Action { implicit request =>
    session.get ( "UserData" ) .map { UserData =>
      val NumberData: Long = UserNumber.Number ( UserData )
      ChangeForm.bindFromRequest.fold (
        errors => BadRequest ( views.html.index ( "ERROR", NumberData, AllTask.all ( NumberData ), TaskForm, errors ) ) ,
        success => {
          val data: AllTask = ChangeForm.bindFromRequest.get
          val result = AllTask.change( TaskNumber, data )
          Redirect ( routes.TaskController.index )
        }
      )
    }.getOrElse {
      Redirect ( routes.TaskController.index ) .withNewSession
    }
  }

  /**
   * タスク削除のメソッド
   * 引数でタスク番号を受け取り、modelsのdeleteメソッドに渡す
   */
  def deleteTask ( id: Long ) = Action {
    UserNumber.delete ( id )
    Redirect ( routes.TaskController.index )
  }
}
