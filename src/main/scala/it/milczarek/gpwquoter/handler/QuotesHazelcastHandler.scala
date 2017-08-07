package it.milczarek.gpwquoter.handler

import akka.actor.Actor
import com.hazelcast.config.Config
import com.hazelcast.core.{Hazelcast, HazelcastInstance, IMap}
import it.milczarek.gpwquoter.domain.{DateNameKey, OHLC, Quote}

/**
  * Created by milczu on 30.01.16.
  */
class QuotesHazelcastHandler extends Actor {

  val config = new Config()

  val h: HazelcastInstance = Hazelcast.newHazelcastInstance(config)
  val quotesMap: IMap[DateNameKey, OHLC] = h.getMap("quotes")

  override def receive: Receive = {
    case q: Quote => quotesMap.put(DateNameKey(q.date, q.name), OHLC(q.open, q.high, q.low, q.close, q.volume))
  }
}