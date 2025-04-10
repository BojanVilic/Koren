const admin = require("firebase-admin");

admin.initializeApp();

const callHomeFunctions = require("./callHome");
exports.sendCallHomeNotification = callHomeFunctions.sendCallHomeNotification;