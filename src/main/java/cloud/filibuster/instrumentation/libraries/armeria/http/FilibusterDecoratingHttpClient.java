package cloud.filibuster.instrumentation.libraries.armeria.http;

import com.linecorp.armeria.client.ClientRequestContext;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.SimpleDecoratingHttpClient;
import com.linecorp.armeria.client.UnprocessedRequestException;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.common.FilteredHttpResponse;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.RequestHeadersBuilder;
import io.netty.channel.ConnectTimeoutException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

final public class FilibusterDecoratingHttpClient extends SimpleDecoratingHttpClient {
    private static final Logger logger = Logger.getLogger(FilibusterDecoratingHttpClient.class.getName());

    final private String serviceName;

    public FilibusterDecoratingHttpClient(HttpClient delegate, String serviceName) {
        super(delegate);
        this.serviceName = serviceName;
    }

    @Override
    public HttpResponse execute(ClientRequestContext ctx, HttpRequest req) throws Exception {
        HttpResponse res = unwrap().execute(ctx, req);
        logger.log(Level.INFO, "got response, returning: " + req.uri().toString());
        logger.log(Level.INFO, "res.toString(): " + res.toString());
        return res;
    }
}

