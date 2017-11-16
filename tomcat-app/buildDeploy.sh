#!/bin/bash
mvn package
zip -r sample_v2.zip appspec.yml scripts target/SampleMavenTomcatApp.war
aws s3 cp sample_v2.zip s3://$3/codedeploy/sample_v2.zip
aws deploy create-deployment --region $1 --application-name $2-app --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name $2-dg --description "new-version" --s3-location bucket=$3,bundleType=zip,key=codedeploy/sample_v2.zip
