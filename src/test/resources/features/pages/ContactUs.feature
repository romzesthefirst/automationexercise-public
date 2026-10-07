@ui
Feature: Contact Us Form

  # Test Case 6: Contact Us Form
  @TC_06
  Scenario: Submit the Contact Us form successfully
    Given the home page is opened
    When the user opens the Contact Us page from the header
    Then the Get In Touch section should be displayed
    When the user fills in the contact form
    And uploads a file
    And submits the contact form
    And confirms the submission alert
    Then the contact form success message should be displayed
    When the user returns to the home page from the header
    Then the home page should be displayed
