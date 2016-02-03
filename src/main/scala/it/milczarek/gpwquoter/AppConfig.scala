package it.milczarek.gpwquoter

import com.typesafe.config.ConfigFactory

/**
  * Created by milczu on 29.01.16.
  */
class AppConfig(name: String) {

  private lazy val config = ConfigFactory.load(name).getConfig("it.milczarek.gpwquoter")

  val minDate = config.getString("minDate")
  val dataFilesLocation = config.getString("dataFilesLocation")
  val feedProviderUrl = config.getString("feedProviderUrl")
  val excludedTradingDays = config.getConfig("calendar.excludedDays")
}
