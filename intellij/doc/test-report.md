# Test Report

We used system tests to test our prototype for iteration 1. According to our tests, our prototype worked to our
expectation. 

## Printing all food items in the menu for a day
When the program is run, it welcomes the user to Vassar Eats and asks the user to choose their preference. If the user inputs
the number 8, it means that the user doesn't have any dietary preferences. Hence, the full list of menu for the day is printed.

```angular2html
Welcome to Vassar Eats
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
8
**************************************************************************
                               Menu Items                                 
**************************************************************************
                               Breakfast                                 
**************************************************************************
              Breakfast Specials                          
***********************************************************
          Pressed                   
********************************************
Food id: 26976840
Food name: make your own belgian waffle
Dietary labels: [Vegetarian]
*****************************
********************************************
          The Grill                   
********************************************
Food id: 26976747
Food name: omelette made to order
Dietary labels: [Vegetarian, Humane]
*****************************
********************************************
          Root                   
********************************************
Food id: 26976571
Food name: make your own vegan waffles
Dietary labels: [Made without Gluten-Containing Ingredients, Vegan]
*****************************
Food id: 26976597
Food name: strawberry and chia seed oatmeal
Dietary labels: [Vegan]
*****************************
Food id: 26976598
Food name: tofu scramble with spinach and sun dried tomatoes
Dietary labels: [Made without Gluten-Containing Ingredients, Vegan]
*****************************
********************************************
          Stocks                   
********************************************
Food id: 26976524
Food name: cream of wheat
Dietary labels: [Vegan]
*****************************
Food id: 26976521
Food name: oatmeal
Dietary labels: [Vegan]
*****************************
********************************************
          Coffee & Sweets                   
********************************************
Food id: 26976804
Food name: cinnamon swirl coffee cake
Dietary labels: [Vegetarian]
*****************************
Food id: 26976803
Food name: mini assorted muffins
Dietary labels: [Vegetarian]
*****************************
Food id: 26976805
Food name: croissants
Dietary labels: [Vegetarian]
*****************************
********************************************
          Home                   
********************************************
Food id: 26978419
Food name: vegetarian sausage patties
Dietary labels: [Vegetarian]
*****************************
Food id: 26978457
Food name: lemon poppy seed pancakes
Dietary labels: [Vegetarian]
*****************************
Food id: 26978454
Food name: hash brown potatoes
Dietary labels: [Made without Gluten-Containing Ingredients, Vegan]
*****************************
Food id: 26978456
Food name: cage free  scrambled eggs with tomato and onions
Dietary labels: [Made without Gluten-Containing Ingredients, Vegetarian, Humane]
```

## Printing selected food items for a day based on dietary preferences
When the program is run, it welcomes the user to Vassar Eats and asks the user to choose their preference. If the user inputs
the number between 1 and 8 inclusive, the program filters the menu options based on the dietary preference(s) selected. 
More than one preference can be selected by the user by using commas to separate the numbers. 

### User selects just one preference
```angular2html
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
1
**************************************************************************
                               Menu Items                                 
**************************************************************************
                               Breakfast                                 
**************************************************************************
              Breakfast Specials                          
***********************************************************
          Pressed                   
********************************************
Food id: 26976840
Food name: make your own belgian waffle
Dietary labels: [Vegetarian]
*****************************
********************************************
          The Grill                   
********************************************
Food id: 26976747
Food name: omelette made to order
Dietary labels: [Vegetarian, Humane]
*****************************
********************************************
          Root                   
********************************************
********************************************
          Stocks                   
********************************************
********************************************
          Coffee & Sweets                   
********************************************
Food id: 26976804
Food name: cinnamon swirl coffee cake
Dietary labels: [Vegetarian]
*****************************
Food id: 26976803
Food name: mini assorted muffins
Dietary labels: [Vegetarian]
*****************************
Food id: 26976805
Food name: croissants
Dietary labels: [Vegetarian]
```

### User selects more than one preference separated by commas
```angular2html
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
4,6
**************************************************************************
                               Menu Items                                 
**************************************************************************
                               Breakfast                                 
**************************************************************************
              Breakfast Specials                          
***********************************************************
          Pressed                   
********************************************
********************************************
          The Grill                   
********************************************
Food id: 26976747
Food name: omelette made to order
Dietary labels: [Vegetarian, Humane]
*****************************
********************************************
          Root                   
********************************************
********************************************
          Stocks                   
********************************************
********************************************
          Coffee & Sweets                   
********************************************
********************************************
          Home                   
********************************************
Food id: 26978456
Food name: cage free  scrambled eggs with tomato and onions
Dietary labels: [Made without Gluten-Containing Ingredients, Vegetarian, Humane]
*****************************
Food id: 26978455
Food name: turkey sausage links
Dietary labels: [Made without Gluten-Containing Ingredients, Humane]
*****************************
Food id: 26978414
Food name: cage free hard boiled eggs
Dietary labels: [Made without Gluten-Containing Ingredients, Vegetarian, Humane]
*****************************
********************************************
***********************************************************
              Additional Breakfast Favorites                          
***********************************************************
          Stocks                   
********************************************
********************************************
          The Farmer's Table                   
********************************************
********************************************
          Home                   
********************************************
********************************************
***********************************************************
              Breakfast Condiments and Extras                          
***********************************************************
          Beverages                   
********************************************
********************************************
***********************************************************

**************************************************************************
                               Lunch                                 
**************************************************************************
              Lunch Specials                          
***********************************************************
          The Grill                   
********************************************
********************************************
          Pressed                   
********************************************
Food id: 26976841
Food name: turkey and cheddar cheese panini
Dietary labels: [Farm to Fork, Humane]
```

## Dealing with user input errors
We dealt with errors using try/catch blocks. We also created another class called **InputReport** that identifies user input
errors. 

If **users**: 
* input non-integers 
* do not separate integers with commas
* input an integer less than 1 or greater than 9
* do not input any integer
* use spaces (or other symbols) to separate integers

the program catches these errors and asks the user to input the right number. The error messages are also very descriptive 
so that users can fully understand why the program is asking them to input their preferences again.

### Inputting an integer that's greater than 8
```angular2html
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
13
You entered the wrong number. Please enter numbers between 1 and 8 inclusive
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
```

### Giving an input that's not an integer
```angular2html
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
lskdfdgh
Please enter a valid number
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
```

### Inputting non-integers separated by commas
```Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
oadhgoidghofig,djgeihgroighrighr
Please enter a valid number
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
```

### Inputting a number less than 1
```angular2html
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
0
You entered the wrong number. Please enter numbers between 1 and 8 inclusive
Please, choose your food preference
1. Vegetarian 
2. Vegan 
3. Halal 
4. Kosher 
5. Made without gluten-containing ingredients 
6. Humane 
7. Farm to Fork 
8. I don't have any dietary preferences
To select preferences, write the number corresponding to the preference you want to select. To select multiple preferences, add a comma before writing the next number
```