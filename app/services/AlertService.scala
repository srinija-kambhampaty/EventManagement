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
                              eventRepository: EventRepository, // Inject the EventRepository
                              preparationReminderProducer: PreparationReminderProducer,
                              eventDayAlertProducer: EventDayAlertProducer,
                              progressCheckInProducer: ProgressCheckInProducer,
                              issueAlertProducer: IssueAlertProducer
                            )(implicit ec: ExecutionContext) {
  // A more flexible DateTimeFormatter that handles single-digit months and days
  private val dateFormatter = new DateTimeFormatterBuilder()
    .appendValue(ChronoField.YEAR, 4)
    .appendLiteral('-')
    .appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
    .appendLiteral('-')
    .appendValue(ChronoField.DAY_OF_MONTH, 1, 2, java.time.format.SignStyle.NOT_NEGATIVE)
    .toFormatter()

  // Function to convert a String date to LocalDate
  private def stringToDate(dateStr: String): LocalDate = {
    LocalDate.parse(dateStr, dateFormatter)
  }

  def checkAndSendReminders(): Future[Unit] = {
    val currentDate = LocalDate.now()

    taskRepository.listTasks().map { tasks =>
      tasks.foreach { task =>
        // Convert the dueDate from String to LocalDate
        val dueDate = stringToDate(task.dueDate)
        println(currentDate)

        // Check if the current date is one day before the due date
        if (dueDate.minusDays(1).isEqual(currentDate)) {
          // Send a reminder one day before the due date
          preparationReminderProducer.sendPreparationReminder(task)
        }
      }
    }
  }

  // Function to send event day alerts
  def sendEventDayAlerts(): Future[Unit] = {
    val currentDate = LocalDate.now()
    println(s"current date: $currentDate")

    // Fetch all events with event date equal to the current date
    eventRepository.listEvents().flatMap { events =>
      val todayEvents = events.filter(event => stringToDate(event.eventDate).isEqual(currentDate))

      // For each event on today's date, fetch the associated tasks
      val futureTasks = todayEvents.map { event =>
        println(s"events with todays date $event")
        taskRepository.getTasksByEventId(event.id).map { tasks =>
          // Filter tasks that are "In Progress"
          println(s"tasks which have the eventid: $tasks")
          val inProgressTasks = tasks.filter(_.status == "Pending")

          // Send each in-progress task to the Kafka topic
          inProgressTasks.foreach { task =>
            println(s"tasks with inprogress: $task")
            eventDayAlertProducer.sendEventDayAlert(task)
          }
        }
      }
      // Combine all futures and return
      Future.sequence(futureTasks).map(_ => ())
    }
  }

  // Function to send progress check-in alerts
  def sendProgressCheckInAlerts(): Future[Unit] = {
    taskRepository.listTasks().map { tasks =>
      // Filter tasks that are "In Progress"
      val inProgressTasks = tasks.filter(_.status == "Pending")
      // Send each in-progress task to the Kafka topic
      inProgressTasks.foreach { task =>
        progressCheckInProducer.sendProgressCheckIn(task)
      }
    }
  }

  def sendIssueAlerts(): Future[Unit] = {
    taskRepository.listTasks().map { tasks =>
      // Filter tasks where the blockers field is not null
      val tasksWithIssues = tasks.filter(_.blockers.isDefined)

      // Send each task with an issue to the Kafka topic
      tasksWithIssues.foreach { task =>
        issueAlertProducer.sendIssueAlert(task)
      }
    }
  }


}