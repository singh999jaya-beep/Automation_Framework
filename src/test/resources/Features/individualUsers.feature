Feature: Individual Users Search,Status and


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

  Scenario: Validate Individual Users menu
    When user clicks on Individual Users menu
    Then user validate Individual Users title

  Scenario: Validate Active Status
    When user clicks on Individual Users menu
    And user click on status
    And user click on active status
    And user scroll towards right
    Then user validate Active

  Scenario: Validate Inactive Status
    When user clicks on Individual Users menu
    And user click on status
    And user click on inactive status
    And user scroll towards right
    Then user validate Inactive

  Scenario: User delete individual users
    When user clicks on Individual Users menu
    And user scroll towards right
    And user click on delete
    And user enter text "Delete it" in reason
    Then user click on confirm Delete button

