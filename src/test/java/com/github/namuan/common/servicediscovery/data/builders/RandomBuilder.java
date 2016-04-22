package com.github.namuan.common.servicediscovery.data.builders;

import java.util.UUID;

public class RandomBuilder {
    public static String randomString() {
        return UUID.randomUUID().toString();
    }
}
