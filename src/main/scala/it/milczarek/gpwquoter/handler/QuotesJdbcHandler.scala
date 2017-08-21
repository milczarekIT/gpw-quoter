package it.milczarek.gpwquoter.handler

import akka.actor.Actor
import it.milczarek.gpwquoter.domain.Quote
import it.milczarek.gpwquoter.jdbc.QuoteDao
import org.slf4j.LoggerFactory

/**
  * Created by e-bzmk on 18/08/2017.
  */
class QuotesJdbcHandler extends Actor {

  private val logger = LoggerFactory.getLogger(classOf[QuotesJdbcHandler])
  val quoteDao = QuoteDao

  override def receive: Receive = {
    case q: Quote =>
      logger.trace(s"Quote: $q")
      quoteDao insertOrUpdate q
  }

}
