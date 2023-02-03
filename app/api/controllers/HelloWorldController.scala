package api.controllers

import play.api.mvc._

import javax.inject.Inject
import scala.concurrent.Future

class HelloWorldController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  def helloWorld(): Action[AnyContent] = Action.async {
      Future.successful(Ok("Hello world"))
  }

}
