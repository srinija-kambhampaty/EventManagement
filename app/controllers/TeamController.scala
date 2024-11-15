package controllers

import models.Team
import play.api.mvc._
import services.{TeamService, TaskService}
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamController @Inject()(
                                val cc: ControllerComponents,
                                teamService: TeamService,
                                taskService: TaskService
                              )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // GET /teams - Fetch all teams
  def listTeams(): Action[AnyContent] = Action.async {
    teamService.listTeams().map(teams => Ok(Json.toJson(teams)))
  }

  // POST /teams - Add a new team
  def addTeam(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Team] match {
      case JsSuccess(team, _) =>
        teamService.addTeam(team).map(created => Created(Json.toJson(created)))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid team data",
          "errors" -> JsError.toJson(errors))))
    }
  }

  // GET /teams/:id/tasks - Fetch tasks by team ID
  def getTasksByTeamId(teamId: Long): Action[AnyContent] = Action.async {
    taskService.getTasksByTeamId(teamId).map(tasks => Ok(Json.toJson(tasks)))
  }
}
