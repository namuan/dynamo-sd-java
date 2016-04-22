package com.github.namuan.common.servicediscovery;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceNotFoundException;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.github.namuan.common.servicediscovery.data.ServiceData;
import com.github.namuan.common.servicediscovery.data.ServiceDiscoveryTable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DynamoServiceDiscovery {

    private final DynamoScanExpressionBuilder dynamoScanExpressionBuilder = new DynamoScanExpressionBuilder();
    private final AmazonDynamoDBClient dynamoDBClient;
    private final DynamoDBMapper mapper;

    /**
     * Constructor
     * Creates a new connection to DynamoDB using the default region (Regions.DEFAULT_REGION)
     */
    public DynamoServiceDiscovery() {
        dynamoDBClient = new AmazonDynamoDBClient(new DefaultAWSCredentialsProviderChain()).withRegion(Regions.DEFAULT_REGION);
        mapper = new DynamoDBMapper(dynamoDBClient);
    }

    /**
     * Changes the DynamoDB client to point to DynamoDB local
     * @param endpoint The endpoint where local DynamoDB is running
     * @return Reference to the object instance
     */
    public DynamoServiceDiscovery withLocalDynamo(String endpoint) {
        dynamoDBClient.setEndpoint(endpoint);
        return this;
    }

    /**
     * Changes the DynamoDB client to a different region
     * @param region
     * @return Reference to the object instance
     */
    public DynamoServiceDiscovery withRegion(Regions region) {
        dynamoDBClient.setRegion(Region.getRegion(region));
        return this;
    }

    /**
     * Initialises the service discovery client and creates the DynamoDB table if it doesn't exist
     * @return Reference to the object instance
     */
    public DynamoServiceDiscovery init() {
        if (tableExists()) return this;
        createTable();
        return this;
    }

    /**
     * Creates a new entry in DynamoDB with the provided @ServiceData values. If an item already exists with
     * the same name, environment and version then thi method will only update the value in DynamoDB
     * @param newServiceData Service details including name, environment and version
     */
    public void addService(ServiceData newServiceData) {
        ServiceData serviceData = getServiceBy(
                newServiceData.getName(),
                newServiceData.getEnvironment(),
                newServiceData.getVersion()
        ).orElse(
                new ServiceData(newServiceData.getName(), newServiceData.getEnvironment(), newServiceData.getVersion())
        );

        serviceData.setValue(newServiceData.getValue());
        mapper.save(serviceData);
    }


    /**
     * Get service data items by name and environment
     * @param name Service name
     * @param environment Service environment
     * @return List of items if there are multiple versions in the same environment. Otherwise a single item in List.
     */
    public List<ServiceData> getServiceBy(String name, String environment) {
        DynamoDBScanExpression scanExpression = dynamoScanExpressionBuilder.buildExpressionToGetServiceBy(new HashMap<String, String>() {{
            put("name", name);
            put("environment", environment);
        }});

        final PaginatedScanList<ServiceData> scanResults = mapper.scan(ServiceData.class, scanExpression);
        return scanResults.stream().collect(Collectors.toList());
    }

    /**
     * Find service data by name, environment and version
     * @param name Service name
     * @param environment Service environment
     * @param version Service version in the given environment
     * @return An Java Optional value which is empty if unable to find data otherwise it contains the ServiceData instance.
     */
    public Optional<ServiceData> getServiceBy(String name, String environment, String version) {
        DynamoDBScanExpression scanExpression = dynamoScanExpressionBuilder.buildExpressionToGetServiceBy(new HashMap<String, String>() {{
            put("name", name);
            put("environment", environment);
            put("version", version);
        }});

        final PaginatedScanList<ServiceData> scanResults = mapper.scan(ServiceData.class, scanExpression);
        if (scanResults.size() > 1) throw new IllegalStateException(String.format("Multiple rows for the same version: %s", version));
        if (scanResults.isEmpty()) return Optional.empty();
        return Optional.of(scanResults.get(0));
    }

    /**
     * Checks if the service config table exists in DynamoDB
     * @return true or false
     */
    boolean tableExists() {
        try {
            dynamoDBClient.describeTable(ServiceDiscoveryTable.SERVICE_DISCOVERY_TABLE_NAME);
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    private void createTable() {
        CreateTableRequest createTableRequest = new CreateTableRequest(
                Collections.singletonList(new AttributeDefinition("id", ScalarAttributeType.S)),
                ServiceDiscoveryTable.SERVICE_DISCOVERY_TABLE_NAME,
                Collections.singletonList(new KeySchemaElement("id", KeyType.HASH)),
                new ProvisionedThroughput(1L, 1L)
        );

        dynamoDBClient.createTable(createTableRequest);
    }

}
