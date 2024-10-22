# Comment

## 1. Primary actor and goals

__Vassar students, staff and visitors__: wants to comment on food items in the menu

## 2. Other stakeholders and their goals

__System__: wants to save comments so that all users can view the comments on the app

## 3. Preconditions

* The browse use case works

## 4. Postconditions

* Comment is saved
* Comment is visible across all devices


## 4. Workflow

```plantuml
@startuml

skin rose

title comment (casual level)

'define the lanes

|#technology|User|
|#implementation|System|


|User|

start

while (browse?) is (yes) 
    :Execute __Browse__;
    if (wants to comments?) is  ( yes ) then
        :Clicks food item;
        :comment;
        |System|
        :saves comment;
    else ( no ) 
    endif  
    
    

|User|
:views comments;
endwhile(no)

stop
@enduml
```


