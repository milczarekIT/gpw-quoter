package it.milczarek.gpwquoter.file

import java.io.File
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import it.milczarek.gpwquoter.AppConfig
import org.slf4j.LoggerFactory

import scala.util.{Failure, Success, Try}

/**
  * Created by milczu on 30.01.16.
  */
class FileDataProvider(appConfig: AppConfig) {

  private val logger = LoggerFactory.getLogger(classOf[FileDataProvider])
  private val dataFilesLocation = appConfig.dataFilesLocation
  val fileDownloader = new FileDownloader(appConfig)

  def unzipFile(file: File): Unit = UnzipUtility.unzip(file, dataFilesLocation)

  def unzipFile(fileTry: Try[File]): Unit = fileTry match {
    case Success(file) => unzipFile(file)
    case Failure(e: Exception) => logger.error("Unable to unzip file", e)
  }

  def resolveDataFile(date: LocalDate): Try[File] = {
    val today = LocalDate.now

    val isCurrentMonth = today.getMonthValue == date.getMonthValue
    val isCurrentYear = today.getYear == date.getYear

    def dayPrnFileName = date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".prn"

    def monthZipFileName = date.format(DateTimeFormatter.ofPattern("MM-yyyy")) + ".zip"

    def yearZipFileName = date.format(DateTimeFormatter.ofPattern("yyyy")) + ".zip"

    def resolveRemoteFileName: String = {
      if (isCurrentYear) {
        if (isCurrentMonth) dayPrnFileName
        else {
          val dateIsFirstHalfOfYear = date.getMonth.getValue <= 6
          val nowIsSecondHalfOfYear = LocalDate.now().getMonth.getValue > 6
          val isPrevHalfYear = dateIsFirstHalfOfYear && nowIsSecondHalfOfYear
          if (isPrevHalfYear) date.getYear + "a.zip"
          else monthZipFileName
        }
      } else yearZipFileName
    }

    def fileFromName(fileName: String) = new File(dataFilesLocation + File.separator + fileName)

    def urlFromRemoteFileName = new URL(appConfig.feedProviderUrl + "/" + resolveRemoteFileName)

    val localDayPrnFile = fileFromName(dayPrnFileName)
    if (localDayPrnFile.exists) {
      Success(localDayPrnFile)
    } else {
      if (isCurrentYear) {
        if (isCurrentMonth) {
          fileDownloader.download(urlFromRemoteFileName)
        } else {
          val monthZipFile = fileFromName(monthZipFileName)
          if (monthZipFile.exists()) {
            unzipFile(monthZipFile)
            Success(localDayPrnFile)
          } else {
            val dateIsFirstHalfOfYear = date.getMonth.getValue <= 6
            val nowIsSecondHalfOfYear = LocalDate.now().getMonth.getValue > 6
            val isPrevHalfYear = dateIsFirstHalfOfYear && nowIsSecondHalfOfYear
            if (isPrevHalfYear) {
              fileDownloader.download(urlFromRemoteFileName) match {
                case Success(halfYearZipFile) =>
                  unzipFile(halfYearZipFile)
                  unzipFile(monthZipFile)
                  Success(localDayPrnFile)
                case Failure(e) => Failure(e)
              }
            } else {
              fileDownloader.download(urlFromRemoteFileName) match {
                case Success(monthZipFile) =>
                  unzipFile(monthZipFile)
                  Success(localDayPrnFile)
                case Failure(e) => Failure(e)
              }
            }
          }
        }
      } else {
        val localMonthZipFile = fileFromName(monthZipFileName)
        if (localMonthZipFile.exists) {
          unzipFile(localMonthZipFile)
          Success(localDayPrnFile)
        } else {
          val localYearZipFile: Try[File] = {
            val localYearZipFile = fileFromName(yearZipFileName)
            if (localYearZipFile.exists) Success(localYearZipFile)
            else fileDownloader.download(urlFromRemoteFileName)
          }
          unzipFile(localYearZipFile)
          unzipFile(localMonthZipFile)
          Success(localDayPrnFile)
        }
      }
    }
  }
}
