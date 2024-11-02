# Requirements Documentation

We made several substantial changes from the previous iteration. First, our use-cases increased from four to eight, as we
discover more features that need to be implemented on the app. For each use cases, we will go over substantial changes we
made.

## 1. Browse
* We made the naming consistent (we changed 'browse menu' to 'browse')
* We modified the use-case diagrams to fully reflect how we want the browse use-case to work in reality. 
  * We merged use-cases that are related to a particular goal (e.g. browse and filter) and we separated use-cases that are
  for different goals (e.g. feedback and comment).
  * We added a loop to ensure that the user can keep browsing back and forth
  * We used switch cases to ensure that the user is not forced to do any of the actions in a particular order

## 2. Preference
* We separated preference from likes-dislikes
* We expanded preference to include all dietary preferences
* We made naming consistent

## 3. Likes-Dislikes
* Apart from making this a separate use-case from the preference use-case, we ensured that our use-case diagrams didn't 
force the user to perform any action in any order.
* This is one of the new use-cases we added in this iteration (iteration one)

## 4. Comment
* This is one of the new use-cases we added in this iteration (iteration one)
* We made this use-case available for everyone (a user doesn't need a vassar email account to comment on a food item)
* An authenticated user can choose to comment anonymously or with their names shown on the comment section, but an 
authenticated user can only comment anonymously.

## 5. Feedback
* We made the use-case diagram simpler and easier to understand
* We made naming consistent
* User has to be authenticated to give feedback
* We changed the functionality of the use-case, so that users will email the dining administrators directly, instead of 
giving feedback on the app.

## 6. Authenticate
* This is one of the new use-cases we added in this iteration (iteration one)
* Only likes-dislikes, feedback, and get-notification use-cases require authentication
* To authenticate, users will present their username, and email (must be a vassar email address for the user to be 
successfully authenticated)

## 7. Get-Notifications
* We changed the get-recommendations use-case to get-notifications. 
* The main function of the use-case is to notify users (if users turn on their notification) of the food recommended by 
the app based on the preference of the user, specifically the food items that the user liked.

## 8. Read-Articles
* We separated this use-case from the browse use-case. 
* We made the use-case simpler. Instead of showing the dining articles on the app, it links to the dining articles on the 
website, from which the user can read the articles. 