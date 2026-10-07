@ui
Feature: Test Case Page

  # Test Case 7: Verify Test Cases Page
  @TC_07 @smoke
  Scenario: Verify Test Cases Page
    Given the home page is opened
    When the user opens the Test Case page from the header
    Then the Test Cases page should be displayed
