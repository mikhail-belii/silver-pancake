package silverpancake.application.util;

import org.springframework.stereotype.Service;
import silverpancake.domain.entity.draft.Draft;
import silverpancake.domain.entity.draft.DraftPickTurn;
import silverpancake.domain.entity.user.User;

import java.util.*;

@Service
public class DraftPickTurnShuffler {

    public List<User> getShuffledCaptainsByStudents(List<User> captainIds, Integer countOfStudents) {
        var reversedCaptainIds = captainIds.reversed();
        var shuffledCaptains = new ArrayList<User>(captainIds);

        while (shuffledCaptains.size() < countOfStudents) {
            shuffledCaptains.add(getNextCaptainId(captainIds, reversedCaptainIds, shuffledCaptains.size()));
        }

        return shuffledCaptains;
    }

    private User getNextCaptainId(List<User> captainIds, List<User> reversedCaptainIds, Integer currentSize) {
        if ((currentSize / captainIds.size()) % 2 == 0) {
            return captainIds.get(currentSize % captainIds.size());
        } else {
            return reversedCaptainIds.get(currentSize % captainIds.size());
        }
    }

    public List<DraftPickTurn> continueCaptainsByStudents(
            List<User> captainIds,
            List<DraftPickTurn> pickTurns,
            Integer countOfStudents,
            Integer countOfSelectedStudents,
            Draft draft
    ) {
        if (pickTurns.size() >= countOfStudents) {
            return pickTurns.subList(0, countOfStudents - 1);
        }

        var reversedCaptainIds = captainIds.reversed();

        while (pickTurns.size() < countOfStudents) {
            DraftPickTurn lastPickTurn;
            if (pickTurns.isEmpty()) {
                lastPickTurn = null;
            } else {
                lastPickTurn = pickTurns.getLast();
            }
            var lastOrder = lastPickTurn == null ? 0 : lastPickTurn.getOrder();
            pickTurns.add(new DraftPickTurn()
                    .setDraft(draft)
                    .setOrder(lastOrder + 1)
                    .setUser(getNextCaptainId(captainIds, reversedCaptainIds, countOfSelectedStudents + pickTurns.size()))) ;
        }
        return pickTurns;
    }

}
