package it.milczarek.gpwquoter.domain

import scala.beans.BeanProperty

/**
  * Created by e-bzmk on 03/08/2017.
  */
case class OHLC(@BeanProperty open: Double, @BeanProperty high: Double, @BeanProperty low: Double, @BeanProperty close: Double, @BeanProperty volume: Double)
