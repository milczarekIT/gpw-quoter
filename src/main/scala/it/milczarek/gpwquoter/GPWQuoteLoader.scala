package it.milczarek.gpwquoter

import java.nio.file.{NoSuchFileException, Paths}
import java.time.LocalDate

import akka.actor.{Actor, ActorRef, Props}
import it.milczarek.gpwquoter.domain.Quote
import it.milczarek.gpwquoter.file.FileDataProvider
import it.milczarek.gpwquoter.jdbc.QuoteDao
import org.apache.http.client.HttpResponseException
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success}

/**
  * Created by milczu on 29.01.16.
  */
class GPWQuoteLoader(appConfig: AppConfig, gpwCalendar: GPWCalendar, quotesHandlers: List[ActorRef]) extends Actor {

  private val logger = LoggerFactory.getLogger(classOf[GPWQuoteLoader])
  val quoteParser = new QuoteParser
  val fileDataProvider = new FileDataProvider(appConfig)
  val quotesDao = QuoteDao
  var lastProcessedDate: Option[LocalDate] = quotesDao.maxDate().map(_.minusDays(1))

  override def receive: Receive = {
    case LoadQuotes => initGpwQuoteLoader()
  }

  def initGpwQuoteLoader() {
    logger.info("Init GPW quotes loading")
    loadData(findDateRangeToLoad)
  }

  def loadData(range: Option[(LocalDate, LocalDate)]) = range match {
    case None => logger.info("Nothing to load")
    case Some(x) =>
      val startDate = x._1
      val endDate = x._2
      logger.info(s"Load data for dates $startDate - $endDate")

      def loadNext(date: LocalDate): Unit = {
        if (!date.isAfter(LocalDate.now())) {
          loadDataForDay(date)
          if (date.isBefore(endDate)) loadNext(gpwCalendar.nextTradingDay(date))
        }
      }

      loadNext(startDate)
  }

  def loadDataForDay(date: LocalDate) = {
    logger.info(s"Load data for date: $date")
    try {
      fileDataProvider.resolveDataFile(date) match {
        case Success(file) =>
          val quotes = quoteParser.parse(Paths.get(file.getAbsolutePath))
          logger.info(s"Parsed ${quotes.size} quotes for date: $date")
          quotes.foreach(q => quotesHandlers.foreach(h => h ! q))
          lastProcessedDate = lastProcessedDate match {
            case Some(processedDate) => if (date isAfter processedDate) Some(date) else lastProcessedDate
            case None => Some(date)
          }
        case Failure(e: HttpResponseException) =>
          if (date == LocalDate.now()) logger.info("File for today not available yet")
          else logger.info(s"File for date $date unresolved (1)", e)
        case Failure(e) =>
          logger.info(s"File for date $date unresolved", e)
      }
    } catch {
      case e: NoSuchFileException => logger.warn(s"Unable parse file for date: $date", e)
    }
  }

  def findDateRangeToLoad: Option[(LocalDate, LocalDate)] = {
    def lastBusinessDay: LocalDate = {
      def findNearestBusinessDay(candidate: LocalDate): LocalDate = {
        if (gpwCalendar.businessDays.contains(candidate.getDayOfWeek)) candidate
        else findNearestBusinessDay(candidate.minusDays(1))
      }

      findNearestBusinessDay(LocalDate.now())
    }

    val startDate: LocalDate = lastProcessedDate match {
      case Some(date) => gpwCalendar.nextTradingDay(date)
      case None => LocalDate.parse(appConfig.minDate)
    }
    val endDate = lastBusinessDay
    Some((startDate, endDate))
  }

}

object GPWQuoteLoader {

  def props(appConfig: AppConfig, gpwCalendar: GPWCalendar, quotesHandlers: List[ActorRef]) = Props(new GPWQuoteLoader(appConfig, gpwCalendar, quotesHandlers))
}

case object LoadQuotes

case class QuotesBulkMessage(quotes: Seq[Quote])
