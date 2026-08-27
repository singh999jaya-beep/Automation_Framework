Feature: Manage Events Search


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page


  Scenario: Validate search bar using name
    When user clicks on Events
    And user click on search bar
    And user enter name "dnyajh"
    Then user validate name or No data available

  Scenario: Validate search bar using eventID
    When user clicks on Events
    And user click on search bar
    And user enter eventid "180"
    Then user validate ID or No data available

  Scenario: Validate delete of Events
    When user clicks on Events
    And user click on Delete
    Then user click on  confirm Delete Button
    And delete lookup detail API should return status 200

  Scenario: Validate block of Events
    When user clicks on Events
    And user click on Block
    Then user click on  confirm Block Button
    And block lookup detail API should return status 200