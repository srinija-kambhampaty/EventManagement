package controllers

import models.Event
import play.api.mvc._
import services.EventService
import play.api.libs.json._

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventController @Inject()(
                                 val cc: ControllerComponents,
                                 eventService: EventService
                               )(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // GET /events - Fetch all events
//  def listEvents(): Action[AnyContent] = Action.async {
//    eventService.listEvents().map(events => Ok(Json.toJson(events)))
//  }

  def listEvents(): Action[AnyContent] = Action.async {
    eventService.listEvents().map { events =>
      Ok(views.html.eventList(events.toList))
    }
  }


  def addEvent(): Action[JsValue] = Action.async(parse.json) { request =>
    request.body.validate[Event] match {
      case JsSuccess(event, _) =>
        eventService.addEvent(event).map(created => Created(Json.toJson(created)))
      case JsError(errors) =>
        Future.successful(BadRequest(Json.obj(
          "message" -> "Invalid event data",
          "errors" -> JsError.toJson(errors))))
    }
  }
}
