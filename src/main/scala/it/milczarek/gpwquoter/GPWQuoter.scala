package it.milczarek.gpwquoter

import akka.actor.{ActorSystem, Props}
import it.milczarek.gpwquoter.handler.{QuotesJdbcHandler, QuotesLoggingHandler}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

object GPWQuoter {

  private val logger = LoggerFactory.getLogger(classOf[App])
  val actorSystem = ActorSystem("GPWQuoter")

  def main(args: Array[String]) {
    logger.info("GPW quoter")

    val appConfig = new AppConfig("application.conf")
    val gpwCalendar = new GPWCalendar(appConfig)

    val quotesJdbcHandler = actorSystem.actorOf(Props[QuotesJdbcHandler], "quotesJdbcHandler")
    val quotesLoggingHandler = actorSystem.actorOf(Props[QuotesLoggingHandler], "quotesLoggingHandler")
    val gpwQuoteLoader = actorSystem.actorOf(GPWQuoteLoader.props(appConfig, gpwCalendar, List(quotesLoggingHandler, quotesJdbcHandler)), "gpwQuoteLoader")

    import actorSystem.dispatcher
    val minutesInterval = appConfig.minutesIntervalForRequestingData
    actorSystem.scheduler.schedule(0 milliseconds, minutesInterval minutes, gpwQuoteLoader, LoadQuotes)
  }
}
