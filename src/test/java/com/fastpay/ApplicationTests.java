package com.fastpay;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled("Disabled because it requires active infrastructure (PostgreSQL/Kafka). Will be reactivated in the Integration Tests phase.")
@SpringBootTest
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
