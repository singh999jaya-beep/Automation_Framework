Feature: Company Users Search,Status and


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

  Scenario: Validate Company Users menu
    When user clicks on Company Users menu
    Then user validate Company Users title

  Scenario: Validate Active Status
    When user clicks on Company Users menu
    And user click on status
    And user click on active status
    And user scroll towards right
    Then user validate Active

  Scenario: Validate Inactive Status
    When user clicks on Company Users menu
    And user click on status
    And user click on inactive status
    And user scroll towards right
    Then user validate Inactive

  Scenario: User delete company users
    When user clicks on Company Users menu
    And user scroll towards right
    And user click on delete
    And user enter text "Delete it" in reason
    Then user click on confirm Delete button


  Scenario: User block company users
    When user click on Company users menu
    And user scroll towards right
    And user click on block
    And user enter text "Block it" in reason
    Then user click on confirm Block button






