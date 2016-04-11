package sample.page;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class ReserveInputPage {

    private final WebDriver driver;

    @FindBy(name = "reserve_y")
    private WebElement reserveYear;
    @FindBy(name = "reserve_m")
    private WebElement reserveMonth;
    @FindBy(name = "reserve_d")
    private WebElement reserveDay;
    @FindBy(name = "reserve_t")
    @CacheLookup
    private WebElement reserveTerm;
    @FindBy(name = "hc")
    private WebElement headCount;
    @FindBy(id = "breakfast_on")
    private WebElement breakfastOn;
    @FindBy(id = "breakfast_off")
    private WebElement breakfastOff;
    @FindBy(name = "plan_a")
    private WebElement earlyCheckInPlan;
    @FindBy(name = "plan_b")
    private WebElement sightseeingPlan;
    @FindBy(name = "gname")
    private WebElement guestName;
    @FindBy(id = "goto_next")
    private WebElement goToNextButton;

    public ReserveInputPage(WebDriver driver) {
        this.driver = driver;
        if (!StringUtils.equals(this.driver.getTitle(), "予約情報入力")) {
            throw new IllegalStateException("現在のページが間違っています: " + this.driver.getTitle());
        }
    }

    public void setReserveDate(String year, String month, String day) {
        reserveYear.clear();
        reserveYear.sendKeys(year);
        reserveMonth.clear();
        reserveMonth.sendKeys(month);
        reserveDay.clear();
        reserveDay.sendKeys(day);
    }

    public void setReserveTerm(String value) {
        reserveTerm.clear();
        reserveTerm.sendKeys(value);
    }

    public void setHeadCount(String value) {
        headCount.clear();
        headCount.sendKeys(value);
    }

    public void setBreakfast(boolean on) {
        if (on) {
            breakfastOn.click();
        } else {
            breakfastOff.click();
        }
    }

    public void setEarlyCheckInPlan(boolean checked) {
        if (earlyCheckInPlan.isSelected() != checked) {
            earlyCheckInPlan.click();
        }
    }

    public void setSightseeingPlan(boolean checked) {
        if (sightseeingPlan.isSelected() != checked) {
            sightseeingPlan.click();
        }
    }

    public void setGuestName(String value) {
        guestName.clear();
        guestName.sendKeys(value);
    }

    public ReserveConfirmPage goToNext() {
        goToNextButton.click();
        return PageFactory.initElements(driver, ReserveConfirmPage.class);
    }

    public ReserveErrorPage goToNextExpectingFailure() {
        goToNextButton.click();
        return PageFactory.initElements(driver, ReserveErrorPage.class);
    }
}
