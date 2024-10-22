# Get Notifications

## 1. Primary actor and goals

__System__: It notifies users of certain food items that are available on a specific day based on the user's preferences 
(e.g. likes, dislikes, vegetarian food, vegan, halal, etc.). It recommends food across all the dining options at Vassar 
(e.g. Gordon Commons, Retreat, Street eats, etc.), and it recommends both food for the current day and for future days 
(up to one week from the current day).

## 2. Other stakeholders and their goals

* __User (students, staff)__: They are notified of the daily recommendations made by the system. They can choose to turn
on/off their notifications. 

## 3. Preconditions

* Browse use case works
* Users can like/dislike food items
* Users can select their food preference
* Authentication works

## 4. Postconditions

* When browsing through the food options, recommended foods are denoted by a ***star*** feature.
* Users get notified of the notifications on their phones if they turned on their notifications.

## 5. Workflow

```plantuml
@startuml

skin rose

title Notification (casual level)

'define the lanes
|#technology|User|
|#application|System|

start
|System|
:Uses users preferences to make recommendations;
if (Notification?) is  ( On ) then
:Notifies users of food recommendations for the day;
else ( Off ) 
endif

|User|
if (Notification?) is  ( On ) then
:Views notification;
else ( Off ) 
endif

if (Browse?) is ( yes ) then
:Execute __browse__;
:Sees the **star** feature on recommended food items;
else ( No )
endif

|System|
if (End of day?) is  ( Yes ) then
:Update recommendations for the next day;
else ( No ) 
:Keep recommendations;
endif

stop
@enduml
```