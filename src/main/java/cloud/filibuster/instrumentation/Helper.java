package cloud.filibuster.instrumentation;

import cloud.filibuster.instrumentation.datatypes.Pair;
import com.linecorp.armeria.common.AggregatedHttpResponse;
import io.netty.buffer.ByteBuf;
import org.apache.commons.lang.NotImplementedException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Helper {
    private Helper() {

    }

    private static final Logger logger = Logger.getLogger(Helper.class.getName());

    public static boolean getDisableServerCommunicationFromEnvironment() {
        String disableServerCommunication = System.getenv("DISABLE_SERVER_COMMUNICATION");

        if (disableServerCommunication == null) {
            return false;
        }

        switch(disableServerCommunication)
        {
            case "1":
            case "true":
                return true;
            default:
                return false;
        }
    }

    public static boolean getDisableInstrumentationFromEnvironment() {
        String disableInstrumentation = System.getenv("DISABLE_INSTRUMENTATION");

        if (disableInstrumentation == null) {
            return false;
        }

        switch(disableInstrumentation)
        {
            case "1":
            case "true":
                return true;
            default:
                return false;
        }
    }

    private static String readFilibusterNetworkingFile() throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get("../../networking.json"));
        return new String(encoded, StandardCharsets.US_ASCII);
    }

    public static int getPort(String serviceName) {
        try {
            String networkingFileContents = readFilibusterNetworkingFile();
            JSONObject networkingJsonObject = new JSONObject(networkingFileContents);
            JSONObject jsonObject = networkingJsonObject.getJSONObject(serviceName);
            return jsonObject.getInt("port");
        } catch (IOException e) {
            // TODO: testing, should use dependency injection somehow eventually.
            switch (serviceName) {
                case "hello":
                    return 5002;
                case "world":
                    return 5003;
                case "external":
                    return 5004;
                case "filibuster":
                    return 5005;
                default:
                    throw new NotImplementedException(e);
            }
        }
    }

    public static String getHost(String serviceName) {
        try {
            String networkingFileContents = readFilibusterNetworkingFile();
            JSONObject networkingJsonObject = new JSONObject(networkingFileContents);
            JSONObject jsonObject = networkingJsonObject.getJSONObject(serviceName);
            return jsonObject.getString("default-host");
        } catch (IOException e) {
            // TODO: testing, should use dependency injection somehow eventually.
            return "0.0.0.0";
        }
    }

    public static String getFilibusterHost() {
        return getHost("filibuster");
    }

    public static int getFilibusterPort() {
        return getPort("filibuster");
    }

}
