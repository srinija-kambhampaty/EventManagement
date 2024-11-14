package repositories

import models.Event
import models.db.EventTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val events = TableQuery[EventTable]

  def listEvents(): Future[Seq[Event]] = db.run(events.result)

  def addEvent(event: Event): Future[Long] = {
    val insertQueryThenReturnId = events returning events.map(_.id)
    db.run(insertQueryThenReturnId += event)
  }
}
