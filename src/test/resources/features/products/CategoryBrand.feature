@ui
Feature: Category and Brand filters

  # My Test Case 002: View all category products
  @MTC_002
  Scenario Outline: Open product category from Home page
    Given the home page is opened
    When the user opens "<subcategory>" from the "<category>" category
    Then the "<category>" - "<subcategory>" category page should be displayed

    Examples:
      | category | subcategory   |
      | Women    | Dress         |
      | Women    | Tops          |
      | Women    | Saree         |
      | Men      | Tshirts       |
      | Men      | Jeans         |
      | Kids     | Dress         |
      | Kids     | Tops & Shirts |

  # Test Case 18: View Category Products
  @TC_18
  Scenario: View Category Products
    Given the home page is opened
    When the user opens "Dress" from the "Women" category
    Then the "Women" - "Dress" category page should be displayed
    When the user opens "Jeans" from the "Men" category
    Then the "Men" - "Jeans" category page should be displayed

  # Test Case 19: View & Cart Brand Products
  @TC_19
  Scenario: View and cart brand products
    Given the home page is opened
    When the user opens the Products page from the header
    And opens "Biba" brand from Products page
    Then the "Biba" brand page should be displayed
    When opens "Madame" brand from Brand page
    Then the "Madame" brand page should be displayed
