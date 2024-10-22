# Authenticate

## 1. Primary actor and goals
__System__: It authenticates a user to ensure that they are Vassar students or staff so that they can successfully 
like/dislike food items, give feedback, or get food notifications from the app.

## 2. Other stakeholders and their goals
__Vassar staff and students__: wants to get authenticated so that they can like/dislike a food item, give feedback, and
get notifications from the app

## 3. Preconditions
* The browse use case works

## 4. Postconditions
* Authentication is saved
* Users are able to get notified of food recommended by the app
* Users are able to successfully like/dislike a food item and give feedback to dining administrators


## 4. Workflow

```plantuml
@startuml

skin rose

title authenticate (casual level)

'define the lanes

|#technology|User|
|#implementation|System|


|User|

start

while (browse?) is (yes) 
    :Execute __Browse__;
    switch (action?)
        case (like-dislike)
            :cliks on like/dislike  button;
        case (give-feedback)
            :navigates to feedback section;
        case (get-notifications)
            :turns on notifications;
    endswitch 
    
    |System|
    while (authenticated?) is (no)
        :Requests users name and email;
        |User|
        :User inputs name and email;
        |System|
        :verifies email;
    endwhile(yes)
    :Execute __action__;
    

|User|
:views action;
endwhile(no)

stop
@enduml
```


