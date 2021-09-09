package cloud.filibuster.instrumentation.libraries.armeria.mocks.http.tests;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

import static cloud.filibuster.instrumentation.TestHelper.startExternalServerAndWaitUntilAvailable;
import static cloud.filibuster.instrumentation.TestHelper.startHelloServerAndWaitUntilAvailable;
import static cloud.filibuster.instrumentation.TestHelper.stopExternalServerAndWaitUntilUnavailable;
import static cloud.filibuster.instrumentation.TestHelper.stopHelloServerAndWaitUntilUnavailable;

public class HelloServerTest {
    @BeforeAll
    public static void changeLogLevel() {
        // Change the log level, so we avoid seeing debug message during test execution.
        final Logger logger = Logger.getRootLogger();
        logger.setLevel(Level.WARN);
    }

    public void startExternalServer() throws InterruptedException, IOException {
        startExternalServerAndWaitUntilAvailable();
    }

    public void stopExternalServer() throws InterruptedException {
        stopExternalServerAndWaitUntilUnavailable();
    }

    public void startHelloServer() throws InterruptedException, IOException {
        startHelloServerAndWaitUntilAvailable();
    }

    public void stopHelloServer() throws InterruptedException {
        stopHelloServerAndWaitUntilUnavailable();
    }
}
