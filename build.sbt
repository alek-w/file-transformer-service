name := """kafka-file-transformer"""

version := "1.0"

scalaVersion := "2.11.12"


libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" % "2.4.1"
)

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.h2database" % "h2" % "1.4.200",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4",
  "org.scalatest"        %%    "scalatest"    	      %      "2.2.6"     %    "test"
)