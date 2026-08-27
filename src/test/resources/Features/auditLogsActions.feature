Feature: Audit Logs Actions


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user click on the login button
    Then user is navigated to the home page

  Scenario: Validate All Actions of Created
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Created
    Then user validate Created

  Scenario: Validate All Actions of Updated
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Updated
    Then user validate Updated

  Scenario: Validate All Actions of Deleted
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Deleted
    Then user validate Deleted

  Scenario: Validate All Actions of Blocked
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Blocked
    Then user validate Blocked

  Scenario: Validate All Actions of Unblocked
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Unblocked
    Then user validate Unblocked

  Scenario: Validate All Actions of Approved
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Approved
    Then user validate Approved

  Scenario: Validate All Actions of Rejected
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Rejected
    Then user validate Rejected

  Scenario: Validate All Actions of Closed
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Closed
    Then user validate Closed

  Scenario: Validate All Actions of Invited
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Invited
    Then user validate Invited

  Scenario: Validate All Actions of Enabled
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Enabled
    Then user validate Enabled

  Scenario: Validate All Actions of Expired
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on All Actions
    And user click on Expired
    Then user validate Expired

  Scenario: Validate Search of Admin Name
    When user click on Monitoring menu
    And user click on Audit Logs
    And user click on Search
    Then user enter "Company Users"
