# Purpose
Create cloudformation stack for codedeploy with autoscaling and cloudwatch logs, cloudwatch alarms
This is for users that they don't have CI/CD environment but they want to use AutoScaling.

# Required 
1 VPC, 2 Public Subnet (for multiAz), 2 Private Subnet(for multiAz), 1 KeyPair

# Basics
If you're not familiar with AWS, or you haven't done this part, each AWS authored guide has a section on getting started, you might already be an expert however it might also be worth a quick read just to make sure the reader of this guide is in the same place, please have a look at:

http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/GettingStarted.html

# What is CodeDeploy?
AWS CodeDeploy is a deployment service that automates application deployments to Amazon EC2 instances or on-premises instances in your own facility.

http://docs.aws.amazon.com/codedeploy/latest/userguide/welcome.html

# What is AutoScaling?
Auto Scaling helps you ensure that you have the correct number of Amazon EC2 instances available to handle the load for your application. You create collections of EC2 instances, called Auto Scaling groups. You can specify the minimum number of instances in each Auto Scaling group, and Auto Scaling ensures that your group never goes below this size. You can specify the maximum number of instances in each Auto Scaling group, and Auto Scaling ensures that your group never goes above this size. If you specify the desired capacity, either when you create the group or at any time thereafter, Auto Scaling ensures that your group has this many instances. If you specify scaling policies, then Auto Scaling can launch or terminate instances as demand on your application increases or decreases.

http://docs.aws.amazon.com/autoscaling/latest/userguide/WhatIsAutoScaling.html

# Parameters for CloudFormation
vpc_id, subnet_ids, bucket_name, keypair, operator email

# Parameters for CodeDeploy
It is defined automatically from CloudFormation Stack Name

# How to create aws resources with cloudformation
<pre>
<code>
  ./create-codedeploy-with-vpc.sh REGION STACK_NAME(=APPLICATION_NAME) PROFILE 
</code>
</pre>

# How to deploy application
<pre>
<code>
  cd tomcat-app
  ./buildDeploy.sh REGION APPLICATION_NAME(=StackName-app) APPLICATION_BUCKET_NAME
</code>
</pre>
