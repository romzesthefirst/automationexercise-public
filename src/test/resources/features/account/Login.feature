@ui
Feature: Login

  # Test Case 2: Login User with correct email and password
  @TC_02 @user
  Scenario: Login with valid credentials
    Given a registered user exists
    When the user logs in with valid credentials
    Then the user should be logged in
    When the user deletes the account
    Then the account deleted page should be displayed

  # Test Case 3: Login User with incorrect email and password
  @TC_03
  Scenario: Login with invalid credentials
    Given credentials for an unregistered user
    When the user attempts to log in
    Then an invalid login error message should be displayed

  # Test Case 4: Logout User
  @TC_04 @user
  Scenario: Logout user
    Given a registered user exists
    When the user logs in with valid credentials
    And the user logs out
    Then the login page should be displayed
