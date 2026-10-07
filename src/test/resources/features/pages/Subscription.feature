@ui
Feature: Subscription

  # Test Case 10: Verify Subscription in home page
  @TC_10
  Scenario: Subscribe from the Home page
    Given the home page is opened
    Then the home page should be displayed
    When the user scrolls to the Home page footer
    Then the subscription heading should be displayed
    When the user subscribes with a generated email address
    Then the subscription success message should be displayed

  # Test Case 11: Verify Subscription in Cart page
  @TC_11
  Scenario: Subscribe from the Cart page
    Given the home page is opened
    Then the home page should be displayed
    When the user opens the Cart page from the header
    And the user scrolls to the Cart page footer
    Then the subscription heading should be displayed
    When the user subscribes with a generated email address
    Then the subscription success message should be displayed
