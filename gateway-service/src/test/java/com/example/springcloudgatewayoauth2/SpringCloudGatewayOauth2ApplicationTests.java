package com.example.springcloudgatewayoauth2;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(properties = {"ORDER_SERVICE_URL=http://localhost:${wiremock.server.port}", "logging.level.org.springframework.security=trace"})
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
class SpringCloudGatewayOauth2ApplicationTests {
    @Autowired
    WebTestClient client;

    @Autowired
    ApplicationContext context;

    @BeforeEach
    void setup() {
        stubFor(WireMock.get(urlPathEqualTo("/v1/orders"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("[{\"orderId\":\"1\",\"price\":100.01},{\"orderId\":\"2\",\"price\":50.01}]")));
    }

    @Test
    @WithMockUser(username = "user", roles = "ADMIN")
    void testFormLoginCallOrderService() {
        client.get().uri("/v1/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody().jsonPath("$..orderId").isArray();

        verify(getRequestedFor(urlPathEqualTo("/v1/orders")));
    }
}
