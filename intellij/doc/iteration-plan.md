## Iteration Plan

| Rank   | Requirement (use case or feature) | Comments                                      |
|--------|-----------------------------------|-----------------------------------------------|
| High   | Browse                            | Scores high on all metrics.                   |
| Medium | Preference                        | High criticality, medium coverage, low risk   |
| Medium | Recommendation                    | Medium criticality, low coverage, medium risk |
| Low    | Feedback                          | Scores low on all metrics.                    |

## Iteration Plan 2

| Rank   | Requirement (use case or feature) | Comments                                         |
|--------|-----------------------------------|--------------------------------------------------|
| High   | Browse                            | Scores high on all metrics.                      |
| High   | Preference                        | High criticality, medium coverage, low risk      |
| High   | Like-Dislike                      | Medium criticality, high coverage, medium risk   |
| Medium | Comment                           | Medium criticality, high coverage, medium risk   |
| Medium | Feedback                          | Medium criticality, low coverage, low risk       |
| Low    | Authentication                    | Medium criticality, medium coverage, medium risk |
| Low    | Get-Notifications                 | Scores low on all metrics.                       |
| Low    | Read-Articles                     | Scores low on all metrics.                       |

## Browse use-case

We have a prototype that prints out all the food menu, including their mealType (e.g. Breakfast, Lunch, etc.), 
mealTypeSection (e.g. Specials, additional favorites, etc.) and diningSection (e.g. Stocks, Root, etc.). The tasks needed
to complete the browse menu is to ensure that users can view food menu across the various dining centers on campus (e.g.
Retreat, Street Eats), and they can view the menu of any day (presumably, up to one week from the current day).

## Preference

We have been able to filter the food menu based on dietary preferences (e.g. vegan, vegetarian, etc.). Next, we have to
ensure that users can undo the filter option and the menu updates accordingly. Also, we have to ensure that users can 
view their food options recommended to them by the app based on their likes and dislikes, but to do this we have to also
work on the like-dislike use-case

## Like-Dislike

Users need to be able to like and dislike food items. The app will take inventory of the food they liked and disliked, and 
store these food items. Users should also be able to see the total likes and dislikes that a food item has. The **browse** 
use-case will be updated to show the number of likes or dislikes a certain food menu has. The preference will use the likes
and dislikes a food item has to recommended food options to the user at later times.