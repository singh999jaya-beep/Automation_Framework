Feature: Consent Audit Log

  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

  Scenario: Validate Export CSV of Consent Audit Log
    When user clicks on Consent Audit Log menu
    And user click on Export CSV
    Then user validate Exported CSV

  Scenario: Validate user select Virtual Intros of All Apps
    When user clicks on Consent Audit Log menu
    And user click on All Apps
    And user select Virtual Intros
    And user scroll towards right
    Then user validate Virtual Intros

  Scenario: Validate user select Privacy Policy of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Privacy Policy
    And user scroll towards right
    Then user validate Privacy Policy

  Scenario: Validate user select Terms of Service of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Terms of Service
    And user scroll towards right
    Then user validate Terms of Service

  Scenario: Validate user select Marketing Communication of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Marketing Communication
    And user scroll towards right
    Then user validate Marketing Communication

  Scenario: Validate user select Customise Recommendation of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Customise Recommendation
    And user scroll towards right
    Then user validate Customise Recommendation

  Scenario: Validate user select Notification of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Notification
    And user scroll towards right
    Then user validate Notification

  Scenario: Validate user select Location of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Location
    And user scroll towards right
    Then user validate Location

  Scenario: Validate user select Gender of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Gender
    And user scroll towards right
    Then user validate Gender

  Scenario: Validate user select Ethnicity of All Categories
    When user clicks on Consent Audit Log menu
    And user click on All Categories
    And user select Ethnicity
    And user scroll towards right
    Then user validate Ethnicity

  Scenario: Validate user select Pending of All Actions
    When user clicks on Consent Audit Log menu
    And user click on All Actions
    And user select Pending
    And user scroll towards right
    Then user validate Pending

  Scenario: Validate user select Granted of All Actions
    When user clicks on Consent Audit Log menu
    And user click on All Actions
    And user select Granted
    And user scroll towards right
    Then user validate Granted

  Scenario: Validate user select Withdrawn of All Actions
    When user clicks on Consent Audit Log menu
    And user click on All Actions
    And user select Withdrawn
    And user scroll towards right
    Then user validate Withdrawn

  Scenario: Validate user search id
    When user clicks on Consent Audit Log menu
    And user click on Search
    Then user search id "VI-10655"
