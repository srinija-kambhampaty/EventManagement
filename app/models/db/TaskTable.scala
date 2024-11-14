package models.db

import models.Task
import slick.jdbc.MySQLProfile.api._

class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def eventId = column[Long]("event_id")
  def teamId = column[Long]("team_id")
  def taskName = column[String]("task_name")
  def description = column[String]("description")
  def assignedDate = column[String]("assigned_date")
  def dueDate = column[String]("due_date")
  def status = column[String]("status")

  def * = (id, eventId, teamId, taskName, description, assignedDate, dueDate, status) <> ((Task.apply _).tupled, Task.unapply)
}
