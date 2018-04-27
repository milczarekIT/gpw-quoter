package it.milczarek.gpwquoter.file

import java.io.{BufferedOutputStream, File, FileInputStream, FileOutputStream}
import java.util.zip.ZipInputStream

object UnzipUtility {

  val bufferSize = 4096

  def unzip(zipFilePath: File, destDirectory: String) {
    def extractFile(zipIn: ZipInputStream, filePath: String) {
      val bos = new BufferedOutputStream(new FileOutputStream(filePath))
      val bytesIn = new Array[Byte](bufferSize)
      var read = zipIn.read(bytesIn)
      while (read != -1) {
        bos.write(bytesIn, 0, read)
        read = zipIn.read(bytesIn)
      }
      bos.close()
    }

    val destDir = new File(destDirectory)
    if (!destDir.exists()) destDir.mkdir()

    val zipIn = new ZipInputStream(new FileInputStream(zipFilePath))

    var entry = zipIn.getNextEntry
    while (entry != null) {
      val filePath = destDirectory + File.separator + entry.getName
      if (!entry.isDirectory) {
        extractFile(zipIn, filePath)
      } else {
        val dir = new File(filePath)
        dir.mkdir()
      }
      zipIn.closeEntry()
      entry = zipIn.getNextEntry
    }
    zipIn.close()
  }
}
