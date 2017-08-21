package it.milczarek.gpwquoter.jdbc

import java.sql.Date
import java.time.LocalDate

import com.typesafe.config.ConfigFactory
import it.milczarek.gpwquoter.domain.Quote
import slick.jdbc.PostgresProfile.api._
import slick.jdbc.meta.MTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

/**
  * Created by e-bzmk on 18/08/2017.
  */
class QuotesTable(tag: Tag) extends Table[Quote](tag, "quotes") {

  private implicit val localDateToDate = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )

  def name = column[String]("name", O.Length(100))

  def date = column[LocalDate]("date_val")

  def open = column[Double]("open")

  def high = column[Double]("high")

  def low = column[Double]("low")

  def close = column[Double]("close")

  def volume = column[Double]("volume")

  def pk = primaryKey("pk_quotes", (name, date))

  def idxName = index("idx_name", name)

  def * = (name, date, open, high, low, close, volume) <> (Quote.tupled, Quote.unapply)
}

object QuoteDao {

  private val db = Database.forConfig("database", ConfigFactory.load("jdbc.conf"))
  private val quotes = TableQuery[QuotesTable]
  createSchemaIfNotExists()

  private def createSchemaIfNotExists() = {
    val tableName = quotes.baseTableRow.tableName

    def tableExists(tableName: String) = Await.result(db.run(MTable.getTables(tableName).headOption), 5000 millis).isDefined

    if (!tableExists(tableName)) waitForResult(db.run(quotes.schema.create), Duration.Inf)
  }

  def findByName(name: String): Future[Option[Quote]] = db.run(quotes.filter(_.name === name).result).map(_.headOption)

  def insertOrUpdate(quote: Quote) = waitForResult(db.run(quotes.insertOrUpdate(quote)))

  def maxDate(): Option[LocalDate] = {
    val queryMaxDate = sql"""SELECT max(date_val) FROM quotes""".as[Date]
    waitForResult(db.run(queryMaxDate).map(_.headOption)).flatMap(Option(_)).map(_.toLocalDate)
  }

  private def waitForResult[T](f: Future[T], atMost: Duration): T = Await.result(f, atMost)

  private def waitForResult[T](f: Future[T]): T = waitForResult(f, 5000 millis)

}
