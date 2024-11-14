package models.db

import models.Event
import slick.jdbc.MySQLProfile.api._

class EventTable(tag: Tag) extends Table[Event](tag, "events") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def location = column[String]("location")
  def noOfGuests = column[Int]("no_of_guests")
  def requirements = column[String]("requirements")
  def eventDate = column[String]("event_date")

  def * = (id, name, location, noOfGuests, requirements, eventDate) <> ((Event.apply _).tupled, Event.unapply)
}
