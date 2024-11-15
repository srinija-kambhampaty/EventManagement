package repositories

import models.Team
import models.db.TeamTable
import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TeamRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private val teams = TableQuery[TeamTable]

  // Fetch all teams
  def listTeams(): Future[Seq[Team]] = db.run(teams.result)

  // Add a new team
  def addTeam(team: Team): Future[Long] = {
    val insertQueryThenReturnId = teams returning teams.map(_.id)
    db.run(insertQueryThenReturnId += team)
  }
}
