package it.milczarek.gpwquoter.domain

import java.time.LocalDate

/**
  * Created by milczu on 30.01.16.
  */
case class Quote(name: String, date: LocalDate, open: Double, high: Double, low: Double, close: Double, volume: Double)
