package com.shangin.automationexercise.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.shangin.automationexercise.base.BaseComponent;

public class AddressComponent extends BaseComponent {

    private static final By TITLE = By.cssSelector(".address_title h3");

    private static final By FULL_NAME = By.cssSelector(".address_firstname.address_lastname");

    private static final By ADDRESS_LINES = By.cssSelector(".address_address1.address_address2");

    private static final By CITY_STATE_POSTCODE
            = By.cssSelector(".address_city.address_state_name.address_postcode");

    private static final By COUNTRY = By.cssSelector(".address_country_name");

    private static final By PHONE = By.cssSelector(".address_phone");

    public AddressComponent(WebElement root) {
        super(root);
    }
    
    public List<String> getAddressLines() {
        return findAll(ADDRESS_LINES)
                .stream()
                .map(WebElement::getText)
                .toList();
    }
    
    public String getPhone() {
        return getText(PHONE);
    }
    
    public String getCountry() {
        return getText(COUNTRY);
    }
    
    public String getCityStatePostcode() {
        return getText(CITY_STATE_POSTCODE);
    }
    
    public String getTitle() {
        return getText(TITLE);
    }
    
    public String getFullName() {
        return getText(FULL_NAME);
    }
    
    public String getCompany() {
        return getAddressLines().get(0);
    }

    public String getAddressLine1() {
        return getAddressLines().get(1);
    }

    public String getAddressLine2() {
        return getAddressLines().get(2);
    }
    
}