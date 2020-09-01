import * as functions from 'firebase-functions';
const admin = require('firebase-admin');

const axios = require('axios');

var serviceAccount = require("../serviceAccountKey.json");
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://cal-o-meter.firebaseio.com",
  storageBucket:"cal-o-meter.appspot.com"
});

const db = admin.firestore();

// Start writing Firebase Functions
// https://firebase.google.com/docs/functions/typescript

let GCP_API_KEY= "x";

/*
export const foodImageRecongitionAPI = functions.https.onRequest((request, response) => {
    
    axios.post('https://vision.googleapis.com/v1/images:annotate?key='+GCP_API_KEY, {
        requests:[
            {
              "image":{
                "content":image

              },
              "features":[
                {
                  "type":"LABEL_DETECTION",
                  "maxResults":1
                }
              ]
            }
          ]
      })
      .then(function (res: any) {
        console.log("Vision API returned");
        console.log(JSON.stringify(res.data));        
        res.data.message="SUCCESS";
        response.send(res.data);
      })
      .catch(function (error: any) {
        console.log("API ERROR");
        console.log(error);
        response.send(JSON.stringify({message:"ERROR"}));
      });
  
});
*/

export const foodImageRecongitionAPI = functions.https.onCall((data, context) => {
    
    return axios.post('https://vision.googleapis.com/v1/images:annotate?key='+GCP_API_KEY, {
        requests:[
            {
              "image":{
                "content":data.image
              },
              "features":[
                {
                  "type":"LABEL_DETECTION",
                  "maxResults":1
                }
              ]
            }
          ]
      })
      .then(function (res: any) {
        console.log("Vision API returned");
        console.log(JSON.stringify(res.data));      
        //we just want the object name
        let returnObj={
            item:res.data.responses[0].labelAnnotations[0].description,
            message: "SUCCESS"
        }                
        return returnObj;
      })
      .catch(function (error: any) {
        console.log("API ERROR");
        console.log(error);
        return {message:"ERROR"};
      });
  
});
const app_id='X';
const app_key='X';
export const foodImageRecongitionAPI2 = functions.https.onCall((data, context) => {
    console.log("mealName is "+data.mealName);    
    let body= { 
        query: data.mealName 
    };
    let config={
        headers: {
        'Content-Type': 'application/json',
         'x-app-id': app_id,
         'x-app-key': app_key,
         'x-remote-user-id': '1' 
      }
    };

    return axios.post('https://trackapi.nutritionix.com/v2/natural/nutrients', body, config)
      .then(function (res: any) {
        console.log("Nutrition API returned");
        res.data.message="SUCCESS";     
        console.log(JSON.stringify(res.data));        
        
        return res.data;
      })
      .catch(function (error: any) {
        console.log("API ERROR");
        console.log(error);
        return {message:"ERROR"};
        
      });


});
  
  
  
export const uploadImage = functions.https.onRequest((req, res) => {
    
  

  var base64EncodedImageString = req.body.data.Thumbnail64,
  mimeType = 'image/jpeg',
  fileName = new Date().getTime()+'.jpg',
  imageBuffer = Buffer.from(base64EncodedImageString, 'base64');

  var bucket = admin.storage().bucket();

  // Upload the image to the bucket
  var file = bucket.file("prodPics/"+fileName);
  file.save(imageBuffer, {
    metadata: { contentType: mimeType },
  }, (async (error: any) => {

    if (error) {
        console.log(error);
        return res.status(500).send('Unable to upload the image.');
    }
       // admin.storage().bucket()
       // let url = await admin.storage().ref().child(fileName).getDownloadURL();
        return res.status(200).send("Uploaded");
  }));
  

});

exports.addRestaurantMeal = functions.https.onCall((data, context) => {
  return db.collection("RestaurantMeals").add({
    name:data.restaurant,
    meal: data.meal,
    calories:data.calories,    
  }).then(() => {
    
    return "Restaurant Meal Added";
  }).catch(function() {
    throw new functions.https.HttpsError("not-found", "Could not add meal", {
      "testing": "this is a test return value"
    });
  });
});
