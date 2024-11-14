package models

import play.api.libs.json._

case class Task(
                 id: Long,
                 eventId: Long,
                 teamId: Long,
                 taskName: String,
                 description: String,
                 assignedDate: String,
                 dueDate: String,
                 status: String
               )

object Task {
  implicit val taskFormat: Format[Task] = Json.format[Task]
}

