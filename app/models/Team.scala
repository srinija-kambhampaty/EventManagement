package models

import play.api.libs.json._

case class Team(
                 id: Long,
                 name: String,
                 teamLeadEmail: String
               )

object Team {
  implicit val teamFormat: Format[Team] = Json.format[Team]
}
