package com.github.namuan.common;


import com.github.namuan.common.servicediscovery.DynamoServiceDiscovery;
import com.github.namuan.common.servicediscovery.data.ServiceData;

import java.util.List;
import java.util.Optional;

/**
 * For testing
 */
public class Main {
    public static void main(String[] args) {

        DynamoServiceDiscovery dynamoServiceDiscovery = new DynamoServiceDiscovery()
                .withLocalDynamo("http://localhost:8000")
                .init();

        final String serviceName = "some-service-name";
        final String devEnvironment = "dev";
        final String v1_0 = "1.0";
        final String v2_0 = "2.0";
        final String serviceHost = "http://some-other-service-host";
        final String v2_serviceHost = "http://v2-service-host";

        ServiceData serviceData = new ServiceData(serviceName, devEnvironment, v1_0);
        serviceData.setValue("http://service-host");

        dynamoServiceDiscovery.addService(serviceData);

        System.out.println("Get Service by name and environment and version");
        final Optional<ServiceData> maybeServiceData = dynamoServiceDiscovery.getServiceBy(serviceName, devEnvironment, v1_0);
        maybeServiceData.ifPresent(v1ServiceData -> System.out.println(">" + v1ServiceData.getValue()));

        System.out.println("Change version number");
        serviceData.setVersion(v2_0);
        serviceData.setValue(serviceHost);
        dynamoServiceDiscovery.addService(serviceData);

        ServiceData v2ServiceData = dynamoServiceDiscovery.getServiceBy(serviceName, devEnvironment, v2_0).get();
        System.out.println(v2ServiceData.getValue());

        System.out.println("Change value for existing version");
        serviceData.setValue(v2_serviceHost);
        dynamoServiceDiscovery.addService(serviceData);

        ServiceData v2_1ServiceData = dynamoServiceDiscovery.getServiceBy(serviceName, devEnvironment, v2_0).get();
        System.out.println(v2_1ServiceData.getValue());

        System.out.println("Get all service definitions");
        final List<ServiceData> serviceDataList = dynamoServiceDiscovery.getServiceBy(serviceName, devEnvironment);
        for (ServiceData serviceDef : serviceDataList) {
            System.out.println("." + serviceDef);
        }
    }

}
