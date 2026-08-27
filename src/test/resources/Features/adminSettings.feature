Feature: Admin Settings


  Background:
    Given user is on the login page
    When user enters email "viadmin@gmail.com" and password "Virtualintros@30026"
    And user clicks on the login button
    Then user is navigated to the home page

    Scenario: Validate Enable Free Subscription
     Given user click on Admin Settings
      When user validate Enable Free Subscription
      Then user validate toggle button of Enable Free Subscription

  Scenario: Validate Enable Automated Test User Creation
    Given user click on Admin Settings
    When user validate Automated Test User Creation
    Then user validate toggle button of Automated Test User Creation

  Scenario: Validate Enable Seniority Stars
    Given user click on Admin Settings
    When user validate Enable Seniority Stars
    Then user validate toggle button of Enable Seniority Stars

  Scenario: Validate Enable White Label Branding
    Given user click on Admin Settings
    When user validate Enable White Label Branding
    Then user validate toggle button of Enable White Label Branding

  Scenario: Validate Cross Path Cool Down Period
    Given user click on Admin Settings
    When user validate Cross Path Cool Down Period
    And user enter value
    Then user validate save button of Cross Path Cool Down Period

  Scenario: Validate Splash Screen Experience
    Given user click on Admin Settings
    When user validate Splash Screen Experience
    Then user validate Splash Screen dropdown

  Scenario: Validate Splash Screen Display Duration
    Given user click on Admin Settings
    When user validate Splash Screen Display Duration
    And user enter duration
    Then user validate save button of Splash Screen Display Duration
