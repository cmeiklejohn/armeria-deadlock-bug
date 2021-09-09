package cloud.filibuster.instrumentation.libraries.armeria.mocks;

import cloud.filibuster.instrumentation.Helper;
import cloud.filibuster.instrumentation.libraries.armeria.http.FilibusterDecoratingHttpService;
import cloud.filibuster.instrumentation.libraries.armeria.http.FilibusterDecoratingHttpClient;
import com.linecorp.armeria.client.ClientBuilder;
import com.linecorp.armeria.client.Clients;
import com.linecorp.armeria.client.WebClient;

import com.linecorp.armeria.common.HttpMethod;
import com.linecorp.armeria.common.RequestHeaders;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.HttpRequest;

import com.linecorp.armeria.server.AbstractHttpService;
import com.linecorp.armeria.server.Server;
import com.linecorp.armeria.server.ServerBuilder;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.grpc.GrpcService;
import io.grpc.ServerInterceptors;
import io.grpc.ServerServiceDefinition;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HelloServer {
    private static final Logger logger = Logger.getLogger(HelloServer.class.getName());

    final private static String serviceName = "hello";

    private HelloServer() {

    }

    public static Server serve() throws IOException {
        ServerBuilder sb = Server.builder();
        sb.http(Helper.getPort(serviceName));

        sb.service("/test", new AbstractHttpService() {
            @Override
            protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                // Make a dummy call to an external service.
                String uri = "http://" + Helper.getHost("external") + ":" + Helper.getPort("external") + "/";
                ClientBuilder cb1 = Clients.builder(uri);
                cb1.decorator(delegate -> new FilibusterDecoratingHttpClient(delegate, serviceName));
                WebClient webClient1 = cb1.build(WebClient.class);
                RequestHeaders getHeaders1 = RequestHeaders.of(
                        HttpMethod.GET, "/", HttpHeaderNames.ACCEPT, "application/json");
                logger.log(Level.INFO, "/test issuing request to " + uri);
                webClient1.execute(getHeaders1).aggregate().join();
                logger.log(Level.INFO, "/request completed.");

                // Return a response as long as we don't throw.
                return HttpResponse.of("Hello, world!");
            }
          }.decorate(delegate -> new FilibusterDecoratingHttpService(delegate, serviceName)));

        sb.service("/health-check", new AbstractHttpService() {
            @Override
            protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("status", "OK");
                return HttpResponse.of(jsonObject.toString());

            }
        }.decorate(delegate -> new FilibusterDecoratingHttpService(delegate, serviceName)));

        sb.service("/", new AbstractHttpService() {
            @Override
            protected HttpResponse doGet(ServiceRequestContext ctx, HttpRequest req) {
                return HttpResponse.of("Hello, world!");
            }
        }.decorate(delegate -> new FilibusterDecoratingHttpService(delegate, serviceName)));

        return sb.build();
    }

    public static void main(String[] args) throws IOException {
        Server server = serve();
        CompletableFuture<Void> future = server.start();
        future.join();
    }
}
