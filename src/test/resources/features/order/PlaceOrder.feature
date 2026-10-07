@ui
Feature: Order

  # Test Case 14: Place Order: Register while Checkout
  @TC_14
  Scenario: Place an order by registering during checkout
    Given the home page is opened
    And credentials for an unregistered user
    When the user adds the following products to the cart
      | Fancy Green Top       |
      | Premium Polo T-Shirts |
    And the user opens the Cart page from the header
    And the user proceeds to checkout as guest
    And the user creates an account from checkout modal window
    And the user opens the Cart page from the header
    And the user proceeds to checkout as logged in
    Then the delivery address details should be correct
    And the order details should be correct
    And the total amount should be correct
    When the user enters an order comment
    And the user places the order
    And the user enters valid payment details
    And the user confirms the payment
    Then the order success message should be displayed
    And the user redirects to Payment Done page
    When the user deletes the account
    Then the account deleted page should be displayed

  # Test Case 15: Place Order: Register before Checkout
  @TC_15
  Scenario: Place an order registering before checkout
    Given the home page is opened
    And credentials for an unregistered user
    When the user opens the Signup Login page from the header
    And the user creates an account
    And the user adds the following products to the cart
      | Fancy Green Top       |
      | Premium Polo T-Shirts |
    And the user opens the Cart page from the header
    And the user proceeds to checkout as logged in
    Then the delivery address details should be correct
    And the order details should be correct
    And the total amount should be correct
    When the user enters an order comment
    And the user places the order
    And the user enters valid payment details
    And the user confirms the payment
    Then the order success message should be displayed
    And the user redirects to Payment Done page
    When the user deletes the account
    Then the account deleted page should be displayed

  # Test Case 16: Place Order: Login before Checkout
  @TC_16
  Scenario: Place an order by logging in before checkout
    Given the home page is opened
    And a registered user exists
    When the user logs in with valid credentials
    And the user adds the following products to the cart
      | Fancy Green Top       |
      | Premium Polo T-Shirts |
    And the user opens the Cart page from the header
    And the user proceeds to checkout as logged in
    Then the delivery address details should be correct
    And the order details should be correct
    And the total amount should be correct
    When the user enters an order comment
    And the user places the order
    And the user enters valid payment details
    And the user confirms the payment
    Then the order success message should be displayed
    And the user redirects to Payment Done page
    When the user deletes the account
    Then the account deleted page should be displayed

  # Test Case 23: Verify address details in checkout page
  @TC_23
  Scenario: Verify address details in checkout page
    Given credentials for an unregistered user
    And the home page is opened
    When a new user signs up with name and email
    And completes the account registration form
    Then the account created page should be displayed
    When the user continues to the application
    And the user adds the following products to the cart
      | Fancy Green Top       |
      | Premium Polo T-Shirts |
    And the user opens the Cart page from the header
    And the user proceeds to checkout as logged in
    Then the delivery address details should be correct
    And the billing address details should be correct
    When the user deletes the account
    Then the account deleted page should be displayed

  # Test Case 24: Download Invoice after purchase order
  @TC_24
  Scenario: Download Invoice after purchase order
    Given the home page is opened
    And credentials for an unregistered user
    When the user adds the following products to the cart
      | Fancy Green Top       |
      | Premium Polo T-Shirts |
    And the user opens the Cart page from the header
    And the user proceeds to checkout as guest
    And the user creates an account from checkout modal window
    And the user opens the Cart page from the header
    And the user proceeds to checkout as logged in
    Then the delivery address details should be correct
    And the order details should be correct
    And the total amount should be correct
    When the user enters an order comment
    And the user places the order
    And the user enters valid payment details
    And the user confirms the payment
    Then the order success message should be displayed
    And the user redirects to Payment Done page
    When the user downloads invoice
    Then the text in invoice should be correct
    When the user continues from the Payment Done page
    Then the home page should be displayed
    When the user deletes the account
    Then the account deleted page should be displayed
