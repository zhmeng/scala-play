import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.ws.WS

import play.api.test._
import play.api.test.Helpers._

import scala.concurrent.{Future, Await}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
object ApplicationSpec extends Specification {

  "Application" should {

    "send 404 on a bad request" in new WithApplication{
      route(FakeRequest(GET, "/boum")) must beNone
    }

    "render the index page" in new WithApplication{
      val home = route(FakeRequest(GET, "/")).get

      status(home) must equalTo(OK)
      contentType(home) must beSome.which(_ == "text/html")
      contentAsString(home) must contain ("Your new application is ready.")
    }
  }

  "Arithetic" should {
    "add two numbers" in {
      1 + 1 mustEqual 2
    }
    "add three numbers" in {
      1 + 1 + 1 mustEqual 3
    }
  }

}

object T {
  implicit val context = scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration._

  def main(args: Array[String]): Unit = {
    val current = System.currentTimeMillis

//    for(i <- 1 to 5){
//      val rsp = WS.url("http://localhost:9000/async").get().map(
//        response => println(response.body)
//      )
//    }
    Await.result(Future{
      println("HELLO")
    }, 3 seconds)
    println((System.currentTimeMillis - current) / 1000 )
  }
}