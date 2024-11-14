package controllers

import models.Task
import play.api.mvc._
import services.TaskService
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TaskController @Inject()(
                                val cc: ControllerComponents,
                                taskService: TaskService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // GET /tasks - Fetch all tasks
  def listTasks(): Action[AnyContent] = Action.async {
    taskService.listTasks().map(tasks => Ok(Json.toJson(tasks)))
  }

  // POST /tasks - Add a new task
  def addTask(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Task] match {
      case JsSuccess(task, _) =>
        taskService.addTask(task).map(created => Created(Json.toJson(created)))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid task data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  // GET /events/:eventId/tasks - Fetch tasks by event ID
  def getTasksByEventId(eventId: Long): Action[AnyContent] = Action.async {
    taskService.getTasksByEventId(eventId).map(tasks => Ok(Json.toJson(tasks)))
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
}