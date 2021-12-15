name := """kafka-file-transformer"""

version := "1.0"

scalaVersion := "2.11.12"


libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka_2.11" % "0.10.0.0"
)

libraryDependencies ++= List(
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.h2database" % "h2" % "1.3.170",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "org.scalatest"        %%    "scalatest"    	      %      "2.2.5"     %    "test"
)