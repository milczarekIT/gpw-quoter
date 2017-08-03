package it.milczarek.gpwquoter.file

import java.io.File
import java.net.URL
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import it.milczarek.gpwquoter.AppConfig
import org.apache.http.client.fluent.Request
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
  * Created by milczu on 30.01.16.
  */
class FileDownloader(appConfig: AppConfig) {

  private val logger = LoggerFactory.getLogger(classOf[FileDownloader])

  def download(url: URL): Try[File] = {
    try {
      logger.info(s"Download file: $url")
      val response = Request.Get(url.toString).execute().returnContent().asBytes()
      val file = Files.write(outputFile(url), response, StandardOpenOption.CREATE).toFile
      Success(file)
    } catch {
      case e: Throwable => Failure(e)
    }
  }

  def outputFile(url: URL): Path = {
    val dir = new File(appConfig.dataFilesLocation)
    if (!dir.exists) dir.mkdirs()
    val uri = url.getFile
    val startFileName = uri.lastIndexOf("/") + 1
    Paths.get(appConfig.dataFilesLocation, uri.substring(startFileName))
  }

}
