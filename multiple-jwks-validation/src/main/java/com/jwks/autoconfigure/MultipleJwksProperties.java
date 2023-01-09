package com.jwks.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

@Validated
@ConfigurationProperties(prefix = "com.example")
public class MultipleJwksProperties {
    @NotEmpty
    private Map<String, String> multipleJwks = new HashMap<>();

    public Map<String, String> getMultipleJwks() {
        return multipleJwks;
    }

    public void setMultipleJwks(Map<String, String> multipleJwks) {
        this.multipleJwks = multipleJwks;
    }
}
