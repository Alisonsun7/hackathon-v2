import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import reactions.*;
import dao.*;
import dao.model.*;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ReactionReportOverviewTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        RandomContentGenerator.populateRandomData();

        List<Object[]> scenarios = new ArrayList<>();

        // Scenario 1: Basic frequency ordering
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();
                    User userB = getDifferentUser(userA);

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
                    ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2);
                    ReactionsFacade.addReaction(getDifferentUser(userA, userB).getUUID(),
                            message.id(), ReactionType.HAPPY, 3);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{
                        new ReactionDisplayTag(ReactionType.HAPPY, "2"),
                        new ReactionDisplayTag(ReactionType.ANGRY, "1")
                }
        });

        // Scenario 2: Tie-breaker by earliest appearance
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();
                    User userB = getDifferentUser(userA);

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
                    ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2);
                    ReactionsFacade.addReaction(getDifferentUser(userA, userB).getUUID(),
                            message.id(), ReactionType.ANGRY, 3);
                    ReactionsFacade.addReaction(getDifferentUser(userA, userB).getUUID(),
                            message.id(), ReactionType.HAPPY, 4);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{
                        new ReactionDisplayTag(ReactionType.HAPPY, "2"),
                        new ReactionDisplayTag(ReactionType.ANGRY, "2")
                }
        });

        // Scenario 3: Deletion removes type completely
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 1);
                    ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.SAD);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{}
        });

        // Scenario 4: More than 5 types, should truncate
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.ANGRY, 2);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 3);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.LAUGH, 4);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.GOOD_LUCK, 5);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.CONGRATULATIONS, 6);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{
                        new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                        new ReactionDisplayTag(ReactionType.ANGRY, "1"),
                        new ReactionDisplayTag(ReactionType.SAD, "1"),
                        new ReactionDisplayTag(ReactionType.LAUGH, "1"),
                        new ReactionDisplayTag(ReactionType.GOOD_LUCK, "1")
                }
        });

        // Scenario 5: Empty message, expect []
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{}
        });

        // Scenario 6: Mixed counts and tie-breaking
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();
                    User userB = getDifferentUser(userA);
                    User userC = getDifferentUser(userA, userB);

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
                    ReactionsFacade.addReaction(userB.getUUID(), message.id(), ReactionType.ANGRY, 2);
                    ReactionsFacade.addReaction(userC.getUUID(), message.id(), ReactionType.ANGRY, 3);
                    ReactionsFacade.addReaction(getDifferentUser(userA, userB, userC).getUUID(),
                            message.id(), ReactionType.HAPPY, 4);
                    ReactionsFacade.addReaction(getDifferentUser(userA, userB, userC).getUUID(),
                            message.id(), ReactionType.LAUGH, 5);
                    ReactionsFacade.addReaction(getDifferentUser(userA, userB, userC).getUUID(),
                            message.id(), ReactionType.LAUGH, 6);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{
                        new ReactionDisplayTag(ReactionType.HAPPY, "2"),
                        new ReactionDisplayTag(ReactionType.ANGRY, "2"),
                        new ReactionDisplayTag(ReactionType.LAUGH, "2")
                }
        });

        // Scenario 7: Deleting one type completely
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.SAD, 1);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 2);

                    ReactionsFacade.removeReaction(userA.getUUID(), message.id(), ReactionType.SAD);
                    ReactionsFacade.removeReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{}
        });

        // Scenario 8: Exactly five different reaction types
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.ANGRY, 2);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 3);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.LAUGH, 4);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.GOOD_LUCK, 5);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{
                        new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                        new ReactionDisplayTag(ReactionType.ANGRY, "1"),
                        new ReactionDisplayTag(ReactionType.SAD, "1"),
                        new ReactionDisplayTag(ReactionType.LAUGH, "1"),
                        new ReactionDisplayTag(ReactionType.GOOD_LUCK, "1")
                }
        });

        // Scenario 9: More than five types, check truncation order
        scenarios.add(new Object[]{
                (Runnable) () -> {
                    Message message = getRandomMessage();
                    User userA = UserDAO.getInstance().getRandom();

                    ReactionsFacade.addReaction(userA.getUUID(), message.id(), ReactionType.HAPPY, 1);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.ANGRY, 2);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.SAD, 3);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.LAUGH, 4);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.GOOD_LUCK, 5);
                    ReactionsFacade.addReaction(getDifferentUser(userA).getUUID(), message.id(), ReactionType.CONGRATULATIONS, 6);

                    ScenarioContext.setMessage(message);
                },
                new ReactionDisplayTag[]{
                        new ReactionDisplayTag(ReactionType.HAPPY, "1"),
                        new ReactionDisplayTag(ReactionType.ANGRY, "1"),
                        new ReactionDisplayTag(ReactionType.SAD, "1"),
                        new ReactionDisplayTag(ReactionType.LAUGH, "1"),
                        new ReactionDisplayTag(ReactionType.GOOD_LUCK, "1")
                }
        });

        return scenarios;
    }

    @Parameterized.Parameter(0)
    public Runnable setup;

    @Parameterized.Parameter(1)
    public ReactionDisplayTag[] expected;

    @Before
    public void reset() {
//        ReactionDAO.getInstance().clear();
    }

    @Test
    public void testOverviewReport() {
        setup.run();
        Message message = ScenarioContext.getMessage();

        ReportSources sources = new ReportSources();
        for (IReactionReporter reporter : sources.getReporters()) {
            ReactionDisplayTag[] actual = reporter.generateReport(message);
            assertArrayEquals("Failed on " + reporter.getClass().getSimpleName(), expected, actual);
        }
    }

    static class ScenarioContext {
        private static Message message;
        public static void setMessage(Message msg) { message = msg; }
        public static Message getMessage() { return message; }
    }

    private static Message getRandomMessage() {
        Message msg = null;
        while (msg == null) {
            Post post = PostDAO.getInstance().getRandom();
            msg = post.messages.getRandom();
        }
        return msg;
    }

    private static User getDifferentUser(User... exclude) {
        User u = null;
        while (u == null || Arrays.asList(exclude).contains(u)) {
            u = UserDAO.getInstance().getRandom();
        }
        return u;
    }
}
