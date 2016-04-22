### Java Library for Service Discovery using Dynamo

A simple Java library to lookup service information stored in DynamoDB.

#### Installation

```
<dependency>
// @todo
</dependency>
```

#### Usage

It uses 'service-config' table in DynamoDB to store and lookup service data. The table consists of the following fields

Field Name | Description 
--- | ---
Name | Service Name
Environment | Each service can be available in different environment
Version | Each service can have multiple versions running in the same environment

The library uses the DefaultAWSCredentialsProviderChain and uses EU_WEST_2 as the default region.

The following statement will try to connect to [Local DynamoDB](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Tools.DynamoDBLocal.html#Tools.DynamoDBLocal.DownloadingAndRunning) and create the config table if it doesn't exist

```java
DynamoServiceDiscovery dynamoServiceDiscovery = new DynamoServiceDiscovery()
                .withLocalDynamo("http://localhost:8000").init();
```

To connect to Amazon DynamoDB and create the config table if it doesn't exist

```java
DynamoServiceDiscovery dynamoServiceDiscovery = new DynamoServiceDiscovery().init();
```

To connect to DynamoDB (Local/Cloud) without creating the config table

```java
DynamoServiceDiscovery dynamoServiceDiscovery = new DynamoServiceDiscovery();
```

To connect to DynamoDB in a different region

```java
DynamoServiceDiscovery serviceDiscovery = new DynamoServiceDiscovery().withRegion(Regions.EU_WEST_1);
```

Once we are connected, we can add new config data.

```java
ServiceData serviceData = new ServiceData("serviceName", "devEnvironment", "1.0");
serviceData.setValue("http://service-host");

dynamoServiceDiscovery.addService(serviceData);
```

And to retrieve config data.

```java
final Optional<ServiceData> maybeServiceData = dynamoServiceDiscovery.getServiceBy("serviceName", "devEnvironment", "1.0");
maybeServiceData.ifPresent(v1ServiceData -> System.out.println(">" + v1ServiceData.getValue())); // >http://service-host
```

To get all the versions for a service in an environment

```java
final List<ServiceData> serviceDataList = dynamoServiceDiscovery.getServiceBy(serviceName, devEnvironment);
for (ServiceData service : serviceDataList) {
	System.out.println(service);
}
```

#### Development

For local development, please make sure that you are running [Local DynamoDB](http://docs.aws.amazon.com/amazondynamodb/latest/developerguide/Tools.DynamoDBLocal.html#Tools.DynamoDBLocal.DownloadingAndRunning)

```shell
git clone https://github.com/namuan/dynamo-sd-java.git
cd dynamo-sd-java
gradle test
```
