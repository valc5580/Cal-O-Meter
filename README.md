# Cal-O-Meter
Cal-O-Meter is a calorie counter service which allows users to plan/track their meals and caloric intake, as well as find recipes and local restaurants according to their dietary needs. This was implemented using Firebase (Authentication, Firestore, Cloud Functions), GCP Vision API, NutritionX API, and EDAMAM Recipe Search API, and Google's Android barcode scanning library. 
<br>
<br>
Checkout the demo below: 
<br>
<br>
[![Demo Video](http://img.youtube.com/vi/0Rc-3NSIlHU/0.jpg)](http://www.youtube.com/watch?v=0Rc-3NSIlHU)
<br>
https://www.youtube.com/watch?v=0Rc-3NSIlHU
<br>
<br>
<br>
The user can input meals in the following ways: <br>
- Single Food Item
  - Manual Input
  - Search By Name
  - Scan Barcode
  - Take Picture
- Search for recipies within a caloric range given ingredients (using EDAMAM Recipe Search API)
- Search for local restaurants that have meals within a caloric range (querying Firestore database)
<br>
<br>
<br>
Since this is the public version, the repo does not contain the settings json files for the Firebase project or for the cloudfunctions code. Also the API keys for the GCP Vision API, NutritionX API, and EDAMAM API have been removed.
