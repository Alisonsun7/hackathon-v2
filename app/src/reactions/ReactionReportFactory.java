package reactions;

import java.util.Locale;

public final class ReactionReportFactory {
	private ReactionReportFactory() {}

	public static IReactionReporter buildReporter(String type) {
		if (type == null) throw new IllegalArgumentException("Reporter type is null");
		String k = type.trim().toLowerCase(Locale.ROOT);
		return switch (k) {
			case "oldest"   -> new OldestReactionReporter();
			case "overview" -> new OverviewReactionReporter();
			default -> throw new IllegalArgumentException("Unknown reporter: " + type);
		};
	}
}
