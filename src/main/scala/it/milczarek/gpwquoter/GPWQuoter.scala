package it.milczarek.gpwquoter

import akka.actor.{ActorSystem, Props}
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 29.01.16.
  */
object GPWQuoter {

  val logger = LoggerFactory.getLogger(classOf[App])
  val actorSystem = ActorSystem("GPWQuoter")

  def main(args: Array[String]) {
	logger.info("GPW quoter")

	val appConfig = new AppConfig("application.conf")
	val gpwCalendar = new GPWCalendar(appConfig)

	val quotesHandler = actorSystem.actorOf(Props[QuotesHandler], "quotesHandler")
	val gpwQuoteLoader = actorSystem.actorOf(GPWQuoteLoader.props(appConfig, gpwCalendar, quotesHandler), "gpwQuoteLoader")

	gpwQuoteLoader ! InitGpwQuoteLoader
  }
}
