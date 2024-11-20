package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import java.time.{Duration, LocalDateTime, LocalTime}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

@Singleton
class SchedulerService @Inject()(alertService: AlertService)(implicit ec: ExecutionContext) {
  // Specify the time you want to run the tasks
  private val dailyExecutionTime: LocalTime = LocalTime.of(2, 19)
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  startDailyTaskScheduler()

  private def calculateInitialDelay(targetTime: LocalTime): Long = {
    val now = LocalDateTime.now()
    val targetDateTime = if (now.toLocalTime.isBefore(targetTime)) {
      now.toLocalDate.atTime(targetTime)
    } else {
      now.toLocalDate.plusDays(1).atTime(targetTime)
    }
    Duration.between(now, targetDateTime).getSeconds
  }

  private def startDailyTaskScheduler(): Unit = {
    println("inside the start daily schdeuler")
    val initialDelay = calculateInitialDelay(dailyExecutionTime)
    scheduler.scheduleAtFixedRate(
      new Runnable {
        override def run(): Unit = {
          runDailyTasks()
        }
      },
      initialDelay,
      TimeUnit.DAYS.toSeconds(1),
      TimeUnit.SECONDS
    )
  }

  // Function to call the alert functions
  private def runDailyTasks(): Unit = {
    // Call the alert functions from AlertService
    alertService.checkAndSendReminders()
    alertService.sendEventDayAlerts()
    alertService.sendProgressCheckInAlerts()
    alertService.sendIssueAlerts()

    println("Daily tasks executed successfully.")
  }
}
