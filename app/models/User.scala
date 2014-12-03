package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

/**
 * ユーザーデータ
 */
case class User(name: String, password: String)

/**
 * 登録済みタスク
 */
case class AllTask ( id: Long, date: String, time: String, work: String )

/**
 * ID検索
 */
case class UserNumber ( id: Long )

/**
 * タスク追加クラス
 * 受け取った値を結合させて、DB登録
 */ 
case class Task ( id: Long, year: String, month: String, date: String, hour: String, minute: String, work: String ){
  def addTask( id: Long ) = {
   val Date = year + month + date
   val Time = hour + minute
    DB.withConnection { implicit c =>
      val num: Int = SQL ( " insert into tasks ( user_id, date, time, work ) values ( {user_id}, {date}, {time}, {work} ) " ).
                      on ('user_id -> id, 'date -> Date, 'time -> Time, 'work -> work ).executeUpdate()
    }
  }
}

  object UserNumber {

    /**
     *  引数でユーザ名を受け取り、番号を返す
     */
    def number( name: String ): Long = {
      DB.withConnection{ implicit c =>
        val id:Long = SQL( " select id from users where name = {name} " ).
                       on( 'name -> name ).as(scalar[Long].single)
        return id;
      }
    }
  
    /*
     * 引数で番号を受け取り、タスクを削除
     */
    def delete( id: Long ) = {
      DB.withConnection { implicit c =>
        SQL(" delete from tasks where id = {id} ").on( 'id -> id ).executeUpdate()
      }
    }
  }  

  /**
   * 登録済みタスクを扱うオブジェクト
   */
  object AllTask {
    val schedule = {
      get[ Long ] ( "id" ) ~
      get[ String ] ( "date" ) ~
      get[ String ] ( "time" ) ~
      get[ String ] ( "work" ) map {
        case id ~ date ~ time ~ work => AllTask ( id, date, time, work )
      }
    }

    /**
     * 引数でユーザ番号を受け取り、そのユーザの登録済みタスクを全て検索
     * Listにして値を返す 
     */   
    def all ( id: Long ): List [ AllTask ] = {
      DB.withConnection { implicit c =>
        SQL ( " select * from tasks where user_id = {id} " ).on ( 'id -> id).as ( schedule * )
      }
    }

    /*
     * タスク変更メソッド
     * 引数でタスクの番号と変更内容を受け取り、情報を更新
     */
    def change ( taskNumber: Long, task: AllTask ) = {
      DB.withConnection { implicit c =>
        val count = SQL ( " update tasks set date = {date}, time = {time}, work = {work} where id = {id} " ).
                     on ( 'date -> task.date, 'time -> task.time, 'work -> task.work, 'id -> taskNumber ) .executeUpdate()
      }
    }
  }

  object User {
    val simple = {
      get [ String ] ( "users.name" ) ~ 
      get [ String ] ( "users.password" ) map {
        case name ~ password => User ( name, password )
      }
    }


    def addData( data: User): Boolean = {
      val result = User.findUser( data.name, data.password )
      if(result == None){
        DB.withConnection { implicit c =>
          val id: Int = SQL( "insert into users( name, password )values( {name}, {password} )").
                      on( 'name -> data.name, 'password -> data.password ).executeUpdate()
        }
        return true
      } else {
        return false
      }
   } 


    /*
     * ユーザを検索するメソッド
     * 引数でユーザ名とパスワードを受け取り、
     * 検索結果をOption型で返す
     */
    def findUser( name: String, password: String ): Option[ User ] = {
      DB.withConnection { implicit c =>
        SQL(
          """
            select * from users where
            name = {name} and password = {password}
          """
        ).on(
	  'name -> name,
	  'password -> password
        ).as( User.simple.singleOpt )
      }
    } 
  }
