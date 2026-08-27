Feature: Transactions

  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page


  @transactions
  Scenario: Validate Monthly Subscription Status
    When user clicks on Transactions
    And user click on All
    And user click on Monthly Subscription status
    And user scroll towards right
    Then user validate Monthly Subscription

  @transactions
  Scenario: Validate Event Invitation Status
    When user clicks on Transactions
    And user click on All
    And user click on Event Invitation status
    And user scroll towards right
    Then user validate Event Invitation

  @transactions
  Scenario: Validate Advertisement Status
    When user clicks on Transactions
    And user click on All
    And user click on Advertisement status
    And user scroll towards right
    Then user validate Advertisement

  @transactions
  Scenario: Validate search bar using email id
    When user clicks on Transactions
    And user click on search bar
    And user enter email "jayas1@yopmail.com"
    Then user validate mail id or No data available

  @transactions
  Scenario: Validate search bar using name
    When user clicks on Transactions
    And user click on search bar
    And user enter name "dnyajh"
    Then user validate name or No data available
