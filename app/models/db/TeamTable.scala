package models.db

import models.Team
import slick.jdbc.MySQLProfile.api._

class TeamTable(tag: Tag) extends Table[Team](tag, "teams") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def teamLeadEmail = column[String]("team_lead_email")

  def * = (id, name, teamLeadEmail) <> ((Team.apply _).tupled, Team.unapply)
}
