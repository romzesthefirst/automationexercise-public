@ui
Feature: Scroll

  # Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality
  @TC_25
  Scenario: Verify Scroll Up using 'Arrow' button and Scroll Down functionality
    Given the home page is opened
    When the home page is scroll to the bottom
    Then subscription section is visible
    When the user clicks on the arrow up
    Then the home page is scrolled up to top
    And 'Full-Fledged practice website for Automation Engineers' text is visible

  # Test Case 26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality
  @TC_26
  Scenario: Verify Scroll Up without 'Arrow' button and Scroll Down functionality
    Given the home page is opened
    When the home page is scroll to the bottom
    Then subscription section is visible
    When the home page is scroll to the up
    Then the home page is scrolled up to top
    And 'Full-Fledged practice website for Automation Engineers' text is visible
