package sample.page;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class ReserveConfirmPage {

    private final WebDriver driver;

    @FindBy(id = "price")
    private WebElement price;
    @FindBy(id = "commit")
    private WebElement commitButton;
    
    public ReserveConfirmPage(WebDriver driver) {
        this.driver = driver;
        if (!StringUtils.equals(this.driver.getTitle(), "予約内容確認")) {
            throw new IllegalStateException("現在のページが間違っています: " + this.driver.getTitle());
        }
    }

    public String getPrice() {
        return price.getText();
    }

    public void commit() {
        commitButton.click();
    }
}
