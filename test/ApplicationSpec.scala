import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    /** routesのテスト  */
    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome("text/html")
    }

    "render the newuser page" in new WithApplication{
      val home = route(FakeRequest(GET, "/new_user")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome("text/html")
    }

    "render the login page" in new WithApplication{
      val home = route(FakeRequest(POST, "/sendform")).get
      
      status(home) must equalTo(400)
      contentType(home) must beSome("text/html")
    }

    /** controllersのテスト  */

    "respond to the index Action" in {
      val result = controllers.TaskController.index()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }

    "respond to the newUser Action" in {
      val result = controllers.Application.newUser()(FakeRequest())

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
      contentAsString(result) must contain("新規ユーザー登録")
    }

    "respond to the createUser Action" in {
      val result = controllers.Application.createUser()(FakeRequest())

      status(result) must equalTo(400)
      contentType(result) must beSome("text/html")
      charset(result) must beSome("utf-8")
    }

    /** TaskControllerのテスト  */

    "respond to the login Action" in {
     val result = controllers.TaskController.login()(FakeRequest())

     status(result) must equalTo(400)
     contentType(result) must beSome("text/html")
     charset(result) must beSome("utf-8")
    }

    "respond to the logout Action" in {
      val result = controllers.TaskController.logout()(FakeRequest())

      status(result) must equalTo(303)
    }

    "respond to the createTask Action" in {
     val result = controllers.TaskController.createTask()(FakeRequest())

     status(result) must equalTo(400)
     contentType(result) must beSome("text/html")
     charset(result) must beSome("utf-8")
    }

    "respond to the upData Action" in {
      val result = controllers.TaskController.upData()(FakeRequest())

     status(result) must equalTo(400)
     contentType(result) must beSome("text/html")
     charset(result) must beSome("utf-8")
    }

    "respond to the deleteTask Action" in {
      val result = controllers.TaskController.deleteTask()(FakeRequest())

      status(result) must equalTo(303)
    }
  }
}
