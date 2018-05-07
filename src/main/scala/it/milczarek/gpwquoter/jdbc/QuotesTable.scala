package it.milczarek.gpwquoter.jdbc

import java.sql.Date
import java.time.LocalDate

import it.milczarek.gpwquoter.domain.Quote
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{Index, PrimaryKey, ProvenShape}

class QuotesTable(tag: Tag) extends Table[Quote](tag, Some("public"), "quotes") {

  private implicit val localDateToDate = MappedColumnType.base[LocalDate, Date](
    l => Date.valueOf(l),
    d => d.toLocalDate
  )

  def name: Rep[String] = column[String]("name", O.Length(100))

  def date: Rep[LocalDate] = column[LocalDate]("date_val")

  def open: Rep[Double] = column[Double]("open")

  def high: Rep[Double] = column[Double]("high")

  def low: Rep[Double] = column[Double]("low")

  def close: Rep[Double] = column[Double]("close")

  def volume: Rep[Double] = column[Double]("volume")

  def pk: PrimaryKey = primaryKey("pk_quotes", (name, date))

  def idxName: Index = index("idx_name", name)

  def * : ProvenShape[Quote] = (name, date, open, high, low, close, volume) <> (Quote.tupled, Quote.unapply)
}
