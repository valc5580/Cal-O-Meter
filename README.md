# Cal-O-Meter
Cal-O-Meter is a calorie counter android app which allows users to plan/track their meals and caloric intake, as well as find recipes and local restaurants according to their dietary needs. This was implemented using Firebase (Authentication, Firestore, Cloud Functions), GCP Vision API, Nutritionix API, and EDAMAM Recipe Search API, and Google's Android barcode scanning library. 
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
1.Single Food Item:
  <br>
  <table>
    <tr>
      -Manual Input
    </tr>
    <tr>
      <table>
        <tr>
          <td>
            <img src="./Screenshots/manualInput.png" width="250" height="500"/>
          </td>
        </tr>
      </table>
    </tr>
    <tr>
      -Search By Name
    </tr>
    <tr>
      <table>
        <tr>
          <td>
            <img src="./Screenshots/searchByName.png" width="250" height="500"/>
          </td>
        </tr>
      </table>
    </tr>
    <tr>
      -Scan Barcode
    </tr>
    <tr>
      <table>
        <tr>
          <td>
            <img src="./Screenshots/barcodePic.png" width="250" height="500"/>
          </td>
          <td>
            <img src="./Screenshots/barcodePic2.png" width="250" height="500"/>
          </td>
        </tr>
      </table>
    </tr>
    <tr>
      -Take Picture
    </tr>
    <tr>
      <table>
        <tr>
          <td>
            <img src="./Screenshots/compVision.png" width="250" height="500"/>
          </td>
          <td>
            <img src="./Screenshots/compVision2.png" width="250" height="500"/>
          </td>
        </tr>
      </table>
  </table>
2.Search for recipes within a caloric range given ingredients (using EDAMAM Recipe Search API)
  <br>
      <table>
        <tr>
          <td>
            <img src="./Screenshots/recipieSearch.png" width="250" height="500"/>
          </td>
        </tr>
      </table>
3.Search for local restaurants that have meals within a caloric range (querying Firestore database)
  <br>
      <table>
        <tr>
          <td>
            <img src="./Screenshots/restaurant.png" width="250" height="500"/>
          </td>
        </tr>
      </table>
<br>
<br>
<br>
Since this is the public version, the repo does not contain the settings json files for the Firebase project or for the cloudfunctions code. Also the API keys for the GCP Vision API, Nutritionix API, and EDAMAM API have been removed.
