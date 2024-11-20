package controllers

import models.Task
import play.api.mvc._
import services.{TaskService,AlertService}
import play.api.libs.json._
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class TaskController @Inject()(
                                val cc: ControllerComponents,
                                taskService: TaskService,
                                alertService: AlertService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // GET /tasks - Fetch all tasks
//  def listTasks(): Action[AnyContent] = Action.async {
//    taskService.listTasks().map(tasks => Ok(Json.toJson(tasks)))
//  }


  def listTasks(): Action[AnyContent] = Action.async {
    taskService.listTasks().map { tasks =>
      Ok(views.html.tasksList(tasks.toList))
    }
  }

  // POST /tasks - Add a new task and send a Kafka message
  def addTask(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Task] match {
      case JsSuccess(task, _) =>
        taskService.addTask(task).map { createdTask =>
          Ok(Json.toJson(createdTask))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid task data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  // GET /events/:eventId/tasks - Fetch tasks by event ID
  def getTasksByEventId(eventId: Long): Action[AnyContent] = Action.async {
    taskService.getTasksByEventId(eventId).map(tasks => Ok(views.html.tasksList(tasks.toList)))
  }

  // PUT /tasks/:taskId - Update a specific task
  def updateTask(taskId: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Task] match {
      case JsSuccess(task, _) =>
        taskService.updateTask(taskId, task).map {
          case Some(updated) => Ok(Json.toJson(updated))
          case None => NotFound(Json.obj("message" -> s"Task with id $taskId not found"))
        }
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid task data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  //functions to test alerts instead of scheduler
/*
  def sendPreparationReminders() = Action.async {
    alertService.checkAndSendReminders().map { _ =>
      Ok("Preparation reminders have been sent.")
    }
  }

  def sendEventDayAlerts() = Action.async {
    alertService.sendEventDayAlerts().map { _ =>
      Ok("Event day alerts have been sent.")
    }
  }

  def sendProgressCheckInAlerts() = Action.async {
    alertService.sendProgressCheckInAlerts().map { _ =>
      Ok("Progress check-in alerts have been sent.")
    }
  }

  def sendIssueAlerts() = Action.async {
    alertService.sendIssueAlerts().map { _ =>
      Ok("Issue alerts have been sent.")
    }
  }
 */

}
