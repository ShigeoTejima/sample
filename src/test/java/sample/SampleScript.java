package sample;

import java.io.File;
import org.apache.commons.io.FileUtils;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/*
 * - firefox
 * WebDriver driver = new FirefoxDriver();
 *
 * - chrome
 * System.setProperty("webdriver.chrome.driver", "D:/apps/selenium/driver/chromedriver.exe");
 * WebDriver driver = new ChromeDriver();
 *
 * - ie
 * System.setProperty("webdriver.ie.driver", "D:/apps/selenium/driver/IEDriverServer.exe");
 * WebDriver driver = new InternetExplorerDriver();
 */
public class SampleScript extends TestBase {

    @Test
    public void test_firefox() throws Exception {
        driver.get("http://example.selenium.jp/reserveApp");

        assertThat(driver.getTitle(), is("予約情報入力"));
        driver.findElement(By.id("reserve_day")).clear();
        driver.findElement(By.id("reserve_day")).sendKeys("8");
        driver.findElement(By.id("guestname")).sendKeys("サンプルユーザ");

        File tempFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        FileUtils.moveFile(tempFile, new File("D:/tmp/test/" + tempFile.getName()));

        ((JavascriptExecutor)driver).executeScript("window.alert('this is alert.');");
        driver.switchTo().alert().accept();

        driver.findElement(By.id("goto_next")).click();
    }
}
