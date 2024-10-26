# Browse

## 1. Primary actor and goals

__Vassar students, staff (including dining administrators), and visitors__: wants to browse through the dining menu 
quickly and see information clearly in order to help them make decisions faster


## 2. Other stakeholders and their goals
__System__: displays dining menu for users to browse and enables users to perform further actions on the app, such as 
like/dislike food items, give comments, read articles, etc.

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
* The default menu will be the current day's menu, but users can select the menu of future dates 
* The default dining center that will be shown on the app will be the Gordon Commons, but their will be a section where
users can check the menu of other dining centers, such as Retreat, The Brew, Express, and Street Eats. 
* There will also be a section where people can read food and wellness articles
* There will be a section for giving feedback to the dining services as well as links to connect with them on social 
media
* Students and staff members should be able to like or dislike menu option and give feedback.

## 4. Workflow

```plantuml
@startuml

skin rose

title browse (casual level)

'define the lanes

|#technology|User|

|User|
start
while (browsing?) is (yes) 
  :finds menu options for a specific day;
  
  switch (click/browse) 
      case (filter)
          :Execute __preference__;
      case (location)
          :chooses dining location;
      case (day)
          :chooses day;
      case (food items)
          switch (view food item)
              case (like-dislike)
                  :Execute __like-dislike__;
              case (comment)
                  :Execute __comment__;
              case (give-feedback)
                  :Execute __give-feedback__;
          endswitch
      case (articles)
          :read articles;
  endswitch
endwhile (no)

stop
@enduml
```

## Sequence Diagram

```plantuml
Sequence diagrams: messages
@startuml
hide footbox

participant "User" as user
participant ": UI" as ui
participant ": Menu" as menu
participant ": Request" as request
participant ": Request parser" as request_parser
participant ": Food Item" as food_item
participant ": Dining Section" as dining_section
participant ": Meal Type Section" as meal_type_section
participant ": Meal Type" as meal_type







ui -> user : display menu
menu -> ui : updateMenuDisplay()
menu -> request : getRequest()
request -> request_parser : parseRequest()
loop i in 0..request_parser.size-1
    request_parser -> food_item : create()
end
food_item -> dining_section : addFood()
dining_section -> meal_type_section : addSection()
meal_type_section  -> meal_type : ???

@enduml
```


