package services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import models.Task
import repositories.TaskRepository
import kafka.KafkaProducerService
import kafka.{PreparationReminderProducer, EventDayAlertProducer}
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Singleton
class TaskService @Inject()(
                             taskRepository: TaskRepository,
                             kafkaProducerService: KafkaProducerService,
                             preparationReminderProducer: PreparationReminderProducer,// Inject KafkaProducerService
                             eventDayAlertProducer: EventDayAlertProducer
                           )(implicit ec: ExecutionContext) {

  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  // Function to convert a String date to LocalDate
  private def stringToDate(dateStr: String): LocalDate = {
    LocalDate.parse(dateStr, dateFormatter)
  }


  def listTasks(): Future[Seq[Task]] = taskRepository.listTasks()

  def addTask(task: Task): Future[Long] = {
    taskRepository.addTask(task).map { createdTaskId =>
      // Fetch the created task details and send a Kafka notification
      taskRepository.getTaskById(createdTaskId).foreach {
        case Some(createdTask) => kafkaProducerService.sendTaskCreationAlert(createdTask)
        case None => println("Error: Task not found after creation.")
      }
      createdTaskId
    }
  }

  def getTasksByEventId(eventId: Long): Future[Seq[Task]] = taskRepository.getTasksByEventId(eventId)

  def getTasksByTeamId(teamId: Long): Future[Seq[Task]] = taskRepository.getTasksByTeamId(teamId)

  def updateTask(taskId: Long, task: Task): Future[Option[Task]] = taskRepository.updateTask(taskId, task)

}











