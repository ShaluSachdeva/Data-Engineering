package com.gd.twitteranalytics

import com.gd.twitteranalytics.TweetsTransformer._
import com.gd.twitteranalytics.TweetsDataLoader._
import org.apache.log4j.Logger
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.DStream
import org.apache.spark.streaming.{Seconds, StreamingContext}
import java.util.{Date => JavaDate}

import com.gd.twitteranalytics.util.AppConfigReader
import com.typesafe.config.ConfigException

object StreamingTweetsJob {

  val log = Logger.getLogger(StreamingTweetsJob.getClass.getName)

  def main(args: Array[String]): Unit = {
    try {
      val authKeys = ConfigValidator.validateTwitterAuth
      if (ConfigValidator.isConfValid(authKeys)) {
        TweetsDataReader.configureTwitter()

        //Incase Only tweets related to a specific Hashtags are needed..
        val filters = AppConfigReader.HashTags
        val dateColumn = "event_date"

        val ssc = setSparkStreamingContext
        val englishTweets = TweetsDataReader.getTweets(ssc, Array(filters))
        val tweetsInfo = getDateAndText(englishTweets)
        processTweetsInfo(tweetsInfo,AppConfigReader.SavePath,dateColumn)

        ssc.start
        ssc.awaitTermination()
      }
      else
        System.exit(1)
    }
    catch {
      case exception: Exception => {
        handleException(exception)
      }
    }
  }

  def setSparkStreamingContext() = {

    //TODO: Add Spark Configuration via command line
    val sparkConf = new SparkConf().
      setAppName(AppConfigReader.AppName).setMaster(AppConfigReader.MasterUrl)
    new StreamingContext(sparkConf, Seconds(2))
  }

  def processTweetsInfo(tweetsInfo: DStream[(JavaDate, String)],path:String,dateColumn:String) = {
    tweetsInfo.foreachRDD { rdd =>
      var tweetsDataFrame = convertRddToDataFrame(rdd)
      tweetsDataFrame = updateDateColFormat(tweetsDataFrame,dateColumn)
      saveOutputToHdfs(tweetsDataFrame,path,dateColumn)
    }
  }

  def handleException(exception: Exception) {
    exception match {
      case exception: ConfigException =>
        if (exception.getMessage == null)
          log.error(">>>Application configuration is not valid. Please provide the config parameters! ")
        else
          log.error(exception.getMessage)
      case e: Exception => log.error(e.printStackTrace)
    }
  }
}