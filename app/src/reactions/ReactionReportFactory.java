package reactions;

public class ReactionReportFactory {
	public static IReactionReporter buildReporter(String type) {
		if (type == null) throw new IllegalArgumentException("Algorithm is null");
		String k = type.trim().toLowerCase();
		return switch (k) {
			case "oldest"   -> new OldestReactionReporter();
			case "overview" -> new OverviewReactionReporter();
			default -> throw new IllegalArgumentException("Unknown algorithm: " + type);
		};
	}
}
