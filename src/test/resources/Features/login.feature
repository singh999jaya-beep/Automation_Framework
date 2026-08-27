Feature: Login Functionality

  @valid_login
  Scenario Outline: Login with valid credentials
    Given user is on the login page
    When user enters email "<email>" and password "<password>"
    And user clicks on the login button
    Then user is navigated to the home page

    Examples:
      | email             | password            |
      | viadmin@gmail.com | Virtualintros@30026 |


  @invalid_login
  Scenario Outline: Login with invalid credentials
    Given user is on the login page
    When user enters email "<email>" and password "<password>"
    And user clicks on the login button
    Then user remains in login page

    Examples:
      | email         | password |
      | vvk@gmail.com | VI@30026 |
