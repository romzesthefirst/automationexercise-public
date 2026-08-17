@ui
Feature: All products and product detail page

  # Test Case 8: Verify All Products and product detail page
  @TC_08
  Scenario: Verify all products and product detail page
    Given the home page is opened
    When the user opens the Products page from the header
    Then the products list should be displayed
    When the user opens the first product
    Then the product details page should be displayed
    And the product information should be displayed

  # Test Case 9: Search Product
  @TC_09
  Scenario Outline: Search Product
    Given the home page is opened
    When the user opens the Products page from the header
    Then the products list should be displayed
    When the user searches for "<productName>"
    Then the search results should be displayed
    And all displayed products should match "<productName>"

    Examples:
      | productName      |
      | tshirt           |
      | sleeves top      |
      | sleeveless dress |

  # Test Case 12: Add Products in Cart
  @TC_12
  Scenario Outline: Add Products in Cartt
    Given the home page is opened
    When the user opens the Products page from the header
    And adds product "<firstProduct>" to the cart
    And continues shopping from modal window
    And adds product "<secondProduct>" to the cart
    And opens the Cart page from modal window
    Then both products should be present in the cart with correct details

    Examples:
      | firstProduct         | secondProduct |
      | Blue Top             | Men Tshirt    |
      | Madame Top For Women | Winter Top    |

  # Test Case 13: Verify Product quantity in Cart
  @TC_13
  Scenario: Verify Product quantity in Cart
    Given the home page is opened
    When the user opens a random product on the home page
    And sets the product quantity to 4
    And adds the product to the cart
    And opens the Cart page from modal window
    Then the product should be present in the cart with quantity 4

  # Test Case 21: Add review on product
  @TC_21
  Scenario: Add a review to a product
    Given the home page is opened
    When the user opens the Products page from the header
    And opens a random product on the Products page
    And fills in the review form with valid data
    And submits the review
    Then the review success message should be displayed

  # My Test Case 001: Add Several products to cart with different quantities
  @MTC_001
  Scenario: Add several products to the cart with different quantities
    Given the home page is opened
    When the user opens the Products page from the header
    And opens product "Blue Top" from Products page
    And sets the product quantity to 2
    And adds the product to the cart
    And continues shopping from modal window
    And returns back to the Products page
    And opens product "Men Tshirt" from Products page
    And sets the product quantity to 5
    And adds the product to the cart
    And opens the Cart page from modal window
    Then both products should be present in the cart with correct details
