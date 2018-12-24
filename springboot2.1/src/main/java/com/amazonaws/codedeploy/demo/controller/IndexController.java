package com.amazonaws.codedeploy.demo.controller;

import com.amazonaws.services.autoscaling.AmazonAutoScaling;
import com.amazonaws.services.autoscaling.AmazonAutoScalingClientBuilder;
import com.amazonaws.services.autoscaling.model.AutoScalingGroup;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsRequest;
import com.amazonaws.services.autoscaling.model.DescribeAutoScalingGroupsResult;
import com.amazonaws.services.codedeploy.AmazonCodeDeploy;
import com.amazonaws.services.codedeploy.AmazonCodeDeployClientBuilder;
import com.amazonaws.services.codedeploy.model.EC2TagFilter;
import com.amazonaws.services.codedeploy.model.GetDeploymentGroupRequest;
import com.amazonaws.services.codedeploy.model.GetDeploymentGroupResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;
import java.util.logging.Logger;

@Controller
public class IndexController {
    private final static Logger LOGGER = Logger.getLogger(IndexController.class.getName());

    @Value("${APPLICATION_NAME}")
    private String applicationName;

    @Value("${DEPLOYMENT_GROUP_NAME}")
    private String deploymentGroupName;

    private AmazonCodeDeploy codeDeploy = AmazonCodeDeployClientBuilder.defaultClient();

    private AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

    private AmazonAutoScaling autoScaling = AmazonAutoScalingClientBuilder.defaultClient();

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String displayIndex(Model model) {

        LOGGER.info("Application name set to: " + applicationName);
        model.addAttribute("applicationName", applicationName);
        LOGGER.info("Deployment Group Name set to: " + deploymentGroupName);
        model.addAttribute("deploymentGroupName", deploymentGroupName);
        if (deploymentGroupName.contains("Production")) {
    		model.addAttribute("instanceIds", Collections.<String>emptyList());
    		return "/index";
    	}
        
        final GetDeploymentGroupResult getResult = codeDeploy.getDeploymentGroup(new GetDeploymentGroupRequest()
                .withApplicationName(applicationName)
                .withDeploymentGroupName(deploymentGroupName));

        final List<String> instanceIds = new ArrayList<>();
        for (final EC2TagFilter filter : getResult.getDeploymentGroupInfo().getEc2TagFilters()) {
            Filter ec2DescribeFilter = getFilter(filter);
            LOGGER.info("Calling EC2 Describe Instances with " + ec2DescribeFilter.getName() + " and values " + ec2DescribeFilter.getValues());
            final DescribeInstancesResult describeResult = ec2.describeInstances(new DescribeInstancesRequest()
                    .withFilters(Collections.singletonList(ec2DescribeFilter)));
            for (final Reservation reservation : describeResult.getReservations()) {
                for (final Instance instance : reservation.getInstances()) {
                    LOGGER.info("Found instance " + instance.getInstanceId());
                    instanceIds.add(instance.getInstanceId());
                }
            }
        }
        for (final com.amazonaws.services.codedeploy.model.AutoScalingGroup codeDeployGroup : getResult.getDeploymentGroupInfo().getAutoScalingGroups()) {
            LOGGER.info("Calling AutoScaling Describe Auto Scaling Groups with Auto Scaling Group Name " + codeDeployGroup.getName());
            final DescribeAutoScalingGroupsResult describeResult = autoScaling.describeAutoScalingGroups(new DescribeAutoScalingGroupsRequest()
                    .withAutoScalingGroupNames(codeDeployGroup.getName()));
            for (final AutoScalingGroup autoScalingGroup : describeResult.getAutoScalingGroups()) {
                for (final com.amazonaws.services.autoscaling.model.Instance instance : autoScalingGroup.getInstances()) {
                    LOGGER.info("Found instance " + instance.getInstanceId());
                    instanceIds.add(instance.getInstanceId());
                }
            }
        }
        final Map<String, String> instanceStates = new HashMap<>();
        if (!instanceIds.isEmpty()) {
            final DescribeInstanceStatusResult result = ec2.describeInstanceStatus(new DescribeInstanceStatusRequest()
    			    .withInstanceIds(instanceIds));
            for (final InstanceStatus status : result.getInstanceStatuses()) {
                LOGGER.info("Found instance " + status.getInstanceId() + " in state " + status.getInstanceStatus().getStatus());
            	instanceStates.put(status.getInstanceId(), status.getInstanceStatus().getStatus());
            }
        }
        model.addAttribute("instanceIds", instanceIds);
        model.addAttribute("instanceStates", instanceStates);

        return "/index";
    }

    private Filter getFilter(final EC2TagFilter tagFilter) {
        Filter filter;
        switch (tagFilter.getType()) {
        case "KEY_ONLY":
            filter = new Filter("tag-key", Collections.singletonList(tagFilter.getKey()));
            break;
        case "VALUE_ONLY":
            filter = new Filter("tag-value", Collections.singletonList(tagFilter.getValue()));
            break;
        case "KEY_AND_VALUE":
            filter = new Filter("tag:" + tagFilter.getKey(), Collections.singletonList(tagFilter.getValue()));
            break;
        default:
            throw new IllegalArgumentException("Unknown filter type " + tagFilter.getType());
        }
        return filter;
    }
}
