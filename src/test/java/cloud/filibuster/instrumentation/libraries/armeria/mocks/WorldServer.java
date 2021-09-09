package cloud.filibuster.instrumentation.libraries.armeria.mocks;

import cloud.filibuster.instrumentation.Helper;
import cloud.filibuster.instrumentation.libraries.armeria.http.FilibusterDecoratingHttpService;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
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

public class WorldServer {
    final private static String serviceName = "world";

    public static boolean shouldReturnServerError = false;

    private WorldServer() {

    }

    public static Server serve() throws IOException {
        ServerBuilder sb = Server.builder();
        sb.http(Helper.getPort(serviceName));

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
                if (shouldReturnServerError) {
                    return HttpResponse.of(HttpStatus.INTERNAL_SERVER_ERROR);
                } else {
                    return HttpResponse.of(HttpStatus.OK);
                }
            }
        }.decorate(delegate -> new FilibusterDecoratingHttpService(delegate, serviceName)));

        return sb.build();
    }
}
