package com.shangin.automationexercise.base;

import com.shangin.automationexercise.components.AddToCartModalComponent;
import com.shangin.automationexercise.components.FooterComponent;
import com.shangin.automationexercise.components.HeaderComponent;
import com.shangin.automationexercise.core.AbstractPageObject;
import com.shangin.automationexercise.support.AdsHandler;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

public abstract class BasePage extends AbstractPageObject {

    private static final By HEADER = By.id("header");
    private static final By ADD_TO_CART_MODAL = By.cssSelector("#cartModal .modal-content");
    private static final By FOOTER = By.id("footer");
    private static final By SCROLL_UP_BUTTON = By.id("scrollUp");

    public HeaderComponent header() {
        return new HeaderComponent(HEADER);
    }

    public FooterComponent footer() {
        return new FooterComponent(FOOTER);
    }

    @Override
    protected final WebElement find(By locator) {
        return driver.findElement(locator);
    }

    @Override
    protected final List<WebElement> findAll(By locator) {
        return driver.findElements(locator);
    }

    protected final void removeAds() {
        AdsHandler.disableGoogleAnnotations();
        AdsHandler.removeGoogleAds();
    }

    public abstract boolean isLoaded();

    public abstract void waitUntilLoaded();

    protected final void scrollPageToElement(WebElement element) {
        ((JavascriptExecutor) driver)
                .executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public final void scrollToBottom() {
        ((JavascriptExecutor) driver)
                .executeScript("window.scrollTo(0, document.body.scrollHeight);");
        waitUntilPageAtBottom();
    }

    public final void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
        waitUntilPageAtTop();
    }

    public void scrollToFooter() {
        scrollPageToElement(find(FOOTER));
    }

    protected final AddToCartModalComponent waitForAddToCartModal() {
        waitUntilVisible(ADD_TO_CART_MODAL);
        return new AddToCartModalComponent(ADD_TO_CART_MODAL);
    }

    public final void clickScrollUp() {
        click(SCROLL_UP_BUTTON);
        waitUntilPageAtTop();
    }

    public final boolean isPageAtTop() {
        return getScrollY() == 0;
    }

    protected final void waitUntilPageAtTop() {
        wait.until(ignored -> getScrollY() == 0);
    }

    protected final void waitUntilPageAtBottom() {
        wait.until(
                ignored -> {
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    return (Boolean)
                            js.executeScript(
                                    """
                    return Math.ceil(window.scrollY + window.innerHeight)
                            >= document.documentElement.scrollHeight;
                    """);
                });
    }

    private long getScrollY() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        return (Long) js.executeScript("return Math.round(window.scrollY);");
    }

    public void goBack() {
        driver.navigate().back();
    }
}
