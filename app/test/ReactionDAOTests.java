import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import reactions.*;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ReactionDAOTests {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        UUID msg = UUID.randomUUID();
        UUID user = UUID.randomUUID();

        List<Object[]> scenarios = new ArrayList<>();

        scenarios.add(new Object[] {
                msg, user,
                new Runnable[] {
                        () -> assertTrue(ReactionsFacade.addReaction(user, msg, ReactionType.HAPPY, 1L))
                },
                Arrays.asList(ReactionType.HAPPY)
        });

        scenarios.add(new Object[] {
                msg, user,
                new Runnable[] {
                        () -> assertTrue(ReactionsFacade.addReaction(user, msg, ReactionType.HAPPY, 1L)),
                        () -> assertFalse(ReactionsFacade.addReaction(user, msg, ReactionType.HAPPY, 2L))
                },
                Arrays.asList(ReactionType.HAPPY)
        });

        UUID msg2 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();
        scenarios.add(new Object[] {
                msg2, user2,
                new Runnable[] {
                        () -> ReactionsFacade.addReaction(user2, msg2, ReactionType.HAPPY, 1L),
                        () -> assertTrue(ReactionsFacade.removeReaction(user2, msg2, ReactionType.HAPPY))
                },
                Collections.emptyList()
        });

        UUID msg3 = UUID.randomUUID();
        UUID user3 = UUID.randomUUID();
        scenarios.add(new Object[] {
                msg3, user3,
                new Runnable[] {
                        () -> ReactionsFacade.addReaction(user3, msg3, ReactionType.HAPPY, 10L),
                        () -> ReactionsFacade.addReaction(user3, msg3, ReactionType.SAD, 5L)
                },
                Arrays.asList(ReactionType.SAD, ReactionType.HAPPY)
        });

        return scenarios;
    }

    @Parameterized.Parameter(0)
    public UUID messageId;

    @Parameterized.Parameter(1)
    public UUID userId;

    @Parameterized.Parameter(2)
    public Runnable[] operations;

    @Parameterized.Parameter(3)
    public List<ReactionType> expectedReactions;

    @Test
    public void testDAOBehaviour() {
        // Execute the sequence of operations
        for (Runnable op : operations) {
            op.run();
        }

        // Verify the final result from getReactions
        List<ReactionType> actual = ReactionsFacade.getReactions(userId, messageId);
        assertEquals(expectedReactions, actual);
    }
}
