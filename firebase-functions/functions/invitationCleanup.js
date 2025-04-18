const { onSchedule } = require("firebase-functions/v2/scheduler");
const { logger } = require("firebase-functions/v2");
const admin = require("firebase-admin");

/**
 * Scheduled function (v2) to cleanup and expire invitations.
 * Runs once every 24 hours using Cloud Scheduler.
 */
const cleanupInvitationsHandler = onSchedule('every 24 hours', async (event) => {
  const db = admin.database();
  const invitationsRef = db.ref('invitations');

  const now = Date.now();
  const fortyEightHoursAgo = now - (48 * 60 * 60 * 1000);

  try {
    logger.info('Starting invitation cleanup job (v2).');

    const snapshot = await invitationsRef.once('value');
    const invitations = snapshot.val();

    if (!invitations) {
      logger.info('No invitations found to process.');
      return null;
    }

    const updates = {};
    let itemsToProcessCount = 0;

    for (const invitationId in invitations) {
      const invitation = invitations[invitationId];

      if (!invitation || invitation.status === undefined || invitation.createdAt === undefined) {
          logger.warn(`Skipping invalid invitation entry: ${invitationId}`);
          continue;
      }

      const status = invitation.status;
      const createdAt = invitation.createdAt;

      itemsToProcessCount++;

      if (status === 'ACCEPTED' || status === 'DECLINED' || status === 'EXPIRED') {
        updates[invitationId] = null;
        logger.info(`-> Marking invitation ${invitationId} for deletion (status: ${status}).`);
      }
      else if (status === 'PENDING' && createdAt < fortyEightHoursAgo) {
        updates[`${invitationId}/status`] = 'EXPIRED';
        logger.info(`-> Marking pending invitation ${invitationId} as EXPIRED (created at: ${new Date(createdAt).toISOString()}).`);
      }
      else if (status !== 'PENDING') {
          logger.warn(`-> Encountered unexpected invitation status for ${invitationId}: ${status}`);
      }
    }

    if (Object.keys(updates).length > 0) {
      logger.info(`Executing batch operation for ${Object.keys(updates).length} invitation updates/deletes out of ${itemsToProcessCount} items checked.`);
      await invitationsRef.update(updates);
      logger.info('Invitation cleanup batch operation completed successfully.');
    } else {
      logger.info(`No invitations needed updating or deleting out of ${itemsToProcessCount} items checked.`);
    }

    return null;
  } catch (error) {
    logger.error('Error during invitation cleanup:', error);
    throw error;
  }
});

module.exports = {
  cleanupInvitations: cleanupInvitationsHandler
};