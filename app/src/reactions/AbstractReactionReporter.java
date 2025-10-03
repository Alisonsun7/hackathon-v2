package reactions;

import dao.model.Message;

public abstract class AbstractReactionReporter implements IReactionReporter {
    @Override
    public final ReactionDisplayTag[] generateReport(Message message) {
        if (message == null) throw new IllegalArgumentException("message is null");
        return doGenerate(message);
    }
    protected abstract ReactionDisplayTag[] doGenerate(Message message);
}
