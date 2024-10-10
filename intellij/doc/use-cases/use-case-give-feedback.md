# Feedback

## 1. Primary actor and goals

__Vassar students and staff__: wants to give feedback to vassar dining admin in order to see potential changes

## 2. Other stakeholders and their goals

__Vassar dining administration__: wants to view vassar students' and staffs' feedback on food items

__Visitors__: wants to see what feedback vassar students and staff have of dining options which may help them make more 
informed decisions about dining

## 3. Preconditions

* The app is able to access dining options from the dining website *https://vassar.cafebonappetit.com/*.
* Users are able to browse the menu options.

## 4. Postconditions

* Users and administrators are identified and authenticated.
* Authentication is stored
* Feedback is saved


## 4. Workflow

```plantuml
@startuml

skin rose

title feedback (casual level)

'define the lanes

|#technology|User|
|#implementation|System|
|#application|Dining admin|
|#business|Visitor|


|User|

start

:Execute __browse__;
:Gives comments on food items;
:Goes to the feedback section;
:Gives feedback;
:Submit feedback;

|System|

while (Authentication?) is (False)
  :Authenticate the user;
endwhile (True)
:Feedback saved;
    
|Dining admin|

:Views feedback;
:Responds to feedback;

|User| 
:Views feedback;

|Visitor|
:Views feedback;

|System|

stop
@enduml
```


