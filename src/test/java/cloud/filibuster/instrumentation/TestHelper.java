package cloud.filibuster.instrumentation;

import cloud.filibuster.instrumentation.exceptions.ServerAvailabilityException;
import cloud.filibuster.instrumentation.libraries.armeria.mocks.ExternalServer;
import cloud.filibuster.instrumentation.libraries.armeria.mocks.HelloServer;
import cloud.filibuster.instrumentation.libraries.armeria.mocks.WorldServer;
import com.linecorp.armeria.client.ClientBuilder;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.WebClient;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.RequestHeaders;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.server.Server;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHelper {
    private static final Logger logger = Logger.getLogger(TestHelper.class.getName());

    private static Server helloServer;

    private static Server externalServer;

    private TestHelper() {

    }

    public static void startHelloServerAndWaitUntilAvailable() throws InterruptedException, IOException {
        helloServer = HelloServer.serve();
        CompletableFuture<Void> helloServerFuture = helloServer.start();

        // Wait up to 10 seconds for HelloServer to start.
        boolean online = false;

        for (int i = 0; i < 10; i++) {
            logger.log(Level.INFO, "Waiting for HelloServer to come online...");

            try {
                // Get remote resource.
                String uri = "http://" + Helper.getHost("hello") + ":" + Helper.getPort("hello") + "/";
                ClientBuilder cb = Clients.builder(uri);
                WebClient webClient = cb.build(WebClient.class);
                RequestHeaders getHeaders = RequestHeaders.of(
                        HttpMethod.GET, "/health-check", HttpHeaderNames.ACCEPT, "application/json");
                AggregatedHttpResponse response = webClient.execute(getHeaders).aggregate().join();

                // Get headers and verify a 200 OK response.
                ResponseHeaders headers = response.headers();
                String statusCode = headers.get(HttpHeaderNames.STATUS);

                if (statusCode.equals("200")) {
                    logger.log(Level.INFO, "Available!");
                    online = true;
                    break;
                } else {
                    logger.log(Level.INFO, "Didn't get proper response, status code: " + statusCode);
                }
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "Runtime exception occurred: " + e);
            }

            logger.log(Level.INFO, "Sleeping one second...");
            Thread.sleep(1000);
        }

        if (!online) {
            logger.log(Level.INFO, "HelloServer never came online!");
            throw new ServerAvailabilityException();
        }
    }

    public static void stopHelloServerAndWaitUntilUnavailable() throws InterruptedException {
        helloServer.close();
        helloServer.blockUntilShutdown();
    }

    public static void startExternalServerAndWaitUntilAvailable() throws InterruptedException, IOException {
        externalServer = ExternalServer.serve();
        CompletableFuture<Void> worldServerFuture = externalServer.start();

        // Wait up to 10 seconds for HelloServer to start.
        boolean online = false;

        for (int i = 0; i < 10; i++) {
            logger.log(Level.INFO, "Waiting for ExternalServer to come online...");

            try {
                // Get remote resource.
                String uri = "http://" + Helper.getHost("external") + ":" + Helper.getPort("external") + "/";
                ClientBuilder cb = Clients.builder(uri);
                WebClient webClient = cb.build(WebClient.class);
                RequestHeaders getHeaders = RequestHeaders.of(
                        HttpMethod.GET, "/health-check", HttpHeaderNames.ACCEPT, "application/json");
                AggregatedHttpResponse response = webClient.execute(getHeaders).aggregate().join();

                // Get headers and verify a 200 OK response.
                ResponseHeaders headers = response.headers();
                String statusCode = headers.get(HttpHeaderNames.STATUS);

                if (statusCode.equals("200")) {
                    logger.log(Level.INFO, "Available, still returning 200.");
                    online = true;
                    break;
                } else {
                    logger.log(Level.INFO, "Didn't get proper response, status code: " + statusCode);
                }
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, "Runtime exception occurred: " + e);
            }

            logger.log(Level.INFO, "Sleeping one second...");
            Thread.sleep(1000);
        }

        if (!online) {
            logger.log(Level.INFO, "ExternalServer never came online!");
            throw new ServerAvailabilityException();
        }
    }

    public static void stopExternalServerAndWaitUntilUnavailable() throws InterruptedException {
        externalServer.close();
        externalServer.blockUntilShutdown();
    }
}
