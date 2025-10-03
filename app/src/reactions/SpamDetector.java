package reactions;

import dao.PostDAO;
import dao.model.Message;
import dao.model.User;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class SpamDetector {

	// Score threshold (tunable)
	private static final float THRESHOLD = 5.0f;
	// Cap for frequency contribution: 1 / min(freq, COUNT_CAP)
	private static final int COUNT_CAP = 3;

	public boolean checkspamforuser(User user) {
		if (user == null) return false;

		Iterator<Message> messages = PostDAO.getInstance().getAllMessages();
		if (messages == null) return false;

		float aggregatedScore = 0.0f;
		final Set<UUID> distinctThreadsWhereUserReacted = new HashSet<>();

		while (messages.hasNext()) {
			Message message = messages.next();
			if (message == null) continue;

			// Count the frequency of each response type for the message
			int[] freq = computeFrequenciesSafely(message);

			// The user's reaction type to this message (maybe empty)
			List<ReactionType> userTypes =
					ReactionsFacade.getReactions(user.getUUID(), message.id());

			if (userTypes != null && !userTypes.isEmpty()) {
				// Only when the user responds to this message will it be counted in the thread
				UUID threadId = message.thread();
				if (threadId != null) distinctThreadsWhereUserReacted.add(threadId);

				// ∑ 1 / min(freq(type), 3)
				aggregatedScore += scoreForUserReactionsOnMessage(userTypes, freq);
			}
		}

		int threads = distinctThreadsWhereUserReacted.size();
		if (threads == 0) return false; // avoid division by zero; and "no response" should be classified as non-spam

		float normalized = aggregatedScore / threads;
		return normalized >= THRESHOLD;
	}

	// Build frequency array for all ReactionType on a single message using the reporting pipeline
	private static int[] computeFrequenciesSafely(Message message) {
		int[] frequency = new int[ReactionType.values().length + 100]; // remains original +100
		ReactionDisplayTag[] report =
				ReactionReportFactory.buildReporter("overview").generateReport(message);
		if (report != null) {
			for (ReactionDisplayTag tag : report) {
				if (tag == null || tag.type() == null) continue;
				int idx = tag.type().ordinal();
				if (idx < 0 || idx >= frequency.length) continue;
				try {
					String label = tag.label();
					if (label != null) frequency[idx] += Integer.parseInt(label);
				} catch (NumberFormatException ignored) { }
			}
		}
		return frequency;
	}

	// for one message: the cumulative score is based on the user's response type.
	private static float scoreForUserReactionsOnMessage(List<ReactionType> userTypes, int[] frequency) {
		float score = 0.0f;
		for (ReactionType type : userTypes) {
			if (type == null) continue;
			int idx = type.ordinal();
			if (idx < 0 || idx >= frequency.length) continue;
			int f = frequency[idx];
			int capped = (f > COUNT_CAP) ? COUNT_CAP : Math.max(f, 1); // consider 0, avoid 1/0
			score += 1.0f / capped;
		}
		return score;
	}
}
