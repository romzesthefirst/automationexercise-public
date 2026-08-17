@ui
Feature: Cart

  # Test Case 17: Remove Products From Cart
  @TC_06
  Scenario: Remove a product from the cart
    Given the home page is opened
    When the user adds the following products to the cart
      | Sleeveless Dress                 |
      | Stylish Dress                    |
      | Rose Pink Embroidered Maxi Dress |
    And the user opens the Cart page from the header
    Then all added products should be present in the cart
    When the user removes "Stylish Dress" from the cart
    Then the cart should not contain removed product

  # Test Case 20: Search Products and Verify Cart After Login
  @TC_20 @user
  Scenario: Search products and verify cart after login
    Given a registered user exists
    And the home page is opened
    When the user opens the Products page from the header
    And the user searches for "tshirt"
    Then the search results should be displayed
    And all displayed products should match "tshirt"
    When the user adds all search results to the cart
    And the user opens the Cart page from the header
    Then all added products should be present in the cart
    When the user opens the Signup Login page from the header
    And the user logs in with valid credentials
    And the user opens the Cart page from the header
    Then all previously added products should still be present in the cart

  # Test Case 22: Add to cart from Recommended items
  @TC_22
  Scenario: Add a recommended item to the cart
    Given the home page is opened
    When the user scrolls to the Recommended Items section
    And the user adds the first recommended item to the cart
    And opens the Cart page from modal window
    Then the cart should contain the added product
