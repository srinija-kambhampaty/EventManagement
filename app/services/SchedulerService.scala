package services

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext
import java.time.{Duration, LocalDateTime, LocalTime}
import java.util.concurrent.{Executors, ScheduledExecutorService, TimeUnit}

@Singleton
class SchedulerService @Inject()(alertService: AlertService)(implicit ec: ExecutionContext) {
  // Specify the time you want to run the tasks (e.g., 8:00 AM)
  private val dailyExecutionTime: LocalTime = LocalTime.of(16, 19)
  private val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

  // Start the scheduler
  startDailyTaskScheduler()

  // Function to calculate the initial delay until the specified time
  private def calculateInitialDelay(targetTime: LocalTime): Long = {
    val now = LocalDateTime.now()
    val targetDateTime = if (now.toLocalTime.isBefore(targetTime)) {
      now.toLocalDate.atTime(targetTime) // Today at the target time
    } else {
      now.toLocalDate.plusDays(1).atTime(targetTime) // Tomorrow at the target time
    }
    Duration.between(now, targetDateTime).getSeconds // Return the delay in seconds
  }

  // Function to start the daily task scheduler
  private def startDailyTaskScheduler(): Unit = {
    println("inside the start daily schdeuler")
    val initialDelay = calculateInitialDelay(dailyExecutionTime)
    // Schedule the tasks to run daily at the specified time
    scheduler.scheduleAtFixedRate(
      new Runnable {
        override def run(): Unit = {
          runDailyTasks()
        }
      },
      initialDelay,
      TimeUnit.DAYS.toSeconds(1), // Run every 24 hours
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
