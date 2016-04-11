package sample;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import sample.page.ReserveErrorPage;
import sample.page.ReserveInputPage;

@RunWith(Parameterized.class)
public class ParameterizedSampleTest {

    private WebDriver driver;
    private Calendar reserveDate;
    private String reserveTerm;
    private String headCount;
    private String guestName;
    private String errorMessage;

    @Before
    public void before() {
        driver = new FirefoxDriver();
    }

    @After
    public void after() {
        driver.quit();
    }

    private static Calendar sampleCalendar() {
        try {
            Date date = DateUtils.parseDate("2016-04-09", new String[]{"yyyy-MM-dd"});
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException ex) {
            Logger.getLogger(ParameterizedSampleTest.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Parameterized.Parameters(name = "メッセージ: {4}")
    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
            {null, "1", "2", "サンプルユーザ", "宿泊日が指定されていません"},
            {sampleCalendar(), "", "2", "サンプルユーザ", "泊数が指定されていません"},
            {sampleCalendar(), "1", "", "サンプルユーザ", "人数が指定されていません"},
            {sampleCalendar(), "1", "2", "", "お名前が指定されていません"}
        });
    }

    public ParameterizedSampleTest(Calendar reserveDate, String reserveTerm, String headCount, String guestName, String errorMessage) {
        this.reserveDate = reserveDate;
        this.reserveTerm = reserveTerm;
        this.headCount = headCount;
        this.guestName = guestName;
        this.errorMessage = errorMessage;
    }

    @Test
    public void test_failure_in_required() {
        driver.get("http://example.selenium.jp/reserveApp");
        ReserveInputPage inputPage = PageFactory.initElements(driver, ReserveInputPage.class);
        if (reserveDate == null) {
            inputPage.setReserveDate("", "", "");
        } else {
            inputPage.setReserveDate(
                    Integer.toString(reserveDate.get(Calendar.YEAR)),
                    Integer.toString(reserveDate.get(Calendar.MONTH) + 1),
                    Integer.toString(reserveDate.get(Calendar.DAY_OF_MONTH)));
        }
        inputPage.setReserveTerm(reserveTerm);
        inputPage.setHeadCount(headCount);
        inputPage.setGuestName(guestName);
        ReserveErrorPage errorPage = inputPage.goToNextExpectingFailure();
        assertThat(errorPage.getMessage(), is(errorMessage));
    }
}
