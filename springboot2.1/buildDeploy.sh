#!/bin/bash
APP_ZIP=springboot_2.1.zip
PROFILE=$1
APP_NAME=$2
BUCKET_NAME=$3
REGION=$4
./gradlew clean build

if [ $? -ne 0 ]; then
  echo "Build is failed. Check codes"
  exit -1
fi

rm -rf $APP_ZIP
zip -r $APP_ZIP appspec.yml scripts/** build/libs/**
aws s3 cp --profile $PROFILE $APP_ZIP s3://${BUCKET_NAME}/codedeploy/${APP_ZIP}
aws deploy create-deployment --profile $PROFILE --region $REGION --application-name ${APP_NAME}-app --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name ${APP_NAME}-dg --description "new-version1" --s3-location bucket=${BUCKET_NAME},bundleType=zip,key=codedeploy/${APP_ZIP}
