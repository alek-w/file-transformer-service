package com.knoldus.kafka.persistance

import scala.slick.driver.H2Driver.simple._
import scala.slick.lifted.ProvenShape

class Users(tag: Tag)
  extends Table[(Int, String, String)](tag, "USERS") {

  def id: Column[Int] = column[Int]("USER_ID", O.PrimaryKey)
  def name: Column[String] = column[String]("NAME")
  def startTime: Column[String] = column[String]("START_TIME")

  def * : ProvenShape[(Int, String, String)] =
    (id, name, startTime)
}

