package services

import models.Team
import repositories.TeamRepository
import javax.inject.{Inject, Singleton}
import scala.concurrent.Future

@Singleton
class TeamService @Inject()(teamRepository: TeamRepository) {

  def listTeams(): Future[Seq[Team]] = teamRepository.listTeams()

  def addTeam(team: Team): Future[Long] = teamRepository.addTeam(team)
}
