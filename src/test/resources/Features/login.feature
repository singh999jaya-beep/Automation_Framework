Feature: Login Functionality

  @login
  Scenario Outline: Login with different credentials
    Given user is on the login page
    When user enters email "<email>" and password "<password>"
    And user clicks on the login button
    Then user is navigated to the home page

    Examples:
      | email                             | password |
      | helloteamdemo+techahead@gmail.com | H3770T34M|


  @valid_login
  Scenario Outline: Login with valid credentials
    Given user is on the login page
    When user enters email "<email>" and password "<password>"
    And user clicks on the login button
    Then user is navigated to the home page
    Examples:
      | email                             |  password           |
      | helloteamdemo+techahead@gmail.com |  H3770T34M          |
