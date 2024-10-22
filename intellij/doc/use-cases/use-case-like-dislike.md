# Like-Dislike

## 1. Primary actor and goals

__Users (students, staff (dining administrators and chefs))__: They want to like or dislike a menu option based on their 
preference. They want to ensure that they can do this with minimal stress and their 'like' or 'dislike' preferences remain 
unchanged except they change it themselves. They also will like to view the dining options for a particular day with their 
'liked' food choices appearing first before other food options. Moreover, they want to be able to view people's total 
preferences for a particular menu option. This information can help dining administrators and chefs to make better dining 
decisions for users.

## 2. Other stakeholders and their goals

* __Visitors__: They want to be able to view people's preference for a particular menu option. This information can help 
* them to make better informed dining decisions.

## 3. Preconditions

* The app is able to access dining options from the dining website *https://vassar.cafebonappetit.com/*.
* Users are able to browse the menu options.

## 4. Postconditions

* Users and administrators are identified and authenticated.
* 'Like' or 'dislike' choices are saved.
* The system uses this preference to order the menu (with liked preferences appearing first).
* Users can remove this preference by clicking on the 'like' or 'dislike' button again, and the system correctly responds
to this change.

## 5. Workflow

```plantuml
@startuml

skin rose

title Preferences (casual level)

'define the lanes
|#application|User|
|#technology|Visitor|
|#implementation|System|

|User|
start
:Browse through the food menu;
:Likes or dislikes a food item;
while (Authentication?) is (False)
  :Authenticate the user;
endwhile (True)

|System|
:Saves authentication;
:Saves the food item;
:Rearranges menu options based on user's preference;

|User|
:Filter menu options based on their preferences;
:Views the number of likes or dislikes for a menu option;

|Visitor|
:Views the number of likes or dislikes for a menu option;

|User|
:Undo the action;

|System|
:Checks authentication;
if (Authentication?) is  ( False ) then
:Request authentication;
else ( True ) 
endif
:Unsaves food item;
:Rearranges menu options based on user's preference;
stop
@enduml
```