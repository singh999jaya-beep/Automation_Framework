Feature: Manage Admin Users


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page


  Scenario: Validate invite of Admin
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on Invite Admin
    And user enter email address "dk11@yopmail.com"
    And user enter full name "Ajay"
    And user click on Assign Role
    And user select Rahul
    And user click on Permanent
    Then user click on Send Invitation

  Scenario: Validate Active status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Status
    And  user select Active
    Then user validate Active Status

  Scenario: Validate Blocked status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Status
    And  user select Blocked
    Then user validate Blocked Status

  Scenario: Validate Pending status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Status
    And  user select Pending
    Then user validate Pending Status

  Scenario: Validate Deleted status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Status
    And  user select Deleted
    Then user validate Deleted Status

  Scenario: Validate Expired status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Status
    And  user select Expired
    Then user validate Expired Status

  Scenario: Validate Permanent status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Access
    And  user select Permanent
    Then user validate Permanent Status

  Scenario: Validate Temporary status
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on All Access
    And  user select Temporary
    Then user validate Temporary Status

  Scenario: Validate Search
    When user click on User & Access Management
    And user click on Manage Admin Users
    And user click on Search
    And  user Search user "VI-248"
    Then user validate Search





















