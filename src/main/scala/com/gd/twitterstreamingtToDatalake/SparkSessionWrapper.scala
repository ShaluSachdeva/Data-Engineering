package com.gd.twitterstreamingtToDatalake

import org.apache.spark.sql.SparkSession

trait SparkSessionWrapper {

  lazy val spark: SparkSession = {
    SparkSession
      .builder()
      .master("local")
      .appName("spark pika")
      .getOrCreate()
  }

  val test = "spark session test"
}
