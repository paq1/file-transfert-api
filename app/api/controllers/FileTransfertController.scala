package api.controllers

import api.services.FileTransfertServiceApache
import models.commands.FileCommand
import play.api.Configuration
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

@Singleton
class FileTransfertController @Inject() (
    val controllerComponents: ControllerComponents,
    val configuration: Configuration
)(implicit ec: ExecutionContext)
  extends BaseController {

  val fileTransfertService = new FileTransfertServiceApache(configuration)

  def transfertFile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    val fileCommand: FileCommand = request.body.asJson.get.as[FileCommand]

    fileTransfertService
      .transfertFile(fileCommand)
      .map { _ =>
        Ok("transfert réussi !")
      }
      .recoverWith {
        case NonFatal(err) => Future.successful(InternalServerError)
      }
  }

}
