package it.milczarek.gpwquoter

import java.nio.charset.CodingErrorAction
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import it.milczarek.gpwquoter.domain.Quote

import scala.io.{Codec, Source}

/**
  * Created by milczu on 30.01.16.
  */
class QuoteParser {

  private val dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
  private val decoder = Codec.UTF8.decoder.onMalformedInput(CodingErrorAction.IGNORE)

  implicit val codec = Codec("UTF-8")
  codec.onMalformedInput(CodingErrorAction.REPLACE)
  codec.onUnmappableCharacter(CodingErrorAction.REPLACE)

  def parse(path: Path): Seq[Quote] = Source.fromFile(path.toFile)(decoder).getLines().map(convert).toSeq

  def convert(line: String): Quote = {
    val items = line.split(',')
    Quote(items(0), LocalDate.parse(items(1), dateFormatter), items(2).toDouble, items(3).toDouble, items(4).toDouble, items(5).toDouble, items(6).toDouble)
  }

}
