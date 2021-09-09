package cloud.filibuster.instrumentation.libraries.armeria.mocks.http.tests;

import cloud.filibuster.instrumentation.Helper;
import cloud.filibuster.instrumentation.libraries.armeria.http.FilibusterDecoratingHttpService;
import cloud.filibuster.instrumentation.libraries.armeria.http.FilibusterDecoratingHttpClient;

import com.linecorp.armeria.client.ClientBuilder;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.RequestHeaders;
import com.linecorp.armeria.common.ResponseHeaders;

import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloServerWithHelloServerTest extends HelloServerTest {
    @BeforeEach
    public void startServices() throws IOException, InterruptedException {
        super.startHelloServer();
        super.startExternalServer();
    }

    @AfterEach
    public void stopServices() throws InterruptedException {
        super.stopHelloServer();
        super.stopExternalServer();
    }

    @Test
    @DisabledIfEnvironmentVariable(named = "CI", matches = "true")
    @DisplayName("Test hello server test route.")
    public void testTest() throws IOException, InterruptedException {
        // Get remote resource.
        String uri = "http://" + Helper.getHost("hello") + ":" + Helper.getPort("hello") + "/";
        ClientBuilder cb = Clients.builder(uri);
        WebClient webClient = cb.build(WebClient.class);
        RequestHeaders getHeaders = RequestHeaders.of(HttpMethod.GET, "/test", HttpHeaderNames.ACCEPT, "application/json");
        AggregatedHttpResponse response = webClient.execute(getHeaders).aggregate().join();

        // Get headers and verify a 200 response.
        ResponseHeaders headers = response.headers();
        String statusCode = headers.get(HttpHeaderNames.STATUS);
        assertEquals("200", statusCode);
    }
}
