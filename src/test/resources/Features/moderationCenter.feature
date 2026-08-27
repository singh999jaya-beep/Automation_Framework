Feature: Moderation Center


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

  @moderation
  Scenario: Validate Pending Status
    When user clicks on Moderation Center
    And user click on status
    And user click on Pending status
    Then user validate Pending

  @moderation
  Scenario: Validate Report Dismissed Status
    When user clicks on Moderation Center
    And user click on status
    And user click on Report Dismissed
    Then user validate Report Dismissed

  @moderation
  Scenario: Validate Blocked Status
    When user clicks on Moderation  Center
    And user click on status
    And user click on Blocked
    Then user validate Blocked

  @moderation
  Scenario: Validate search bar using name
    When user clicks on Moderation  Center
    And user click on search bar
    And user enter name "dnyajh"
    Then user validate name or No data available

  @moderation
  Scenario: Validate search bar using mail
    When user clicks on Moderation  Center
    And user click on search bar
    And user enter name "techno@yopmail.com"
    Then user validate email or No data available

  @moderation
  Scenario: Validate Dismiss Report Action of Moderation Center
    When user clicks on Moderation Center
    And user click on status
    And user click on Pending status
    And user scroll towards right
    And user click on View
    And user click on Take Action
    And user click on Dismiss Report
    Then user click on Confirm Button

  @moderation
  Scenario: Validate Block reported Event of Moderation Center
    When user clicks on Moderation Center
    And user click on status
    And user click on Pending status
    And user scroll towards right
    And user click on View
    And user click on Take Action
    And user click on Block reported event
    Then user click on Confirm Button

