Feature: Audit Logs


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user click on the login button
    Then user is navigated to the home page

  Scenario: Validate Audit Logs menu
    When user click on Monitoring menu
    And user click on Audit Logs
    Then user validate Audit Logs

    Scenario: Validate Export Logs
      When user click on Monitoring menu
      And user click on Audit Logs
      Then user click on Export Logs

  Scenario: Validate All Modules of Individual Users
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Individual Users
    Then user validate Individual Users

  Scenario: Validate All Modules of Company Users
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Company Users
    Then user validate Company Users

  Scenario: Validate All Modules of Input Categories
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Input Categories
    Then user validate Input Categories

  Scenario: Validate All Modules of Events
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Events
    Then user validate Events


  Scenario: Validate All Modules of Advertisement
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Advertisement
    Then user validate Advertisement

  Scenario: Validate All Modules of Transactions
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Transactions
    Then user validate Transactions

  Scenario: Validate All Modules of Moderation Center
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Moderation Center
    Then user validate Moderation Center

  Scenario: Validate All Modules of Admin Settings
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Admin Settings
    Then user validate Admin Settings

  Scenario: Validate All Modules of Manage Roles
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Manage Roles
    Then user validate Manage Roles

  Scenario: Validate All Modules of Manage Admin Users
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Manage Admin Users
    Then user validate Manage Admin Users

  Scenario: Validate All Modules of Deeplink History
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Deeplink History
    Then user validate Deeplink History

  Scenario: Validate All Modules of Consent Audit Log
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Consent Audit Log
    Then user validate Consent Audit Log

  Scenario: Validate All Modules of Content Versions
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Content Versions
    Then user validate Content Versions

  Scenario: Validate All Modules of Audit Logs
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Audit Logs
    Then user validate Audit Logs

  Scenario: Validate All Modules of Deleted Users
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Modules
    And user click on Deleted Users
    Then user validate Deleted Users

