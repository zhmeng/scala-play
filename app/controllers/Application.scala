package controllers

import java.io.File
import java.util.concurrent.TimeUnit

import play.api._
import play.api.libs.Comet
import play.api.libs.concurrent.Promise
import play.api.libs.iteratee.{Concurrent, Iteratee, Enumeratee, Enumerator}
import play.api.mvc._
import play.api.templates.Html

import scala.concurrent.Future

object Application extends Controller {

  def index = Action {
    implicit request =>
      request.session.get("connected").map {
        user =>
          Ok("Hello " + user)
      }.getOrElse{
        Unauthorized("Oops, you are not connected")
      }
  }

  def bye = Action {
    Ok("Bye").withNewSession
  }

  def welcome(name: String) = Action {
    Ok("Welcome! " + name).withSession("connected" -> name)
  }

  def demo = Action {
    Ok(views.html.demo("vv"))
  }

  def css = Action {
    Ok(views.html.css("CSS"))
  }

  def login = Action {
    Ok(views.html.bootstrap.login())
  }

  def content = Action {
    Ok(views.html.bootstrap.index(Html.empty))
  }

  def panelAndwall = Action {
    Ok(views.html.bootstrap.panelandwall())
  }

  def buttons = Action {
    Ok(views.html.bootstrap.buttons())
  }

  val echo = Action { implicit request =>
    Ok("Got request [" + request + "]").withHeaders(
      CACHE_CONTROL -> "max-page=3600",
      ETAG -> "xx"
    ).withCookies(Cookie("theme", "blue"))
  }

  def download(file: String) = Action {
    Ok(file)
  }

  def items(id: Long) = Action {
    Ok(id.toString)
  }

  def save = Action {
    Redirect(routes.Application.flashT).flashing(
      "success" -> "The item has been created"
    )
  }

  def flashT = Action {
    implicit request =>
      Ok{
        request.flash.get("success").getOrElse("Welcome!")
      }
  }

  def storeInUserFile = parse.using {
    request =>
      request.session.get("username").map { user =>
        parse.file(to = new java.io.File("/tmp/" + user + ".upload"))
      }.getOrElse{
        sys.error("You don't have the right to upload here");
      }
  }

  def bodyParserSave = Action(parse.tolerantText) {
    request =>
      Ok("Got: " + request.body)
  }

  def loggingIndex = LoggingAction {
    Ok("Hello World")
  }

  def contentNego = LoggingAction {
    implicit request =>
      render {
        case Accepts.Json() => Ok("Json")
        case Accepts.Html() => Ok("HTMl")
      }
  }

  def async = Action {
    TimeUnit.SECONDS.sleep(3)
    Ok(Thread.currentThread.getName)
  }

  import play.api.libs.concurrent.Execution.Implicits.defaultContext

  val futurePIValue: Future[Double] = Future{
    TimeUnit.SECONDS.sleep(2)
    2.0
  }
  def futureResult = Action.async {
    futurePIValue.map { pi =>
      Ok("PI value computed: " + pi)
    }
  }

  import scala.concurrent.duration._
  def timeoutResult = Action.async {
    val futureInt = futurePIValue
    val timeoutFuture = play.api.libs.concurrent.Promise.timeout("Oops", 1.second)
    Future.firstCompletedOf(Seq(futureInt, timeoutFuture)).map{
      case i: Int => Ok("Got result: " + i)
      case t: String => InternalServerError(t)
    }
  }

  def readPdf = Action {
    Ok.sendFile(
      content = new File("/srv/ftp/upload/books/Docker.in.Action.2016.3.pdf"),
      inline = true
    )
  }

  def chunked = Action {
    Ok.chunked(
      Enumerator("kiki", "foo", "bar").andThen(Enumerator.eof)
    )
  }

  val toCometMsg = Enumeratee.map[String] { data =>
    Html("""<script>alert('""" + data + """')</script>""")
  }

  lazy val clock: Enumerator[String] = {
    import java.util._
    import java.text._
    val dataFormat = new SimpleDateFormat("HH mm ss")
    Enumerator.generateM{
      Promise.timeout(Some(dataFormat.format(new Date())), 1000 milliseconds)
    }
  }

  def comet = Action {
    val events = Enumerator("kiki", "foo", "bar")
    Ok.chunked(clock &> Comet(callback = "parent.cometMessage"))
  }


  def cometIndex = Action {
    Ok(views.html.comet())
  }
  import play.api.mvc._
  import play.api.Play.current

  def socket = WebSocket.using[String] { request =>
    val (out, channel) = Concurrent.broadcast[String]
    val in = Iteratee.foreach[String]{
      msg => println(msg)
      channel.push("I received your message: " + msg)
    }
    (in, out)
  }

  def todo = Action {
    Ok(views.html.todo.index())
  }

  def requireRemote = Action {
    Ok(views.html.requirejs.requireremote())
  }

  def requireRemoteTpl = Action {
    Ok(views.html.requirejs.remote())
  }

}

case class Logging[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[SimpleResult] = {
    Logger.info("Calling action " + request.uri)
    action(request)
  }
  lazy val parser = action.parser
}
object LoggingAction extends ActionBuilder[Request] {

  override protected def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[SimpleResult]): Future[SimpleResult] = {
    block(request)
  }

  override protected def composeAction[A](action: Action[A]): Action[A] = Logging(action)
}
