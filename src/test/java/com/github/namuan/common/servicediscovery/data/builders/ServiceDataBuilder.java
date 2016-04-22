package com.github.namuan.common.servicediscovery.data.builders;


import com.github.namuan.common.servicediscovery.data.ServiceData;

public class ServiceDataBuilder {
    private String id = RandomBuilder.randomString();
    private String name = RandomBuilder.randomString();
    private String environment = RandomBuilder.randomString();
    private String version = RandomBuilder.randomString();
    private String value = RandomBuilder.randomString();

    private ServiceDataBuilder() {
    }

    public static ServiceDataBuilder aServiceData() {
        return new ServiceDataBuilder();
    }

    public ServiceDataBuilder withId(String id) {
        this.id = id;
        return this;
    }

    public ServiceDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ServiceDataBuilder withEnvironment(String environment) {
        this.environment = environment;
        return this;
    }

    public ServiceDataBuilder withVersion(String version) {
        this.version = version;
        return this;
    }

    public ServiceDataBuilder withValue(String value) {
        this.value = value;
        return this;
    }

    public ServiceDataBuilder but() {
        return aServiceData().withId(id).withName(name).withEnvironment(environment).withVersion(version).withValue(value);
    }

    public ServiceData build() {
        ServiceData serviceData = new ServiceData();
        serviceData.setId(id);
        serviceData.setName(name);
        serviceData.setEnvironment(environment);
        serviceData.setVersion(version);
        serviceData.setValue(value);
        return serviceData;
    }
}
