Feature: Athletes Functionality


  Background:
    Given user is on the login page
    When user enters email "helloteamdemo+techahead@gmail.com" and password "H3770T34M"
    And user clicks on the login button
    Then user is navigated to the home page
    And user goes to Athletes



  Scenario: Add a new Athlete
    When user clicks on Add Athletes
    And user enter first name "Jack"
    And user enter last name "Louis"
    And user click on calendar and select date
    And user click on gender dropdown and select it
    And user enter email "jack121@gmail.com"
    And user enter mobile number "(667) 543-2113"
    And user scroll down household tab
    And user click on Add Household
    And user enter household email "jane@example.com" and household relationaship "Parent"
    And user enter firstname "Henry" , lastname "Doe" and phone no. "5551234" of household
    And user clicks on Continue
    Then the athlete is added successfully


  Scenario: Edit the details of new Athlete added

    When user click on edit button of athlete
    And user edit name of the athlete "Jack edited"
    And user scroll down  athlete tab
    And user edit email of the athlete "jackedited@gmail.com"
    And user click on submit button
    Then the edit athlete saved successfully






