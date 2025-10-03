package reactions;

import dao.model.Message;

abstract class AbstractReactionReporter implements IReactionReporter {
    @Override
    public final ReactionDisplayTag[] generateReport(Message message) {
        if (message == null) return new ReactionDisplayTag[0];
        return doGenerate(message);
    }

    protected abstract ReactionDisplayTag[] doGenerate(Message message);
}
