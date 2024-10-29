# VassarEats - Vision document

## 1. Introduction

We envision a robust dining application, Vassar Eats, that will be easier to use and will be highly interactive. It will
make students and staff at Vassar to make quick informed dining decisions with the flexibility to give feedback on the 
dining services. For dining administrators, the app will be used to get more information about people's preferences on 
dining options at Vassar. 

## 2. Business case
Our dining application addresses customer needs that the *https://vassar.cafebonappetit.com/* website does not:
1. It can allow customers to like or dislike dining options.
2. It recommends dining options to customers based on their preferences (both present day and future recommendations). 
3. It provides a feedback section where customers can comment on dining options and give real time feedback to dining
administrators.

## 3. Key functionality
- Browse dining options (with pictorial illustration of each menu).
- Like or dislike dining options.
- Filter dining options based on dietary preferences (e.g. vegetarian, halal etc.).
- Read dining articles available on the vassar dining website.
- Recommend dining options to customers based on their preferences.
- Provide feedback section where users can voice out their honest opinions.
- Give comments on a particular food item

## 4. Stakeholder goals summary
- **Vassar students, staff**: browse dining options, like or dislike dining options, filter dining options, read dining 
articles, give feedback, get recommended menu items, view users' food preferences ('likes' or 'dislikes').
- **Visitors**: browse dining options, filter dining options, read dining articles, view users' food preferences 
('likes' or 'dislikes').
- **Dining administrators and chefs**: browse dining options, filter dining options, read dining articles, view feedback, 
view users' food preferences ('likes' or 'dislikes').

## Use case diagram

```plantuml
skin rose

' human actors
actor "User" as user

' system actors
actor "Cafe bon appetit system" <<system>> as cafebonappetit

' list all use cases in package
package VassarEats{
    
    usecase "Browse" as browseMenu
    usecase "Like/dislike" as preferenceType
    usecase "Give Feedback" as giveFeedback
    usecase "Get recommendations" as getRecommendations
    usecase "Read articles" as readArticles
    usecase "Preference" as preference
    usecase "Comment" as comment
    
    usecase "Authenticate" as authenticate
}

' list relationships between actors and use cases
user --> browseMenu

browseMenu <|-- readArticles : <<extends>>
browseMenu <|-- preference : <<extends>>
browseMenu <|-- giveFeedback : <<extends>>
browseMenu <|-- preferenceType : <<extends>>
browseMenu <|-- getRecommendations : <<extends>>
browseMenu <|-- comment : <<extends>>

giveFeedback <|-- authenticate : <<include>>
preferenceType <|-- authenticate : <<include>>
getRecommendations <|-- authenticate : <<include>>


' system actors
browseMenu --> cafebonappetit
```

