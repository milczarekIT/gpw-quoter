package it.milczarek.gpwquoter

import akka.actor.Actor
import it.milczarek.gpwquoter.domain.Quote
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 30.01.16.
  */
class QuotesHandler extends Actor {

  val logger = LoggerFactory.getLogger(classOf[QuotesHandler])

  override def receive: Receive = {
	case q: Quote => logger.trace(s"Handled quote: $q")
	case a: Any => logger.warn(s"Handled unexpected message: $a")
  }
}
