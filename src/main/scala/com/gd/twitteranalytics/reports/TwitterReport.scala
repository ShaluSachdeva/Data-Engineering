package com.gd.twitteranalytics.reports

import com.gd.twitteranalytics.util.AppConfigReader
import org.apache.log4j.Logger
import org.apache.spark.sql.{DataFrame, SparkSession}

trait TwitterReport {

  val today = java.time.LocalDate.now
  val dataPath = AppConfigReader.getAppConfigurables(0) + "/event_date=" + AppConfigReader.getReportConfigurables(1)
  val activeUserReportSavePath = AppConfigReader.getReportConfigurables(0) + "/ActiveUsers"

  def getInputDataForReport(dataPath:String,spark:SparkSession):DataFrame = {
    val tweetsDataFrame = ReportInputDataParser.getPayloadStatusAsDataFrame(spark, dataPath)
    ReportInputDataParser.getUserIdAndLocation(spark, tweetsDataFrame)
  }

  def getLogger(className:String):Logger = {
    Logger.getLogger(className.getClass.getName)
  }

  def setSparkSession(name:String):SparkSession = {
    SparkSession.builder.appName(name).getOrCreate
  }
}
