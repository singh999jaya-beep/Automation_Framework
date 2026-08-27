Feature: ForgotPassword Functionality

  @PasswordReset
  Scenario: PasswordReset Functionality
    Given user is on the login page
    When user click on password reset
    And user enter registered email address "viadmin@gmail.com"
    And user click on confirm button
    Then user sees password reset confirmation message
