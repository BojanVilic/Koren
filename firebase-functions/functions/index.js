const {logger} = require("firebase-functions");
const {onRequest} = require("firebase-functions/v2/https");

// The Firebase Admin SDK to access Firestore.
const {initializeApp} = require("firebase-admin/app");

initializeApp();

exports.helloWorld = onRequest((request, response) => {
    logger.info("Hello logs!", {structuredData: true});
    response.send("Hello from Firebase!");
});