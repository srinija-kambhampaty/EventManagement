package repositories

import models.Task
import models.db.TaskTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val tasks = TableQuery[TaskTable]

  // Fetch all tasks
  def listTasks(): Future[Seq[Task]] = db.run(tasks.result)

  // Add a new task
  def addTask(task: Task): Future[Long] = {
    val insertQueryThenReturnId = tasks returning tasks.map(_.id)
    db.run(insertQueryThenReturnId += task)
  }

  // Get tasks by event ID
  def getTasksByEventId(eventId: Long): Future[Seq[Task]] = {
    db.run(tasks.filter(_.eventId === eventId).result)
  }

  // Update a task by ID
  def updateTask(taskId: Long, task: Task): Future[Option[Task]] = {
    val updateQuery = tasks.filter(_.id === taskId)
      .map(t => (t.taskName, t.description, t.assignedDate, t.dueDate, t.status))
      .update((task.taskName, task.description, task.assignedDate, task.dueDate, task.status))

    db.run(updateQuery).flatMap {
      case 0 => Future.successful(None)
      case _ => db.run(tasks.filter(_.id === taskId).result.headOption)
    }
  }
}
