# Coverage and test style

This source map was checked on 2026-10-07 against the website's
[UI cases](https://automationexercise.com/test_cases) and
[API cases](https://automationexercise.com/api_list). It maps implemented intent,
not every numbered website step or a current passing run. Actual acceptance
results and platform limits are recorded in
[RUN_GUIDE](RUN_GUIDE.md#compare-complete-browser-runs).

## Why two UI styles exist

TestNG UI tests are direct Java examples with typed page transitions and explicit
assertions. Cucumber scenarios show the same business flows as readable Gherkin,
sharing page objects, domain assertions, API setup and cleanup infrastructure.
The overlap is intentional for this portfolio: it demonstrates both runner
integrations, failure reporting and scenario dependency injection. It does not
add independent business coverage for the same case. CI runs both styles to
check both integrations; local development can select just `-Pui` or `-Pbdd`.

For a new behavior choose TestNG for a focused technical/parameterized check,
or Cucumber when a business narrative helps explain the flow. Add a duplicate
only when it demonstrates a distinct runner concern. The existing two styles
remain as examples, rather than being removed by this documentation task.

## UI case map

Every TestNG link points to the test method under
`src/test/java/com/shangin/automationexercise/tests/ui`; every BDD link points to
the scenario declaration under `src/test/resources/features`. Line anchors open
the corresponding source location on GitHub. Use `@TC_NN` to select a website
case. One outline contributes multiple runnable examples.

| Website ID / intent | TestNG class and method | Cucumber feature / scenario |
| --- | --- | --- |
| 1 / Registration | [RegistrationTest.shouldRegisterNewUser](../src/test/java/com/shangin/automationexercise/tests/ui/account/RegistrationTest.java#L23) | [Register](../src/test/resources/features/account/Register.feature#L6) / Register a new user |
| 2 / Valid login | [LoginTest.shouldLoginWithValidCredentials](../src/test/java/com/shangin/automationexercise/tests/ui/account/LoginTest.java#L23) | [Login](../src/test/resources/features/account/Login.feature#L6) / Login with valid credentials |
| 3 / Invalid login | [LoginTest.shouldNotLoginWithIncorrectCredentials](../src/test/java/com/shangin/automationexercise/tests/ui/account/LoginTest.java#L53) | [Login](../src/test/resources/features/account/Login.feature#L15) / Login with invalid credentials |
| 4 / Logout | [LoginTest.userCanLogOut](../src/test/java/com/shangin/automationexercise/tests/ui/account/LoginTest.java#L73) | [Login](../src/test/resources/features/account/Login.feature#L22) / Logout user |
| 5 / Duplicate registration | [RegistrationTest.shouldRejectRegistrationWithExistingEmail](../src/test/java/com/shangin/automationexercise/tests/ui/account/RegistrationTest.java#L63) | [Register](../src/test/resources/features/account/Register.feature#L20) / Register user with existing email |
| 6 / Contact submission | [ContactUsTest.shouldSendMessage](../src/test/java/com/shangin/automationexercise/tests/ui/pages/ContactUsTest.java#L22) | [ContactUs](../src/test/resources/features/pages/ContactUs.feature#L6) / Submit the Contact Us form successfully |
| 7 / Test Cases navigation | [TestCasesTest.shouldOpenTestCase](../src/test/java/com/shangin/automationexercise/tests/ui/pages/TestCasesTest.java#L18) | [TestCases](../src/test/resources/features/pages/TestCases.feature#L6) / Verify Test Cases Page |
| 8 / Catalog and details | [ProductsPageTest.shouldViewAllProductsAndProductDetails](../src/test/java/com/shangin/automationexercise/tests/ui/products/ProductsPageTest.java#L29) | [ProductsPage](../src/test/resources/features/products/ProductsPage.feature#L6) / Verify all products and product details |
| 9 / Search | [ProductsPageTest.shouldSearchProducts](../src/test/java/com/shangin/automationexercise/tests/ui/products/ProductsPageTest.java#L52) | [ProductsPage](../src/test/resources/features/products/ProductsPage.feature#L16) / Search Product (3 examples) |
| 10 / Home subscription | [FooterTest.shouldSubscribeFromHomePage](../src/test/java/com/shangin/automationexercise/tests/ui/pages/FooterTest.java#L21) | [Subscription](../src/test/resources/features/pages/Subscription.feature#L6) / Subscribe from the Home page |
| 11 / Cart subscription | [FooterTest.shouldSubscribeFromCartPage](../src/test/java/com/shangin/automationexercise/tests/ui/pages/FooterTest.java#L45) | [Subscription](../src/test/resources/features/pages/Subscription.feature#L16) / Subscribe from the Cart page |
| 12 / Add products | [ProductsPageTest.shouldAddProductsToCart](../src/test/java/com/shangin/automationexercise/tests/ui/products/ProductsPageTest.java#L75) | [ProductsPage](../src/test/resources/features/products/ProductsPage.feature#L32) / Add products to the cart (2 examples) |
| 13 / Quantity | [ProductsPageTest.shouldAddSpecificQuantityOfProduct](../src/test/java/com/shangin/automationexercise/tests/ui/products/ProductsPageTest.java#L94) | [ProductsPage](../src/test/resources/features/products/ProductsPage.feature#L48) / Verify Product quantity in Cart |
| 14 / Register during checkout | [PlaceOrderTest.shouldPlaceOrderRegisterWhileCheckout](../src/test/java/com/shangin/automationexercise/tests/ui/order/PlaceOrderTest.java#L41) | [PlaceOrder](../src/test/resources/features/order/PlaceOrder.feature#L6) / Place an order by registering during checkout |
| 15 / Register before checkout | [PlaceOrderTest.shouldPlaceOrderRegisterBeforeCheckout](../src/test/java/com/shangin/automationexercise/tests/ui/order/PlaceOrderTest.java#L98) | [PlaceOrder](../src/test/resources/features/order/PlaceOrder.feature#L31) / Place an order registering before checkout |
| 16 / Login before checkout | [PlaceOrderTest.shouldPlaceOrderLoginBeforeCheckout](../src/test/java/com/shangin/automationexercise/tests/ui/order/PlaceOrderTest.java#L158) | [PlaceOrder](../src/test/resources/features/order/PlaceOrder.feature#L55) / Place an order by logging in before checkout |
| 17 / Remove cart item | [CartTest.shouldRemoveProductFromCart](../src/test/java/com/shangin/automationexercise/tests/ui/cart/CartTest.java#L30) | [Cart](../src/test/resources/features/cart/Cart.feature#L6) / Remove a product from the cart |
| 18 / Categories | [CategoryBrandTest.shouldViewCategory](../src/test/java/com/shangin/automationexercise/tests/ui/products/CategoryBrandTest.java#L23) | [CategoryBrand](../src/test/resources/features/products/CategoryBrand.feature#L23) / View Category Products |
| 19 / Brands | [CategoryBrandTest.shouldViewBrand](../src/test/java/com/shangin/automationexercise/tests/ui/products/CategoryBrandTest.java#L51) | [CategoryBrand](../src/test/resources/features/products/CategoryBrand.feature#L32) / View and cart brand products |
| 20 / Cart persists after login | [CartTest.shouldSaveCartAfterLogin](../src/test/java/com/shangin/automationexercise/tests/ui/cart/CartTest.java#L70) | [Cart](../src/test/resources/features/cart/Cart.feature#L19) / Search products and verify cart after login |
| 21 / Review submission | [ProductsPageTest.shouldAddReviewOnProduct](../src/test/java/com/shangin/automationexercise/tests/ui/products/ProductsPageTest.java#L121) | [ProductsPage](../src/test/resources/features/products/ProductsPage.feature#L58) / Add a review to a product |
| 22 / Recommended product | [CartTest.shouldAddToCartFromRecommended](../src/test/java/com/shangin/automationexercise/tests/ui/cart/CartTest.java#L103) | [Cart](../src/test/resources/features/cart/Cart.feature#L36) / Add a recommended item to the cart |
| 23 / Checkout addresses | [PlaceOrderTest.shouldDisplayCorrectDeliveryAndBillingAddressesOnCheckoutPage](../src/test/java/com/shangin/automationexercise/tests/ui/order/PlaceOrderTest.java#L216) | [PlaceOrder](../src/test/resources/features/order/PlaceOrder.feature#L78) / Verify address details in checkout page |
| 24 / Invoice | [PlaceOrderTest.shouldDownloadInvoiceAfterPurchase](../src/test/java/com/shangin/automationexercise/tests/ui/order/PlaceOrderTest.java#L254) | [PlaceOrder](../src/test/resources/features/order/PlaceOrder.feature#L97) / Download Invoice after purchase order |
| 25 / Arrow scroll | [ScrollTest.shouldScrollUpUsingArrowButtonAndScrollDown](../src/test/java/com/shangin/automationexercise/tests/ui/pages/ScrollTest.java#L19) | [Scroll](../src/test/resources/features/pages/Scroll.feature#L6) / Verify Scroll Up using 'Arrow' button and Scroll Down functionality |
| 26 / Programmatic scroll | [ScrollTest.shouldScrollUpWithoutArrowButtonAndScrollDown](../src/test/java/com/shangin/automationexercise/tests/ui/pages/ScrollTest.java#L38) | [Scroll](../src/test/resources/features/pages/Scroll.feature#L16) / Verify Scroll Up without 'Arrow' button and Scroll Down functionality |

There are 26 TestNG UI methods and 37 Cucumber executions: 26 mapped website
scenario templates become 29 executions through search/cart Examples, plus one
custom quantity scenario (`@MTC_001`) and seven category examples (`@MTC_002`).
These counts exclude explicitly selected support/report-demo tests.

## UI coverage

All 26 published UI case IDs have both a TestNG test and a Cucumber scenario.
Home and cart subscriptions (10/11) verify the heading, submit a generated email
and assert the success message. Valid login (2) deletes the account through
the UI and checks its confirmation. Invoice (24) continues to the home page
after verifying download content, deletes the account through the UI and checks
its confirmation. Owned-account API cleanup remains a safety net for failures.

Validation on 2026-10-07, Java 17.0.20/headless Chrome 154: real Cucumber cases
2/10/11/24 passed 4/4 with no failures or skips, including UI deletion in both
account flows. Full BDD discovery passed 37/37 and verified all 26 `@TC_NN`
selectors; account-cleanup regression passed 10/10. Evidence is under
`target/coverage-gap-validation/`. This focused run does not replace a full
real regression or extend the platform evidence below.

Each website scenario has one matching unique `@TC_NN` tag; outline Examples
share their parent tag. Custom examples retain `@MTC_001`/`@MTC_002`.

Coverage boundaries and intentional differences:

- Case 19 checks brand navigation/products, matching the site's listed steps;
  despite its title, those steps do not request adding a branded item to the cart.
- Product choices and example counts differ between styles. Tests cover the
  published UI intents, rather than every catalog/input combination or exact
  step-for-step parity. Accessibility, visual, security and load audits are
  separate test directions outside this UI coverage map.

## API case map

All 14 published API intents have TestNG methods. No API-only Cucumber features
are provided; BDD uses API clients for setup and data checks. All API tests are
under `src/test/java/com/shangin/automationexercise/tests/api`.

| Website API ID | Class | Method |
| --- | --- | --- |
| 1 | [ProductsApiTest](../src/test/java/com/shangin/automationexercise/tests/api/ProductsApiTest.java) | `shouldReturnProductsList` |
| 2 | ProductsApiTest | `shouldRejectPostRequestToProductsList` |
| 3 | [BrandsApiTest](../src/test/java/com/shangin/automationexercise/tests/api/BrandsApiTest.java) | `shouldReturnBrandList` |
| 4 | BrandsApiTest | `shouldRejectPutRequestToBrandsList` |
| 5 | ProductsApiTest | `shouldSearchProducts` |
| 6 | ProductsApiTest | `shouldRejectSearchWithoutSearchProductParameter` |
| 7 | [AccountApiTest](../src/test/java/com/shangin/automationexercise/tests/api/AccountApiTest.java) | `shouldVerifyLoginWithValidEmailAndPassword` |
| 8 | AccountApiTest | `shouldVerifyLoginOnlyWithPassword` |
| 9 | AccountApiTest | `shouldRejectDeleteRequestToVerifyLogin` |
| 10 | AccountApiTest | `shouldRejectLoginWithInvalidDetails` |
| 11 | AccountApiTest | `shouldCreateNewUserAccount` |
| 12 | AccountApiTest | `shouldDeleteExistingUserAccount` |
| 13 | AccountApiTest | `shouldUpdateExistingUserAccount` |
| 14 | AccountApiTest | `shouldGetUserAccountDetailByEmail` |

## External service and browser limits

These are tests against an independently hosted practice site. Full suites
create accounts, submit contact/review data and place synthetic orders; only
owned accounts have automated cleanup. Network outages, Cloudflare 520, queue
limits, changing catalog content and site markup can fail a correct test.
No automatic retry hides these failures. Seed replay controls generated choices,
not server state. Smoke covers only API catalog read and Test Cases navigation.

Advertisement handling changes the page: Chromium request blocking, selected
DOM removal/annotation restoration and a single vignette navigation retry.
Firefox lacks equivalent request blocking in this implementation. It is neither
complete ad removal nor a check of normal advertising behavior. Disable it with
`-Dads.handling.enabled=false` for an unmodified-ad-handling run; see
[interaction waits](RUN_GUIDE.md#page-readiness-and-interaction-waits).

Chrome, Edge and Firefox share launch settings but can have different viewport
heights and download/security behavior. macOS Firefox may require the documented
LaunchServices wrapper. Recorded acceptance covers Java 17/macOS, with real
configuration/download probes across modes; full Firefox regression retained
failures and Windows/Linux execution is unverified. Exact dated results are in
[the browser comparison](RUN_GUIDE.md#compare-complete-browser-runs), not a
promise that every future browser/site combination will pass.
