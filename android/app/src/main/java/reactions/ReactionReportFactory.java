package reactions;

public class ReactionReportFactory {
    public static IReactionReporter buildReporter(String kind) {
        if (kind == null) throw new IllegalArgumentException("Reporter type is null");

        return switch (kind.toLowerCase()) {
            case "oldest" -> new OldestReporter();
            case "overview" -> new OverviewReporter();
            default -> throw new IllegalArgumentException("Unknown reporter type: " + kind);
        };
    }
}
