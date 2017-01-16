package com.knoldus.kafka.demo

import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}

import com.knoldus.kafka.consumer.KafkaConsumer
import com.knoldus.kafka.persistance.Users
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.slick.driver.H2Driver.simple._
import scala.sys.process._
import scala.util.control.Breaks.{break, breakable}


object ConsumerApp extends App with LazyLogging {

  private val config = ConfigFactory.load()
  private val topic = config.getString("kafka.topic")

  private val groupId = config.getString("kafka.groupId")
  private val zookeeperConnect = config.getString("kafka.zookeeperConnect")

  val consumer = new KafkaConsumer(topic, groupId, zookeeperConnect)

  val users: TableQuery[Users] = TableQuery[Users]

  private val jdbcConnect = config.getString("db.jdbcConnect")
  private val dbDriver = config.getString("db.driver")

  val db = Database.forURL(jdbcConnect, driver = dbDriver)

  val set = scala.collection.mutable.Set[Int]()

  val currentOffset = TimeZone.getDefault.getOffset(System.currentTimeMillis)
  private val inputTimeFormat = config.getString("inputTimeFormat")
  val fmtFromLocal = new SimpleDateFormat(inputTimeFormat)
  val fmtToGmt = new SimpleDateFormat()

  val prefixUrl = config.getString("fileUploaderService.endpoint")
  val storageFolder = config.getString("storageFolder")


  db.withSession { implicit session =>
    users.ddl.create

    while (true) {
      consumer.read() match {
        case Some(message) =>
          logger.info("Getting message: " + message)
          val filename = downloadFile(message)
          processCsv(filename)
          select()
        case None =>
          Thread.sleep(2 * 1000)
      }
    }

    def saveData(id: Int, name: String, utc: String) = {
      if (!set.contains(id)) {
        set += id
        users += (id, name, utc)
      }
    }

    def processCsv(file: File) = {
      val bufferedSource = io.Source.fromFile(file)
      for (line <- bufferedSource.getLines) {
        breakable {
          val cols = line.split(",").map(_.trim)
          if (cols.length < 3) {
            break
          } else {
            toInt(cols(0)) match {
              case Some(i) => saveData(i, cols(1).toLowerCase, toUtc(cols(2)))
              case None => //logger.debug("That doesn't work." + s"${cols(0)}|${cols(1)}|${cols(2)}|${cols(3)}")
            }
          }
        }
      }
      bufferedSource.close
      println("the file has been processed.")
    }

    def select() = {
      val allSuppliers: List[(Int, String, String)] = users.list

      println(allSuppliers)

      users foreach { case (id, name, utc) =>
        println("  " + id + "\t" + name + "\t" + utc)
      }
    }
  }

  def toInt(s: String): Option[Int] = {
    try {
      Some(s.toInt)
    } catch {
      case e: Exception => None
    }
  }


  def toUtc(t: String): String = {
    val time = fmtFromLocal.parse(t).getTime
    val timeUtc = time + currentOffset
    fmtToGmt.format(new Date(timeUtc))
  }


  private def downloadFile(fileToDownload: String): File = {
    logger.debug(" debug input filename for downloading:" + fileToDownload)
    val url = prefixUrl + fileToDownload

    val filename = storageFolder + fileToDownload
    val file = new File(filename)
    new URL(url) #> file !!

    logger.debug("file saved to :" + filename)

    file
    //    new File("/home/liudmila/Documents/data_test.csv")
  }


}
