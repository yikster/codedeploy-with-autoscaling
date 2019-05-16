#!/bin/bash
aws cloudformation create-stack --stack-name $1 --region $2 \
    --profile $3 --template-body file://CodeDeployAutoscalingTomcatInVPC_2.yaml --parameters ParameterKey=OperatorEmail,ParameterValue=$4 ParameterKey=VpcId,ParameterValue=$5 ParameterKey=PublicSubnet1,ParameterValue=$6 ParameterKey=PublicSubnet2,ParameterValue=$7 ParameterKey=PrivateSubnet1,ParameterValue=$8 ParameterKey=PrivateSubnet2,ParameterValue=$9 ParameterKey=KeyPairName,ParameterValue=${10} \
    --capabilities CAPABILITY_IAM --disable-rollback	\
