#!/bin/bash
aws cloudformation create-stack --stack-name $1 --region $2 --template-body file://CodeDeployAutoscalingTomcatInVPC.yaml --parameters file://parameters.json --capabilities CAPABILITY_IAM --disable-rollback
