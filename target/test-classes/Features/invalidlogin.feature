Feature: Login Functionality



  @invalid_login
  Scenario Outline: Login with invalid credentials
    Given user is on the login page
    When user enters email "<email>" and password "<password>"
    And user clicks on the login button
    Then user in  login page

    Examples:
      | email                  | password  |
      | invalid@example.com    | wrongness |
      | valid@example.com      | wrongness |
      | invalid@example.com    | validate |