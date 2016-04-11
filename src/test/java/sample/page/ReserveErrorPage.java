package sample.page;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReserveErrorPage {

    private final WebDriver driver;

    @FindBy(id = "errorcheck_result")
    private WebElement message;
    
    public ReserveErrorPage(WebDriver driver) {
        this.driver = driver;
        if (!StringUtils.equals(this.driver.getTitle(), "予約エラー")) {
            throw new IllegalStateException("現在のページが間違っています: " + this.driver.getTitle());
        }
    }

    public String getMessage() {
        return message.getText();
    }
}
