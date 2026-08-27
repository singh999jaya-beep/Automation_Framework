Feature: Manage Roles


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

  Scenario: User Create New  Role
    When user click on User & Access Management
    And user click on Manage Roles & Permissions
    And user enter role name "Rohit"
    And user enter description "Admin Panel"
    And user click on View Listing of Individual Users
    And user scroll down
    And user click on View Listing of Company Users
    And user click on Add of Input Categories
    And user click on Delete of Events
    And user click on View Listing of Advertisement
    And user click on View Listing of Transactions
    Then user click on Create Role


  Scenario: Search role of user
      When user click on User & Access Management
      And user click on Manage Roles & Permissions
      And user click on search role
      Then user enter name "QA"

  Scenario: View of Manage Roles and Permission
    When user click on User & Access Management
    And user click on Manage Roles & Permissions
    And user click on view
    Then user click on Close

  Scenario: Users of Manage Roles and Permission
    When user click on User & Access Management
    And user click on Manage Roles & Permissions
    And user click on users
    Then user click on Close

  Scenario: Delete of Manage Roles and Permission
    When user click on User & Access Management
    And user click on Manage Roles & Permissions
    And user click on delete
    Then user click on Delete Role













