package it.milczarek.gpwquoter.domain

import scala.beans.BeanProperty

case class OHLC(@BeanProperty open: Double, @BeanProperty high: Double, @BeanProperty low: Double, @BeanProperty close: Double, @BeanProperty volume: Double)
