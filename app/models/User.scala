package models

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(name: String, password: String){
  def addData {
  DB.withConnection { implicit c =>
     val id: Int = SQL("insert into users (name, password) values ({name},{password})").
                    on('name -> name, 'password -> password).executeUpdate()
  }
 } 
}

case class EveryThing(id: Long, date: String, time: String, work: String)
  object EveryThing {
    val schedule = {
      get[Long]("id") ~
      get[String]("date") ~
      get[String]("time") ~
      get[String]("work") map {
        case id ~ date ~ time ~ work => EveryThing(id, date, time, work)
      }
    }
    
    def all(id:Long): List[EveryThing] = {
      DB.withConnection { implicit c =>
        SQL("select * from tasks where user_id = {id}").on('id -> id).as(schedule *)
      }
    }
  }

class UserNumber(id: Long)
object UserNumber {
  def Number(name:String): Long = {
    DB.withConnection { implicit c =>
      val id:Long = SQL("select id from users where name = {name}").on('name -> name).as(scalar[Long].single)
      return id ;
    }
  }

  def delete(id:Long) {
    DB.withConnection { implicit c =>
      SQL("delete from tasks where id = {id}").on('id -> id).executeUpdate()
    }
  }
}


case class Task(id: Long, year: String, month: String, date: String, hour: String, minute: String, work: String) {
  def addTask(id:Long) = {
   val Date = year + month + date
   val Time = hour + minute
    DB.withConnection { implicit c =>
      val num: Int = SQL("insert into tasks (user_id, date, time, work) values ({user_id},{date},{time},{work})").
                    on('user_id -> id, 'date -> Date, 'time -> Time, 'work -> work).executeUpdate()
    }
  }
}

object User {
  val simple = {
    get[String]("users.name") ~ 
    get[String]("users.password") map {
      case name ~ password => User(name, password)
    }
  }

  def LoginCheck(name: String, password: String): Option[User] = {
  DB.withConnection { implicit c =>
    SQL(
      """
        select * from users where
        name = {name} and password = {password}
      """
    ).on(
	'name -> name,
	'password -> password
      ).as(User.simple.singleOpt)
  }
 }
}

