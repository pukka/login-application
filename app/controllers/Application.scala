package controllers

import play.api._
import play.api.mvc._
import play.api.data._

import play.api.data.Forms._

import models._

import play.api.libs.iteratee.Enumerator

/**
 * ログイン前の処理を管理するオブジェクト 
 */
object Application extends Controller {
    
  /** 
   * 新規ユーザ登録のマッピング
   */
  val CreateForm = Form (
    mapping (
      "name" -> nonEmptyText,
      "password" -> nonEmptyText
    ) ( User.apply ) ( User.unapply )
  )

  /**
   * 新規ユーザ登録用のページを表示するメソッド
   * リクエストがあれば、タイトルとフォームをviews.html.newuerへ渡す
   */
  def newUser = Action { implicit request =>
    val title = "新規ユーザー登録"
    Ok ( views.html.newuser ( title, CreateForm ) )
  }

  /**
   * 新しくユーザを追加するメソッド
   * フォームに値が正しくマッピングされていれば、値をmodelsのdata.addDataに渡す
   * その後、ログインページに戻る
   */
  def createUser = Action { implicit request =>
    CreateForm.bindFromRequest.fold (
      errors => BadRequest ( views.html.newuser ( "ERROR", errors ) ) ,
      success => {
        val data: User = CreateForm.bindFromRequest.get
        val result = data.addData
        Redirect ( routes.TaskController.index ) .withSession (
            session + ( "UserData" -> data.name )
        )       
      }
    )
  }
}
