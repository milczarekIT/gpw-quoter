package it.milczarek.gpwquoter.domain

import java.time.LocalDate

import scala.beans.BeanProperty

case class DateNameKey(@BeanProperty date: LocalDate, @BeanProperty name: String) extends Serializable
