@ui
@user
Feature: Login

  Scenario: Login with valid credentials
    Given a registered user exists
    When the user logs in with valid credentials
    Then the user should be logged in