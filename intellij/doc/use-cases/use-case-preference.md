# Preference

## 1. Primary actor and goals

__Users (students, staff) and Visitors__: They want to be able to filter menu items based on their dietary preference, 
such as, vegetarian, vegan, made without gluten-containing ingredients, in balance, halal. They also want to filter menu 
items based on their 'liked' preferences.

## 2. Other stakeholders and their goals

* __System__: It ensures that only food items belonging to the filtered class are shown as the menu options

## 3. Preconditions

* The app is able to access dining options from the dining website *https://vassar.cafebonappetit.com/*.
* Users are able to browse the menu options.
* If authenticated, users are able to like/dislike food items

## 4. Postconditions

* The menu options are updated so that only food items belonging to the filtered class is shown on the browse menu
* If a previously selected class becomes unselected, the system updates the menu options accordingly

## 5. Workflow

```plantuml
@startuml

skin rose

title Manage Preferences (casual level)

'define the lanes
|#application|User|
|#implementation|System|


|User|

start  

    if (choose preference?) is (Yes) then
        :goes to preference section;
        :selects/unselects one or more preferences;
        
        |System|
        :Saves the preference;
        :Updates browse menu to only display food items from selected class;
        
    else (No)
    endif  
    
    |User|
    :views updated food menu;

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
participant "Controller" as controller
participant ": Menu" as menu
participant ": Request" as request
participant ": Preference" as preference
participant ": MealType" as meal_t
participant ": Meal Type Section" as meal_t_s
participant ": Dining Section" as d_s
participant ": Food Item" as food_item

user -> ui : Clicks preferences
ui -> controller : return preferences
controller -> menu : updatePreferences(preferences)
menu -> preference : updatePreference(preferences)
controller -> ui : updateMenu()
ui -> user : Display menu


@enduml
```