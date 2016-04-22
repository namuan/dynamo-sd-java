package com.github.namuan.common.servicediscovery.data;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;


import java.util.Objects;
import java.util.UUID;

@DynamoDBTable(tableName = ServiceDiscoveryTable.SERVICE_DISCOVERY_TABLE_NAME)
public class ServiceData {
    @DynamoDBHashKey(attributeName = "id")
    private String id;
    @DynamoDBAttribute(attributeName = "name")
    private String name;
    @DynamoDBAttribute(attributeName = "environment")
    private String environment;
    @DynamoDBAttribute(attributeName = "version")
    private String version;
    @DynamoDBAttribute(attributeName = "value")
    private String value;

    public ServiceData() {}

    public ServiceData(String name, String environment, String version) {
        setId(UUID.randomUUID().toString());
        setName(name);
        setEnvironment(environment);
        setVersion(version);
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEnvironment() {
        return this.environment;
    }

    public String getVersion() {
        return this.version;
    }

    public String getValue() {
        return this.value;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceData that = (ServiceData) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(environment, that.environment) &&
                Objects.equals(version, that.version) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, environment, version, value);
    }

    @Override
    public String toString() {
        return "ServiceData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", environment='" + environment + '\'' +
                ", version='" + version + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
