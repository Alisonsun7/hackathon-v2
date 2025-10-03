package reactions;

import dao.model.Message;
import java.util.List;

final class OldestReactionReporter extends AbstractReactionReporter {
    private final ReactionDataSource data = new DefaultReactionDataSource();

    @Override
    protected ReactionDisplayTag[] doGenerate(Message message) {
        List<ReactionDataSource.ReactionRecord> rs = data.listEffectiveReactions(message);
        return new ReactionDisplayTag[0];
    }
}
