package it.milczarek.gpwquoter.handler

import akka.actor.Actor
import it.milczarek.gpwquoter.domain.Quote
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 30.01.16.
  */
class QuotesLoggingHandler extends Actor {

  private val logger = LoggerFactory.getLogger(classOf[QuotesLoggingHandler])

  override def receive: Receive = {
    case q: Quote => logger.debug(s"Handled quote: $q")
    case a: Any => logger.warn(s"Handled unexpected message: $a")
  }
}