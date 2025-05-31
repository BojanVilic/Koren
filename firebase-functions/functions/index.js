const admin = require("firebase-admin");

admin.initializeApp();

const callHomeFunctions = require("./callHome");
exports.sendCallHomeNotification = callHomeFunctions.sendCallHomeNotification;

const invitationCleanupFunctions = require("./invitationCleanup");
exports.cleanupInvitations = invitationCleanupFunctions.cleanupInvitations;

const userManagementFunctions = require("./removeFamilyMember");
exports.removeUserFromFamily = userManagementFunctions.removeUserFromFamily;