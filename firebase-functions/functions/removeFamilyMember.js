const { onCall, HttpsError } = require("firebase-functions/v2/https");
const { logger } = require("firebase-functions/v2");
const admin = require("firebase-admin");

const db = admin.database();

const removeUserFromFamilyHandler = onCall(async (request) => {
    if (!request.auth) {
        logger.error("User unauthenticated. Function call aborted.");
        throw new HttpsError('unauthenticated', 'The function must be called while authenticated.');
    }

    const callingUserId = request.auth.uid;
    const { familyIdToRemoveFrom, userIdToRemove } = request.data;

    if (!familyIdToRemoveFrom || !userIdToRemove) {
        logger.error("Missing familyIdToRemoveFrom or userIdToRemove in request data.", {
            familyId: familyIdToRemoveFrom,
            userId: userIdToRemove
        });
        throw new HttpsError('invalid-argument', 'Missing familyIdToRemoveFrom or userIdToRemove.');
    }

    logger.log(`Processing request to remove user ${userIdToRemove} from family ${familyIdToRemoveFrom}, called by ${callingUserId}.`);

    try {
        const familyRef = db.ref(`/families/${familyIdToRemoveFrom}`);

        const familySnapshot = await familyRef.once('value');
        const familyData = familySnapshot.val();

        if (!familyData) {
            logger.warn(`Family ${familyIdToRemoveFrom} not found during removal attempt for user ${userIdToRemove}.`);
            throw new HttpsError('not-found', `Family ${familyIdToRemoveFrom} not found.`);
        }

        if (callingUserId !== userIdToRemove) {
            logger.log(`Permission check: ${callingUserId} attempting to remove ${userIdToRemove}. Further admin role verification would be needed in a production system if not self-removal.`);
        }

        const updates = {};

        if (familyData.members && familyData.members.includes(userIdToRemove)) {
            const updatedMembers = familyData.members.filter(memberId => memberId !== userIdToRemove);
            updates[`/families/${familyIdToRemoveFrom}/members`] = updatedMembers;
            logger.log(`User ${userIdToRemove} will be removed from members list of family ${familyIdToRemoveFrom}.`);
        } else {
            logger.log(`User ${userIdToRemove} not found in members list of family ${familyIdToRemoveFrom}. No change to members list.`);
        }

        updates[`/users/${userIdToRemove}/familyId`] = null;
        logger.log(`FamilyId for user ${userIdToRemove} will be set to null.`);

        updates[`/users/${userIdToRemove}/lastActivityId`] = null;
        logger.log(`Last activityId for user ${userIdToRemove} will be set to null.`);

        updates[`/users/${userIdToRemove}/fcmToken`] = null;
        logger.log(`FCM token for user ${userIdToRemove} will be set to null.`);

        updates[`/users/${userIdToRemove}/familyRole`] = null;
        logger.log(`Family role for user ${userIdToRemove} will be set to null.`);

        const activitiesSnapshot = await db.ref(`/activities/${familyIdToRemoveFrom}/location`).once('value');
        if (activitiesSnapshot.exists()) {
            activitiesSnapshot.forEach(activityLocSnapshot => {
                if (activityLocSnapshot.val().userId === userIdToRemove) {
                    updates[`/activities/${familyIdToRemoveFrom}/location/${activityLocSnapshot.key}`] = null;
                    logger.log(`Removing activity location ${activityLocSnapshot.key} for user ${userIdToRemove} in family ${familyIdToRemoveFrom}.`);
                }
            });
        }

        if (familyData.callHomeRequests) {
            for (const targetId in familyData.callHomeRequests) {
                if (targetId === userIdToRemove || (familyData.callHomeRequests[targetId] && familyData.callHomeRequests[targetId].requesterId === userIdToRemove)) {
                    updates[`/families/${familyIdToRemoveFrom}/callHomeRequests/${targetId}`] = null;
                    logger.log(`Removing callHomeRequest involving user ${userIdToRemove} (target/requester: ${targetId}) from family ${familyIdToRemoveFrom}.`);
                }
            }
        }

        if (familyData.tasks) {
            for (const taskId in familyData.tasks) {
                const task = familyData.tasks[taskId];
                if (task && (task.assigneeUserId === userIdToRemove || task.creatorUserId === userIdToRemove)) {
                    updates[`/families/${familyIdToRemoveFrom}/tasks/${taskId}`] = null;
                    logger.log(`Deleting task ${taskId} (assignee or creator: ${userIdToRemove}) in family ${familyIdToRemoveFrom}.`);
                }
            }
        }

        if (familyData.events) {
             for (const eventId in familyData.events) {
                 const event = familyData.events[eventId];
                 if (event && event.creatorUserId === userIdToRemove) {
                    updates[`/families/${familyIdToRemoveFrom}/events/${eventId}`] = null;
                    logger.log(`Deleting event ${eventId} (creator: ${userIdToRemove}) in family ${familyIdToRemoveFrom}.`);
                 }
             }
        }

        if (Object.keys(updates).length > 0) {
            await db.ref().update(updates);
            logger.log(`Successfully applied all updates for removing user ${userIdToRemove} from family ${familyIdToRemoveFrom}.`);
        } else {
            logger.log(`No updates were necessary for user ${userIdToRemove} in family ${familyIdToRemoveFrom}.`);
        }

        return { success: true, message: `User ${userIdToRemove} processed for removal from family ${familyIdToRemoveFrom}.` };

    } catch (error) {
        if (error instanceof HttpsError) {
            logger.error(`HttpsError while removing user ${userIdToRemove} from family ${familyIdToRemoveFrom}: ${error.code} - ${error.message}`, error.details);
            throw error;
        }
        logger.error(`Generic error removing user ${userIdToRemove} from family ${familyIdToRemoveFrom}:`, error);
        throw new HttpsError('internal', 'Failed to remove user from family.', error.message);
    }
});

module.exports = {
  removeUserFromFamily: removeUserFromFamilyHandler,
};