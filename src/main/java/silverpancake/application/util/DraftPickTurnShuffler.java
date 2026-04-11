package silverpancake.application.util;

import org.springframework.stereotype.Service;
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

}
