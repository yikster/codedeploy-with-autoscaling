#!/bin/bash
if [ $# -ne 2 ];
  then echo "usage $0 stack-name-prefix profile-name"; exit 0;
fi


KEY_PREFIX=$1
PROFILE=$2

declare -a arr=("us-east-2" "us-east-1" "us-west-1" "us-west-2" "ap-south-1" "ap-northeast-3" "ap-northeast-2" "ap-southeast-1" "ap-southeast-2" "ap-northeast-1" "ca-central-1" "eu-central-1" "eu-west-1" "eu-west-2" "eu-west-3" "eu-north-1" "sa-east-1")

## now loop through the above array
for i in "${arr[@]}"
do
   echo "$i"
   #aws ec2 create-key-pair --key-name ${KEY_PREFIX}-${i} > key-${i}-make-pem.json
   cat key-${i}-make-pem.json

   # or do whatever with individual element of the array
done

for ((i = 0; i < ${#arr[@]}; ++i)); do
    # bash arrays are 0-indexed
    echo "$i,${arr[$i]}"
    REGION=${arr[$i]}
    STACK_NAME="SN-${REGION}"
    ENV_NAME="DEV-${REGION}" 

    aws --profile $PROFILE cloudformation create-stack --stack-name $STACK_NAME --region $REGION --template-body file://CreateVPCWithPublicPrivateSubnets.yaml --parameters ParameterKey=EnvironmentName,ParameterValue=${ENV_NAME},ParameterValue=${KEY_PREFIX}-${REGION} \
    ParameterKey=VpcCIDR,ParameterValue=10.${i}.0.0\/16    \
    ParameterKey=PrivateSubnet1CIDR,ParameterValue=10.${i}.1.0\/24    \
    ParameterKey=PrivateSubnet2CIDR,ParameterValue=10.${i}.2.0\/24	\
    ParameterKey=PublicSubnet1CIDR,ParameterValue=10.${i}.3.0\/24	\
    ParameterKey=PublicSubnet2CIDR,ParameterValue=10.${i}.4.0\/24	\
    --capabilities CAPABILITY_IAM --disable-rollback

done
