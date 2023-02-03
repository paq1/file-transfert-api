package models.commands

import play.api.libs.json.{JsSuccess, JsValue, Reads}

case class FileCommand(fileName: String, content: String)

object FileCommand {
  implicit val oread: Reads[FileCommand] = (js: JsValue) =>
    JsSuccess(
      FileCommand(
        (js \ "filename").as[String],
        (js \ "content").as[String]
      )
    )

}
