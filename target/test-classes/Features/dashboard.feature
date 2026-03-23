Feature:Dashboard Functionality;

Background:
 Given user is on the login page
 When user enters email "helloteamdemo+techahead@gmail.com" and password "H3770T34M"
 And user clicks on the login button
 Then user is navigated to the home page



Scenario:Testing a post with text on team feed

 When user navigates to the Dashboard menu
 And user writes a post on the Team Feed "Hello Team, this is a test post"
 And user scrolls to Post to Team Feed button
 And user clicks on Post to Team Feed button
 Then post should be published successfully

 Scenario: Testing a post with text on team feed

  When user navigates to the Dashboard menu
  And user writes a post on the Team Feed "Hello Team, this is a test post"
  And user scrolls to Post to Team Feed button
  And user clicks on Post to Team Feed button
  Then post should be published successfully

 Scenario:Testing a post with link on team feed
 
 When user navigates to the Dashboard menu
  And user writes a post on the Team Feed "Hello Team,post with link"
  And user click on link and enter URL "https://pre-prod-app.safeteam.io/panel/groups"
  And user scrolls to Post to Team Feed button
  And user clicks on Post to Team Feed button
  Then post should be published successfully
  
  Scenario:Testing a post with img on team feed
 
 When user navigates to the Dashboard menu
  And user writes a post on the Team Feed "Hello Team, post with img"
  And user click on img and upload img from path "C:\Users\Jaya\Downloads\Screenshot 2025-12-18 113630.png"
   And user scrolls to Post to Team Feed button
  And user clicks on Post to Team Feed button
  Then post should be published successfully
  
  
  Scenario:Testing a post with video on team feed
 
 When user navigates to the Dashboard menu
  And user writes a post on the Team Feed "Hello Team, post with video"
  And user click on video and upload img from path "C:\Users\Jaya\Downloads\3192198-uhd_3840_2160_25fps.mp4"
   And user scrolls to Post to Team Feed button
  And user clicks on Post to Team Feed button
  Then post should be published successfully
  
   Scenario:Testing a edit of latest post   
 
 When user navigates to the Dashboard menu
    And user scrolls to Post to Team Feed button
  And user click on three dots of the post
  And user clicks on Edit option
  And user edits the post with text "Hello Team, edited post"
  And user scroll Save Changes and clicks on Save Changes button
  Then post should be published successfully
  
  Scenario:Testing a delete of latest post   
 
 When user navigates to the Dashboard menu
   And user scrolls to Post to Team Feed button
  And user click on three dots of the post
  And user clicks on Delete option
  Then post should be deleted successfully
  
  
  Scenario:Testing a comment of latest post   
 
 When user navigates to the Dashboard menu
   And user scrolls to Post to Team Feed button
  And user click on Comments of the post
  And user clicks on Be the first one to comment
  And user enter comment "This is a test comment"
  And user clicks on Post Comment button
  Then comment should be published successfully
  
  
  Scenario: Testing a like of latest post
  
 When user navigates to the Dashboard menu
   And user scrolls to Post to Team Feed button
  And user click on Likes icon of the post
  Then post should be liked successfully
  
  Scenario: Testing a dislike of latest post
  
 When user navigates to the Dashboard menu
   And user scrolls to Post to Team Feed button
  And user click on alreadyLiked icon of the post
  Then post should be dislike successfully
  
 Scenario: When user create event from dashboard
 
 When user navigates to the Dashboard menu
 And user click on plus icon and select Add Event option
 And user enter event name "Team Meeting"
  And user scroll current event tab
 And user enter event location "Conference Room"
 And user enter event address "San Francisco CA"
 And user click on Save button
 Then event should be created successfully
 
 Scenario: When user delete a event from dashboard
 
 When user navigates to the Dashboard menu
 And user click on edit button of the event
 And user click on Delete button
 Then event should be deleted successfully
 
 
 Scenario: When user add announcement from dashboard
 
 When user navigates to the Dashboard menu
 And user click on plus icon and select Add Announcement option
 And user enter announcement title "Office Renovation"
  And user click on add announcement button
 Then announcement should be created successfully
 
 Scenario: When user delete announcement from dashboard 
 
 When user navigates to the Dashboard menu
 And user click on announcement tab
 And user click on delete button of the announcement
 Then announcement should be deleted successfully
 
 Scenario: When user add game in calendar

 When user navigates to the Dashboard menu
 And user click on plus icon and select Add Game option
 And user enter opponent name "Rival Team"
 And user scroll current event tab
 And user enter location "New York"
 And user enter address "New York, NY"
 And user click on Save button
 Then game should be added successfully
 
 
 Scenario: When user delete game from calendar
  
 When user navigates to the Dashboard menu
 And user click on  game tab
 And user click on delete button of game tab
 Then game should be deleted successfully
 
 Scenario: When user export from calendar  

When user navigates to the Dashboard menu
And user click on plus icon and select Add click on Export Calendar
Then calendar should be exported successfully


 
 
 
 
 
 
 
 
 
 
 
 

    
  
  
  
  
  
 


 
