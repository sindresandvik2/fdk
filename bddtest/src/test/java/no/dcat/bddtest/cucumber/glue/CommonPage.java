package no.dcat.bddtest.cucumber.glue;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

import com.google.common.base.Predicate;
import io.github.bonigarcia.wdm.PhantomJsDriverManager;
import no.dcat.bddtest.cucumber.SpringIntegrationTestConfig;
import org.apache.commons.lang3.StringUtils;;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Topclass for glue-code for pagetesting.
 */
public abstract class CommonPage extends SpringIntegrationTestConfig {
    private final Logger logger = LoggerFactory.getLogger(CommonPage.class);
    WebDriver driver = null;
    private static final String LOCAL_PATH_TO_IE_DRIVER = "src/main/resources/IEDriverServer.exe";

    public void openBrowser() throws Throwable {
        File file = new File(LOCAL_PATH_TO_IE_DRIVER);
        File fileC = new File("src/test/resources/chromedriver.exe");
        File fileF = new File("src/test/resources/phantomjs.exe");

        System.setProperty("webdriver.ie.driver", file.getAbsolutePath());
        System.setProperty("webdriver.chrome.driver", fileC.getAbsolutePath());
        System.setProperty("phantomjs.binary.path", fileF.getAbsolutePath());

        DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
        DesiredCapabilities capsC = DesiredCapabilities.chrome();

        caps.setCapability("ignoreZoomSetting", true);

        //driver = new InternetExplorerDriver(caps);
        //driver = new ChromeDriver(capsC);
        driver = new PhantomJSDriver();
    }

    protected String getElement(String idProvenanceText) {
        String element;
        try {
            element = driver.findElement(By.id(idProvenanceText)).getText();
        } catch(Exception e) {
            element = "";
        }
        return element;
    }

    public void openPage(String page) throws Throwable {
        String hostname = getEnv("fdk.hostname");
        int port = getEnvInt("fdk.port");

        driver.get(String.format("http://%s:%d/%s", hostname, port, page));
    }

    public boolean openPageWaitRetry(String page, String idToFind, int waitTimes) {
        return openPageWaitRetry(page, d -> driver.findElement(By.id(idToFind)).isDisplayed(), waitTimes);
    }

    public boolean openPageWaitRetry(String page, Predicate<WebDriver> waitCondition, int waitTimes) {
        logger.info(String.format("Waiting for page %s %d times", page, waitTimes));
        try {
            openPage(page);
        } catch (Throwable t) {
            logger.error("Open page failed {}",t.getMessage());
        }
        if (waitTimes <= 0) {
            return false;
        }
        try {
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(waitCondition);
            return true;
        } catch (TimeoutException toe) {
            openPageWaitRetry(page, waitCondition, --waitTimes);
        }
        return false;
    }

    protected String getEnv(String env) {
        String value = System.getenv(env);

        if (StringUtils.isEmpty(value)) {
            throw new RuntimeException(String.format("Environment %s variable is not defines.", env));
        }

        return value;
    }

    protected int getEnvInt(String env) {
        return Integer.valueOf(getEnv(env));
    }
}
