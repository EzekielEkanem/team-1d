# Browse

## 1. Primary actor and goals

__Vassar students, staff (including dining administrators), and visitors__: wants to browse through the dining menu 
quickly and see information clearly in order to help them make decisions faster


## 2. Other stakeholders and their goals
None

## 3. Preconditions

* The app is able to access dining options from the dining website *https://vassar.cafebonappetit.com/*.

## 4. Postconditions

* Dining menu is shown clearly with food items' information in a consistent format
* The following are the food items' information that will be shown:
  * A photo of each food item
  * The name of each food item
  * Like and dislike buttons for each food item
  * Comment section for each food item
  * If the food is part of the recommended section, a ***star*** feature will appear close to the food's name
* There will be a section to filter food via dietary preferences (e.g. Vegetarian, Vegan, etc) or via recommended food
choices
* While on the app, swiping to the left shows the previous day's menu while swiping to the right shows the following day's
menu
* The default dining center that will be shown on the app will be the Gordon Commons, but their will be a section where
users can check the menu of other dining centers, such as Retreat, The Brew, Express, and Street Eats. 
* There will also be a section where people can view upcoming events organized by the dining service as well as a section 
that will display food and wellness articles
* There will be a section for giving feedback to the dining services as well as links to connect with them on social 
media
* Students and staff members should be able to like or dislike menu option, give/respond to feedback, and get recommendations.

## 4. Workflow

```plantuml
@startuml

skin rose

title Browse (casual level)

'define the lanes

|#technology|User|

|User|
start
:finds menu options for a specific day;

switch (action?) 
    case (filter)
        :;
    case (swipe)
    case (user)
if (action?) is (filter) then
:filters food according to dietary choices/recommendations;;
else if (action?) is ( swipe ) then
  if (swipe?) is  ( left ) then
  :views previous day's food menu;
  else ( right ) 
  :views the food menu for the following day;
  endif
else (user?)
if (user?) is (students and staff) then
:likes/dislikes food item;
:comments on a food item;
:gives feedback to dining administrators;
else (visitor)
endif

:views food preferences, comments, and feedback;
:selects other dining centers (e.g. Retreat, Street Eats etc.);


stop
@enduml
```


