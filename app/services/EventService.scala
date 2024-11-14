package services

import models.{Event, Task}
import repositories.{EventRepository, TaskRepository}
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class EventService @Inject() (
                               eventRepository: EventRepository,
                               taskRepository: TaskRepository
                             ) {

  def listEvents(): Future[Seq[Event]] = eventRepository.listEvents()

  def addEvent(event: Event): Future[Long] = eventRepository.addEvent(event)

  def getTasksByEventId(eventId: Long): Future[Seq[Task]] = taskRepository.getTasksByEventId(eventId)

  def updateTask(taskId: Long, task: Task): Future[Option[Task]] = taskRepository.updateTask(taskId, task)
}

