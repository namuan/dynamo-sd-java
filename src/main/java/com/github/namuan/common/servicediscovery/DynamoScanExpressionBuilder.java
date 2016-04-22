package com.github.namuan.common.servicediscovery;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import java.util.HashMap;

class DynamoScanExpressionBuilder {
    DynamoDBScanExpression buildExpressionToGetServiceBy(HashMap<String, String> params) {
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
        params.forEach((key, value) -> scanExpression.withFilterConditionEntry(key, conditionFor(value)));
        return scanExpression;
    }

    private Condition conditionFor(String param) {
        return new Condition()
                .withComparisonOperator(ComparisonOperator.EQ)
                .withAttributeValueList(new AttributeValue(param));
    }
}
