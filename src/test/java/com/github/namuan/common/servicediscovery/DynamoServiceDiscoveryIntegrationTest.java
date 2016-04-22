package com.github.namuan.common.servicediscovery;

import com.amazonaws.regions.Regions;
import com.github.namuan.common.servicediscovery.data.ServiceData;
import com.github.namuan.common.servicediscovery.data.builders.RandomBuilder;
import com.github.namuan.common.servicediscovery.data.builders.ServiceDataBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DynamoServiceDiscoveryIntegrationTest {

    private DynamoServiceDiscovery serviceDiscovery = new DynamoServiceDiscovery()
            .withLocalDynamo("http://localhost:8000")
            .withRegion(Regions.EU_WEST_1)
            .init();

    @Test
    public void createTableIfNotExists() {
        // when
        serviceDiscovery.init();

        // then
        Assert.assertTrue("Config table should exist", serviceDiscovery.tableExists());
    }

    @Test
    public void useDifferentRegion() {
        // given
        serviceDiscovery.withRegion(Regions.EU_CENTRAL_1);

        // when
        serviceDiscovery.init();

        // then
        Assert.assertTrue("Config table should exist", serviceDiscovery.tableExists());
    }

    @Test
    public void dontThrowErrorIfTableAlreadyExists() {
        // given
        serviceDiscovery.init();

        // when
        serviceDiscovery.init();

        // then
        Assert.assertTrue("Config table should exist", serviceDiscovery.tableExists());
    }

    @Test
    public void getServiceDataByName() {
        // given
        ServiceData testServiceData = addTestServiceData(ServiceDataBuilder.aServiceData().build());

        // when
        ServiceData serviceDataResult = serviceDiscovery.getServiceBy(
                testServiceData.getName(),
                testServiceData.getEnvironment(),
                testServiceData.getVersion()).get();

        // then
        Assert.assertNotNull(serviceDataResult);
        assertServiceDataObjects(testServiceData, serviceDataResult);
    }

    @Test
    public void canGetServiceDataForAllVersionsInAGivenEnvironment() {
        // given
        String serviceName = RandomBuilder.randomString();
        String environment = RandomBuilder.randomString();

        final ServiceData firstTestServiceData = addTestServiceData(ServiceDataBuilder.aServiceData()
                .withName(serviceName)
                .withEnvironment(environment)
                .withVersion("1.0").build());

        final ServiceData secondTestServiceData = addTestServiceData(ServiceDataBuilder.aServiceData()
                .withName(serviceName)
                .withEnvironment(environment)
                .withVersion("2.0").build());

        // when
        final List<ServiceData> serviceDataList = serviceDiscovery.getServiceBy(serviceName, environment);

        // then
        Assert.assertEquals(2, serviceDataList.size());

        // assert individual results
        ServiceData actualFirstTestServiceData = serviceDataList.stream()
                .filter(serviceData -> serviceData.getVersion().equals("1.0")).findFirst().get();
        assertServiceDataObjects(firstTestServiceData, actualFirstTestServiceData);

        ServiceData actualSecondTestServiceData = serviceDataList.stream()
                .filter(serviceData -> serviceData.getVersion().equals("2.0")).findFirst().get();
        assertServiceDataObjects(secondTestServiceData, actualSecondTestServiceData);
    }

    @Test public void checkServiceNameEnvironmentAndVersionAsUniqueKeysBeforeInsertingRecord() {
        // given
        String serviceName = RandomBuilder.randomString();
        String environment = RandomBuilder.randomString();
        String version = RandomBuilder.randomString();

        // and trying to add same item twice
        addTestServiceData(ServiceDataBuilder.aServiceData()
                .withName(serviceName)
                .withEnvironment(environment)
                .withVersion(version)
                .build());

        addTestServiceData(ServiceDataBuilder.aServiceData()
                .withName(serviceName)
                .withEnvironment(environment)
                .withVersion(version)
                .build());

        // when
        final List<ServiceData> serviceDataList = serviceDiscovery.getServiceBy(serviceName, environment);

        // then
        Assert.assertEquals(1, serviceDataList.size());
    }

    private ServiceData addTestServiceData(ServiceData serviceData) {
        serviceDiscovery.addService(serviceData);
        return serviceData;
    }

    private void assertServiceDataObjects(ServiceData expectedServiceData, ServiceData actualServiceData) {
        Assert.assertEquals("Service name is incorrect", expectedServiceData.getName(), actualServiceData.getName());
        Assert.assertEquals("Service environment is incorrect", expectedServiceData.getEnvironment(), actualServiceData.getEnvironment());
        Assert.assertEquals("Service version is incorrect", expectedServiceData.getVersion(), actualServiceData.getVersion());
        Assert.assertEquals("Service value is incorrect", expectedServiceData.getValue(), actualServiceData.getValue());
    }
}