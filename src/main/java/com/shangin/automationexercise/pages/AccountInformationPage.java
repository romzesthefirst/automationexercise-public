package com.shangin.automationexercise.pages;

import org.openqa.selenium.By;

import com.shangin.automationexercise.base.BasePage;
import com.shangin.automationexercise.model.User;

public class AccountInformationPage extends BasePage {

    private static final By ACCOUNT_INFO_TITLE
            = By.xpath("//div[contains(@class,'login-form')][.//input[@data-qa='password']]/h2");
    private static final By MR_RADIO = By.cssSelector("[data-qa='title'] [value='Mr']");
    private static final By MRS_RADIO = By.cssSelector("[data-qa='title'] [value='Mrs']");
    // private static final By NAME_INPUT = By.cssSelector("[data-qa='name']");
    // private static final By EMAIL_INPUT = By.cssSelector("[data-qa='email']");
    private static final By PASSWORD_INPUT = By.cssSelector("[data-qa='password']");
    private static final By DAY_DROPDOWN = By.cssSelector("[data-qa='days']");
    private static final By MONTH_DROPDOWN = By.cssSelector("[data-qa='months']");
    private static final By YEAR_DROPDOWN = By.cssSelector("[data-qa='years']");
    private static final By NEWSLETTER_CHECKBOX = By.cssSelector("input[id='newsletter']");
    private static final By OPTIN_CHECKBOX = By.cssSelector("input[id='optin']");
    private static final By FIRST_NAME_INPUT = By.cssSelector("[data-qa='first_name']");
    private static final By LAST_NAME_INPUT = By.cssSelector("[data-qa='last_name']");
    private static final By COMPANY_INPUT = By.cssSelector("[data-qa='company']");
    private static final By ADDRESS1_INPUT = By.cssSelector("[data-qa='address']");
    private static final By ADDRESS2_INPUT = By.cssSelector("[data-qa='address2']");
    private static final By COUNTRY_SELECT = By.id("country");
    private static final By STATE_INPUT = By.cssSelector("[data-qa='state']");
    private static final By CITY_INPUT = By.cssSelector("[data-qa='city']");
    private static final By ZIPCODE_INPUT = By.cssSelector("[data-qa='zipcode']");
    private static final By MOB_NUMBER_INPUT = By.cssSelector("[data-qa='mobile_number']");
    private static final By CREATE_ACCOUNT_BTN = By.cssSelector("[data-qa='create-account']");

    public AccountCreatedPage createAccount(User user) {
        fillAccountInformation(user);
        fillAddressInformation(user);
        click(CREATE_ACCOUNT_BTN);
        AccountCreatedPage page = new AccountCreatedPage();
        page.waitUntilLoaded();
        return page;
    }

    private void fillAccountInformation(User user) {
        selectTitle(user.title());
        type(PASSWORD_INPUT, user.password());
        selectByValue(DAY_DROPDOWN, user.dayOfBirth());
        selectByValue(MONTH_DROPDOWN, user.monthOfBirth());
        selectByValue(YEAR_DROPDOWN, user.yearOfBirth());
        check(NEWSLETTER_CHECKBOX);
        check(OPTIN_CHECKBOX);
    }

    private void fillAddressInformation(User user) {
        type(FIRST_NAME_INPUT, user.firstName());
        type(LAST_NAME_INPUT, user.lastName());
        type(COMPANY_INPUT, user.company());
        type(ADDRESS1_INPUT, user.addressLine1());
        type(ADDRESS2_INPUT, user.addressLine2());
        selectByValue(COUNTRY_SELECT, user.country());
        type(STATE_INPUT, user.state());
        type(CITY_INPUT, user.city());
        type(ZIPCODE_INPUT, user.zipcode());
        type(MOB_NUMBER_INPUT, user.phone());
    }

    public void selectTitle(String title) {
        switch (title.toLowerCase()) {
        case "mr":
            click(MR_RADIO);
            break;

        case "mrs":
            click(MRS_RADIO);
            break;

        default:
            throw new IllegalArgumentException("Unsupported title: " + title);
        }
    }

    public String getPageTitle() {
        return waitUntilVisible(ACCOUNT_INFO_TITLE).getText();
    }

    @Override
    public boolean isLoaded() {
        return isDisplayed(PASSWORD_INPUT) && isDisplayed(CREATE_ACCOUNT_BTN);
    }

    @Override
    public void waitUntilLoaded() {
        waitUntilVisible(ACCOUNT_INFO_TITLE);
        waitUntilVisible(PASSWORD_INPUT);
        waitUntilClickable(CREATE_ACCOUNT_BTN);
        removeAds();
    }

}
