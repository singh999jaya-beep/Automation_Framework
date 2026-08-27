Feature: Advertisement


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page


  @advertisements
  Scenario: Create a ads and publish it
    When user clicks on Advertisements
    And user enter campaign name "Job search bootcamp"
    And user enter URL "https://dq2embcxfli7y.cloudfront.net/"
    And user enter title "Virtual Drive"
    And user scroll down the page
    And user enter Ad description "It is one of best platform for recruitment and job search"
    And user scroll down the page
    And user click on upload here with "src/test/resources/testdata/carrer.jpg"
    Then user click on publish button
