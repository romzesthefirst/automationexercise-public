@ui @allure.label.epic:Report-demonstration
Feature: Cucumber failure evidence
  Scenario: Diagnose a deliberate failure on a synthetic browser page
    Given a local browser failure probe is opened
    Then the browser probe deliberately fails
