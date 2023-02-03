package api.services

import core.services.FileTransfertService
import models.commands.FileCommand
import org.apache.commons.net.ftp.FTPClient
import play.api.Configuration

import java.io.ByteArrayInputStream
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

case class MonExceptionDeConnectionFTP()
    extends Exception("erreur lors de la connection")

case class MonExceptionDeTransfertDeFichier()
    extends Exception("erreur lors du transfert")

class FileTransfertServiceApache(
    configuration: Configuration
)(implicit ec: ExecutionContext) extends FileTransfertService[FTPClient, FileCommand] {
  val serverName: String = configuration.underlying.getString("ftp.host")
  val port: Int = configuration.underlying.getInt("ftp.port")
  val userName: String = configuration.underlying.getString("ftp.user")
  val password: String = configuration.underlying.getString("ftp.password")
  val serverFolderPath: String =
    configuration.underlying.getString("ftp.serverFolderPath")

  override def connect(): Future[FTPClient] = {
    Try({
      val client = new FTPClient()
      client.connect(serverName, port)
      if (client.login(userName, password)) {
        Future.successful(client)
      } else {
        Future.failed(MonExceptionDeConnectionFTP())
      }
    }) match {
      case Success(value: Future[FTPClient]) => value
      case Failure(th) => Future.failed(th)
    }
  }

  override def transfertFile(file: FileCommand): Future[Unit] = {
    connect()
      .flatMap { client =>
        Try({
          client.storeFile(
            s"$serverFolderPath${file.fileName}",
            new ByteArrayInputStream(file.content.getBytes())
          )
        }) match {
          case Success(res) =>
            if (res) {
              Future.successful(())
            } else {
              Future.failed(MonExceptionDeTransfertDeFichier())
            }
          case _ => Future.failed(MonExceptionDeTransfertDeFichier())
        }
      }
  }
}
