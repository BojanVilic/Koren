const { onValueCreated } = require("firebase-functions/v2/database");
const { logger } = require("firebase-functions");
const admin = require("firebase-admin");

const sendCallHomeNotificationHandler = onValueCreated(
  {
    ref: "/families/{familyId}/callHomeRequests/{targetUserId}",
  },
  async (event) => {
    const familyId = event.params.familyId;
    const targetUserId = event.params.targetUserId;
    const requestData = event.data.val();

    if (!requestData) {
      logger.error(
        `[v2] Request data is null for path: ${event.data.ref.path}. Aborting.`
      );
      return null;
    }

    if (!requestData.requesterId) {
      logger.error(
        `[v2] RequesterId is missing in request data for path: ${event.data.ref.path}. Aborting.`
      );
      return null;
    }
    const { requesterId } = requestData;

    logger.log(
      `[v2] Processing call home request for target: ${targetUserId}, ` +
      `requester: ${requesterId}, family: ${familyId}`
    );

    try {
      const targetUserRef = admin.database().ref(`/users/${targetUserId}`);
      const targetUserSnapshot = await targetUserRef.once("value");
      const targetUserData = targetUserSnapshot.val();

      if (!targetUserData || !targetUserData.fcmToken) {
        logger.error(
          `[v2] Target user ${targetUserId} data or FCM token not found or empty.`
        );
        return null;
      }
      const targetFcmToken = targetUserData.fcmToken;
      logger.log(`[v2] Found FCM token for target ${targetUserId}`);

      const requesterRef = admin.database().ref(`/users/${requesterId}`);
      const requesterSnapshot = await requesterRef.once("value");
      const requesterData = requesterSnapshot.val();
      const requesterName = requesterData?.displayName || "Someone";
      logger.log(`[v2] Requester name: ${requesterName}`);

      const payload = {
        notification: {
          title: "Time to come home!",
          body: `${requesterName} is asking you to come back home.`,
        },
        data: { type: "CALL_HOME", requesterId: requesterId, familyId: familyId },
        token: targetFcmToken,
      };

      logger.log(`[v2] Sending FCM notification to ${targetUserId}...`);
      const response = await admin.messaging().send(payload);
      logger.log("[v2] Successfully sent FCM message:", response);

      return response;

    } catch (error) {
      if (error.errorInfo) {
           logger.error(
             `[v2] FirebaseMessagingError processing request for ${targetUserId}:`,
             error.errorInfo
           );
      } else {
           logger.error(
             `[v2] Generic error processing call home request for target ${targetUserId}:`,
             error
           );
      }
      return null;
    }
  }
);

module.exports = {
  sendCallHomeNotification: sendCallHomeNotificationHandler,
};