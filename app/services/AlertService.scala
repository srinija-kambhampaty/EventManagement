package services

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import javax.inject.{Inject, Singleton}
import kafka.{PreparationReminderProducer, EventDayAlertProducer,ProgressCheckInProducer,IssueAlertProducer}
import models.Task
import repositories.{TaskRepository,EventRepository}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AlertService @Inject()(
                              taskRepository: TaskRepository,
                              eventRepository: EventRepository,
                              preparationReminderProducer: PreparationReminderProducer,
                              eventDayAlertProducer: EventDayAlertProducer,
                              progressCheckInProducer: ProgressCheckInProducer,
                              issueAlertProducer: IssueAlertProducer
                            )(implicit ec: ExecutionContext) {

  private val dateFormatter = new DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4)
    .appendLiteral('-')
    .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
    .appendLiteral('-')
    .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
    .toFormatter()


  private def stringToDate(dateStr: String): LocalDate = {
    LocalDate.parse(dateStr, dateFormatter)
  }

  def checkAndSendReminders(): Future[Unit] = {
    val currentDate = LocalDate.now()

    taskRepository.listTasks().map { tasks =>
      tasks.foreach { task =>
        val dueDate = stringToDate(task.dueDate)
        println(currentDate)

        if (dueDate.minusDays(1).isEqual(currentDate)) {
          preparationReminderProducer.sendPreparationReminder(task)
        }
      }
    }
  }

  // Function to send event day alerts
  def sendEventDayAlerts(): Future[Unit] = {
    val currentDate = LocalDate.now()
    println(s"current date: $currentDate")

    eventRepository.listEvents().flatMap { events =>
      val todayEvents = events.filter(event => stringToDate(event.eventDate).isEqual(currentDate))

      val futureTasks = todayEvents.map { event =>
        println(s"events with todays date $event")
        taskRepository.getTasksByEventId(event.id).map { tasks =>
          println(s"tasks which have the eventid: $tasks")
          val inProgressTasks = tasks.filter(_.status == "Pending")

          inProgressTasks.foreach { task =>
            println(s"tasks with inprogress: $task")
            eventDayAlertProducer.sendEventDayAlert(task)
          }
        }
      }
      Future.sequence(futureTasks).map(_ => ())
    }
  }

  // Function to send progress check-in alerts
  def sendProgressCheckInAlerts(): Future[Unit] = {
    taskRepository.listTasks().map { tasks =>
      // Filter tasks that are "In Progress"
      val inProgressTasks = tasks.filter(_.status == "Pending")
      inProgressTasks.foreach { task =>
        progressCheckInProducer.sendProgressCheckIn(task)
      }
    }
  }

  def sendIssueAlerts(): Future[Unit] = {
    taskRepository.listTasks().map { tasks =>
      // Filter tasks where the blockers field is not null
      val tasksWithIssues = tasks.filter(_.blockers.isDefined)
      tasksWithIssues.foreach { task =>
        issueAlertProducer.sendIssueAlert(task)
      }
    }
  }


}