package it.milczarek.gpwquoter

import java.nio.file.{NoSuchFileException, Paths}
import java.time.LocalDate

import akka.actor.{Actor, ActorRef, Props}
import it.milczarek.gpwquoter.file.FileDataProvider
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 29.01.16.
  */
class GPWQuoteLoader(appConfig: AppConfig, gpwCalendar: GPWCalendar, quotesHandler: ActorRef) extends Actor {

  val logger = LoggerFactory.getLogger(classOf[GPWQuoteLoader])
  val quoteParser = new QuoteParser
  val fileDataProvider = new FileDataProvider(appConfig)

  override def receive: Receive = {
	case InitGpwQuoteLoader => initGpwQuoteLoader()
  }

  def initGpwQuoteLoader() = loadData(findDateRangeToLoad)

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
		case Some(file) =>
		  val quotes = quoteParser.parse(Paths.get(file.getAbsolutePath))
		  logger.info(s"Parsed ${quotes.size} quotes for date: $date")
		  quotes.foreach(quotesHandler ! _)
		case None =>
		  logger.info(s"File for date $date unresolved")
	  }


	} catch {
	  case e: NoSuchFileException => logger.warn(s"Unable parse file for date: $date")
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
	def lastProcessedDate: Option[LocalDate] = None

	val startDate: LocalDate = lastProcessedDate match {
	  case Some(date) => date
	  case None => LocalDate.parse(appConfig.minDate)
	}
	val endDate = lastBusinessDay

	if (startDate == endDate) None
	else Some((startDate, endDate))
  }

}

object GPWQuoteLoader {

  def props(appConfig: AppConfig, gpwCalendar: GPWCalendar, quotesHandler: ActorRef) = Props(new GPWQuoteLoader(appConfig, gpwCalendar, quotesHandler))
}

case object InitGpwQuoteLoader
