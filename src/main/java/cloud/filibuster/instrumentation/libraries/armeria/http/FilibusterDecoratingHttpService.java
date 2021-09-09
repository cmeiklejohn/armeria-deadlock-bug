package cloud.filibuster.instrumentation.libraries.armeria.http;

import cloud.filibuster.instrumentation.Helper;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.server.HttpService;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.SimpleDecoratingHttpService;

import java.util.logging.Logger;

import static cloud.filibuster.instrumentation.Helper.getDisableInstrumentationFromEnvironment;
import static cloud.filibuster.instrumentation.Helper.getDisableServerCommunicationFromEnvironment;

final public class FilibusterDecoratingHttpService extends SimpleDecoratingHttpService {
    private static final Logger logger = Logger.getLogger(FilibusterDecoratingHttpService.class.getName());

    final private String serviceName;

    public FilibusterDecoratingHttpService(HttpService delegate, String serviceName) {
        super(delegate);
        this.serviceName = serviceName;
    }

    @Override
    public HttpResponse serve(ServiceRequestContext ctx, HttpRequest req) throws Exception {
        HttpService delegate = (HttpService) unwrap();
        return delegate.serve(ctx, req);
    }
}