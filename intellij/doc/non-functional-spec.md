# Non-Functional Specifications

## 1. Usability
* Foods on the menu should be presented in a post format similar to instagram where:
  * Food items are presented as posts organized vertically where each post contains:
    * An image of the food item which will be the largest portion of the post (70%)
    * A title of the food's name
    * A description containing dietary labels (e.g. halal, vegetarian, etc.)
    * Two buttons (like and dislike) for user preferences
* On a separate tab will be a feedback form with:
  * Anonymize button
  * Input for feedback
  * Submit button
* Colors associated with common forms of color blindness should be avoided.

## 2. Reliability
* If scraping the dining website fails then the information saved from the most recent scrape should be used
* If the external authentication server fails then users should be able to view the app in guest mode
* If the external server fails to pull information then information saved should be used

## 3. Performance
The app needs to be efficient because users are looking through multiple pieces of information quickly to make a decision and so the app will need to load and save information efficiently.
* In order to do so:
  * Authentication process should time out after 10 seconds
  * Information should be saved in a database for faster retrieval and backup
  * Information should only be requested from external servers the first time users login for the day
  * User changes should be saved automatically saved to external servers throughout the day
  * User should have the option to download all information for faster retrieval

## 4. Supportability
Information will be displayed in English, units will be imperial, and date formating will be in 12-hour format

## 5. Implementation
* Software must run on Android devices.
* Software must be written using Java.

## 6. External Interfaces
* Must connect to Cafe Bon Appetit's server (https://vassar.cafebonappetit.com/)

## 7. Legal
* Using google stock photos for food images
* Dining information will be scraped form Vassar dining website
  * Users will be prompted if it is ok that we save their information for purposes of authentication, food recommendation, and feedback