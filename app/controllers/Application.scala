package controllers

import play.api._
import play.api.mvc._
import play.api.templates.Html

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
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

}