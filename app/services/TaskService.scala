package services

import models.Task
import repositories.TaskRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TaskService @Inject() (taskRepository: TaskRepository) {

  def listTasks(): Future[Seq[Task]] = taskRepository.listTasks()

  def addTask(task: Task): Future[Long] = taskRepository.addTask(task)

  def getTasksByEventId(eventId: Long): Future[Seq[Task]] = taskRepository.getTasksByEventId(eventId)

  def updateTask(taskId: Long, task: Task): Future[Option[Task]] = taskRepository.updateTask(taskId, task)
}
