Feature: Deleted App Users


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user click on the login button
    Then user is navigated to the home page

  Scenario: Validate Recover Action
    When user click on Deleted App Users
    And user click on All
    And user click on Deactivated
    And user scroll towards right
    Then user click on Recover

  Scenario: Validate Search Bar
    When user click on Deleted App Users
    And user click on search
    And user enter ID "VI-11630"
    Then user validate userID



  Scenario: Validate User Type of Individual Dropdown
    When user click on Deleted App Users
    And user click on All
    And user click on Individual
    Then user validate User Type

  Scenario: Validate User Type of Company Dropdown
    When user click on Deleted App Users
    And user click on All
    And user click on Company
    Then user validate User Type

  Scenario: Validate status of Deactivated
    When user click on Deleted App Users
    And user click on All
    And user click on Deactivated
    Then user validate Status

  Scenario: Validate status of Deleted
    When user click on Deleted App Users
    And user click on All
    And user click on Deleted
    Then user validate Status

  Scenario: Validate status of Recovered
    When user click on Deleted App Users
    And user click on All
    And user click on Recovered
    Then user validate Status


