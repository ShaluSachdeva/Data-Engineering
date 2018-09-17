package com.gd.twitteranalytics

import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StreamingTweetsJob {

  val log = Logger.getLogger(StreamingTweetsJob.getClass.getName)

  def main(args: Array[String]): Unit = {

    if (ConfigValidator.isConfValid(args)) {
      val authorizationKeys = args.take(4)
      TweetsIngestion.configureTwitter(authorizationKeys)

      //Incase Only tweets related to a specific Hashtags are needed..
      val filters = args.takeRight(args.length - 4)

      val ssc = setSparkSessionConf
      val englishTweets = TweetsIngestion.getTweets(ssc,filters)
      val tweetsInfo = TransformTweets.getDateAndText(englishTweets)
      tweetsInfo.foreachRDD(_.foreach(println))
      TransformTweets.processTweetsInfo(tweetsInfo)

      ssc.start
      ssc.awaitTermination()
    }
    else
      System.exit(1)
  }

  def setSparkSessionConf() = {

    //TODO: Add Spark Configuration via command line
    val sparkConf = new SparkConf().
                        setAppName("twitterStreaming").setMaster("local[*]")
    new StreamingContext(sparkConf, Seconds(2))
  }
}