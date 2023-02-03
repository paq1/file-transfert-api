package core.services

import scala.concurrent.Future

trait FileTransfertService[Client, File] {
  def connect(): Future[Client]
  def transfertFile(fileName: File): Future[Unit]
}
