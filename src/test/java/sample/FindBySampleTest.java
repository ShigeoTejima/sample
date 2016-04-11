package sample;

import static org.hamcrest.CoreMatchers.*;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import sample.page.ReserveConfirmPage;
import sample.page.ReserveErrorPage;
import sample.page.ReserveInputPage;

public class FindBySampleTest {

    private WebDriver driver;

    @Before
    public void before() {
        driver = new FirefoxDriver();
    }

    @After
    public void after() {
        driver.quit();
    }

    @Test
    public void test_success() {
        driver.get("http://example.selenium.jp/reserveApp");
        ReserveInputPage inputPage = PageFactory.initElements(driver, ReserveInputPage.class);
        inputPage.setReserveDate("2016", "4", "16");
        inputPage.setReserveTerm("1");
        inputPage.setHeadCount("2");
        inputPage.setBreakfast(true);
        inputPage.setEarlyCheckInPlan(true);
        inputPage.setGuestName("サンプルユーザ");

        ReserveConfirmPage confirmPage = inputPage.goToNext();
        assertThat(confirmPage.getPrice(), is("21500"));
        confirmPage.commit();
    }

    @Test
    public void test_failure() {
        driver.get("http://example.selenium.jp/reserveApp");
        ReserveInputPage inputPage = PageFactory.initElements(driver, ReserveInputPage.class);
        inputPage.setReserveDate("1999", "1", "1");
        inputPage.setGuestName("テストユーザ");
        ReserveErrorPage errorPage = inputPage.goToNextExpectingFailure();
        assertThat(errorPage.getMessage(), is("宿泊日には、翌日以降の日付を指定してください。"));
    }
}
