package sample;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.ProxyServer;
import net.lightbody.bmp.proxy.http.BrowserMobHttpResponse;
import net.lightbody.bmp.proxy.http.ResponseInterceptor;
import org.junit.BeforeClass;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

public class TestBase {

    protected static WebDriver driver = null;

    @BeforeClass
    public static void beforeClass() {
        if (driver == null) {
            Logger logger = Logger.getLogger("net.lightbody.bmp");
            logger.setLevel(Level.OFF);
            ProxyServer proxy = new ProxyServer(0);
            proxy.start();
            proxy.addResponseInterceptor(new ResponseInterceptor() {
                @Override
                public void process(BrowserMobHttpResponse response, Har har) {
                    if (response == null
                        || response.getEntry() == null
                        || response.getEntry().getResponse() == null) {
                        return;
                    }
                    int status = response.getEntry().getResponse().getStatus();
                    String url = response.getEntry().getRequest().getUrl();
                    if (status >= 400) {
                        System.out.println("HTTP status error: " + url + ": " + status);
                    }
                }
            });
            Proxy seProxy = proxy.seleniumProxy();
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.PROXY, seProxy);
            driver = new FirefoxDriver(capabilities);
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    driver.quit();
                }
            });
        }
    }
}
