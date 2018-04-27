package it.milczarek.gpwquoter.handler

import java.util.Collections

import akka.actor.Actor
import com.hazelcast.config.{Config, ManagementCenterConfig}
import com.hazelcast.core.{Hazelcast, HazelcastInstance, IMap}
import it.milczarek.gpwquoter.domain.{DateNameKey, OHLC, Quote}

class QuotesHazelcastHandler extends Actor {

  private val config = initHazelcastConfig
  private val h: HazelcastInstance = Hazelcast.newHazelcastInstance(config)

  private def initHazelcastConfig: Config = {
    val config = new Config().setProperty("hazelcast.logging.type", "slf4j")
    config.setInstanceName("hz-demo")
    val joinConfig = config.getNetworkConfig.getJoin
    joinConfig.getMulticastConfig.setEnabled(false)
    joinConfig.getTcpIpConfig.setEnabled(true).setMembers(Collections.singletonList("127.0.0.1"))
    config.setManagementCenterConfig(new ManagementCenterConfig().setEnabled(true).setUrl("http://localhost:8080/mancenter"))
    config
  }

  val quotesMap: IMap[DateNameKey, OHLC] = h.getMap("quotes")

  override def receive: Receive = {
    case q: Quote => quotesMap.put(DateNameKey(q.date, q.name), OHLC(q.open, q.high, q.low, q.close, q.volume))
  }
}