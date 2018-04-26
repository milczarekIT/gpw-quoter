package it.milczarek.gpwquoter.domain

import java.time.LocalDate

import scala.beans.BeanProperty

case class Quote(@BeanProperty name: String, @BeanProperty date: LocalDate, @BeanProperty open: Double, @BeanProperty high: Double, @BeanProperty low: Double, @BeanProperty close: Double, @BeanProperty volume: Double)