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
import org.json.JSONObject;

import java.io.IOException;

public class ExternalServer {
    final private static String serviceName = "external";

    private ExternalServer() {

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
                return HttpResponse.of(HttpStatus.OK);
            }
        }.decorate(delegate -> new FilibusterDecoratingHttpService(delegate, serviceName)));

        return sb.build();
    }
}
