package it.milczarek.gpwquoter

import java.time.DayOfWeek._
import java.time.LocalDate

import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

class GPWCalendar(appConfig: AppConfig) {

  private val logger = LoggerFactory.getLogger(classOf[GPWCalendar])

  val businessDays = List(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY)

  val excludedTradingDays: List[LocalDate] = {
    val startYear = LocalDate.parse(appConfig.minDate).getYear
    val endYear = LocalDate.now().getYear
    val config = appConfig.excludedTradingDays
    var excludedDays: List[LocalDate] = List()
    for (year <- startYear to endYear) {
      if (config.hasPath(year.toString)) {
        val excludedDaysThisYear: List[LocalDate] = config.getStringList(year.toString).asScala.toList.map(LocalDate.parse)
        logger.debug(s"Excluded days for year $year: $excludedDaysThisYear")
        excludedDays = excludedDaysThisYear ::: excludedDays
      } else {
        logger.warn(s"Excluded trading days for year $year not defined")
      }
    }
    excludedDays
  }

  def nextTradingDay(date: LocalDate): LocalDate = {
    val nextCandidate = date.plusDays(1)
    if (businessDays.contains(nextCandidate.getDayOfWeek) && !excludedTradingDays.contains(nextCandidate)) nextCandidate
    else nextTradingDay(nextCandidate)
  }

}
