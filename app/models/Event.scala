package models

import play.api.libs.json._

case class Event(
                  id: Long,
                  name: String,
                  location: String,
                  noOfGuests: Int,
                  requirements: String,
                  eventDate: String
                )

object Event {
  implicit val eventFormat: Format[Event] = Json.format[Event]
}
