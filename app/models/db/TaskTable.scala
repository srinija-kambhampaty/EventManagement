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
  def totalTimeHours = column[Option[Double]]("total_time_hours") // New field
  def remainingHours = column[Option[Double]]("remaining_hours")  // New field
  def completedHours = column[Option[Double]]("completed_hours")  // New field
  def blockers = column[Option[String]]("blockers")              // New field

  def * = (id, eventId, teamId, taskName, description, assignedDate, dueDate, status, totalTimeHours, remainingHours, completedHours,
    blockers) <> ((Task.apply _).tupled, Task.unapply)
}
