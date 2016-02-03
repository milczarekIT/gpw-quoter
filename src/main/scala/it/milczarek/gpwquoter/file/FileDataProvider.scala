package it.milczarek.gpwquoter.file

import java.io.File
import java.net.URL
import java.nio.file.Path
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import it.milczarek.gpwquoter.AppConfig
import org.apache.http.client.HttpResponseException
import org.slf4j.LoggerFactory

/**
  * Created by milczu on 30.01.16.
  */
class FileDataProvider(appConfig: AppConfig) {

  val logger = LoggerFactory.getLogger(classOf[FileDataProvider])
  val dataFilesLocation = appConfig.dataFilesLocation
  val feedProviderUrl = appConfig.feedProviderUrl
  val fileDownloader = new FileDownloader(appConfig)

  def unzipFile(file: File, directory: String = dataFilesLocation) = UnzipUtility.unzip(file, directory)

  def resolveDataFile(date: LocalDate): Option[File] = {
	val today = LocalDate.now()

	val isCurrentMonth = today.getMonthValue == date.getMonthValue
	val isCurrentYear = today.getYear == date.getYear

	def dayPrnFileName = date.format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".prn"
	def monthZipFileName = date.format(DateTimeFormatter.ofPattern("MM-yyyy")) + ".zip"
	def yearZipFileName = date.format(DateTimeFormatter.ofPattern("yyyy")) + ".zip"

	def resolveRemoteFileName: String = {
	  if (isCurrentMonth && isCurrentYear) dayPrnFileName
	  else if (!isCurrentMonth && isCurrentYear) monthZipFileName
	  else yearZipFileName
	}

	def fileFromName(fileName: String) = new File(dataFilesLocation + File.separator + fileName)
	def urlFromRemoteFileName = new URL(appConfig.feedProviderUrl + "/" + resolveRemoteFileName)

	val localDayPrnFile = fileFromName(dayPrnFileName)
	if (localDayPrnFile.exists) {
	  Some(localDayPrnFile)
	} else {
	  if (isCurrentMonth && isCurrentYear) {
		try {
		  fileDownloader.download(urlFromRemoteFileName)
		  Some(localDayPrnFile)
		} catch {
		  case e: HttpResponseException =>
			if (date == LocalDate.now()) {
			  logger.info("File for today not available yet")
			  None
			} else throw e
		}
	  } else if (!isCurrentMonth && isCurrentYear) {
		val monthZipFile = fileDownloader.download(urlFromRemoteFileName)
		unzipFile(monthZipFile)
		Some(localDayPrnFile)
	  } else {
		val localMonthZipFile = fileFromName(monthZipFileName)
		if (localMonthZipFile.exists) {
		  unzipFile(localMonthZipFile)
		  Some(localDayPrnFile)
		} else {
		  val localYearZipFile = {
			val localYearZipFile = fileFromName(yearZipFileName)
			if (localYearZipFile.exists) localYearZipFile
			else {
			  fileDownloader.download(urlFromRemoteFileName)
			}
		  }
		  unzipFile(localYearZipFile)
		  unzipFile(localMonthZipFile)
		  Some(localDayPrnFile)
		}
	  }
	}
  }
}
