package it.milczarek.gpwquoter

import com.typesafe.config.{Config, ConfigFactory}

class AppConfig(name: String) {

  private lazy val config = ConfigFactory.load(name).getConfig("it.milczarek.gpwquoter")

  val minDate: String = config.getString("minDate")
  val dataFilesLocation: String = config.getString("dataFilesLocation")
  val feedProviderUrl: String = config.getString("feedProviderUrl")
  val excludedTradingDays: Config = config.getConfig("calendar.excludedDays")
  val minutesIntervalForRequestingData: Int = config.getInt("minutesIntervalForRequestingData")
}
