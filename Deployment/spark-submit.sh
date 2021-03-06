#!/bin/bash

set +x
/usr/bin/clear

PS3="Please choose the job you want to run: "
select opt in TwitterStreamingJob ActiveUserDataFrameReport ActiveUserDatasetReport ActiveUserSqlReport

do
    case $opt in
        "TwitterStreamingJob")
            echo "====> Running TwitterStreaming Application...."
            break
            ;;
        "ActiveUserDataFrameReport")
            echo "=====> Running ActiveUserDataFrameReport Application..."
           break
           ;;
       "ActiveUserDatasetReport")
           echo "=====> Running ActiveUserDatasetReport Application..."
          break
          ;;
       "ActiveUserSqlReport")
           echo "=====> Running ActiveUserSqlReport Application..."
          break
          ;;
        *) echo "invalid option $REPLY";;
    esac
    done


    if [ $opt == "TwitterStreamingJob" ]
    then
    className=com.gd.twitteranalytics.StreamingTweetsJob
    fi
    if [ $opt == "ActiveUserDataFrameReport" ]
    then
    className=com.gd.twitteranalytics.ActiveUserDataFrameReport
    fi
    if [ $opt == "ActiveUserDatasetReport" ]
    then
    className=com.gd.twitteranalytics.ActiveUserDatasetReport
    fi
    if [ $opt == "ActiveUserSqlReport" ]
    then
    className=com.gd.twitteranalytics.ActiveUserSqlReport
    fi

read -p "====> Please enter the path to Spark/bin/spark-submit script :" SPARK_HOME

if [[ -z "$SPARK_HOME" ]]; then
  echo ">>> ERROR :Path to spark-submit script is missing !!!"
  echo
  exit 1
fi

read -p "====> Please enter the path to Application jar file :" ASSEMBLY_JAR

if [[ -z "$ASSEMBLY_JAR" ]]; then
  echo ">>> ERROR :Path to Application jar file is missing"
  echo
  exit 1
fi

echo "Command that will be executed is.."
echo $SPARK_HOME --class $className --driver-memory 512M  --master yarn --executor-memory 512M $ASSEMBLY_JAR
echo
echo ">>>>>Running twitter-application>>>>>>>>"
echo
$SPARK_HOME --class $className --driver-memory 512M  --master yarn --executor-memory 512M $ASSEMBLY_JAR