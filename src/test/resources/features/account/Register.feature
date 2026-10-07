@ui
Feature: Registration

  # Test Case 1: Register User
  @TC_01
  Scenario: Register a new user
    Given credentials for an unregistered user
    And the home page is opened
    When a new user signs up with name and email
    And completes the account registration form
    Then the account created page should be displayed
    When the user continues to the application
    Then the username should be visible in the header
    When the user deletes the account
    Then the account should be deleted successfully
    And the user can return to the home page

  # Test Case 5: Register User with existing email
  @TC_05 @user
  Scenario: Register user with existing email
    Given a registered user exists
    And the home page is opened
    When the user attempts to sign up with the registered email
    Then an email already exists error message should be displayed
