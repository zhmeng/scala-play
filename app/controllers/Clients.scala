package controllers

import play.api.mvc.{Action, Controller}

/**
 * Created by zhangmeng on 16-5-13.
 */
object Clients extends Controller{
  def show(id: Long) = Action {
    Ok(id + "")
  }
}
