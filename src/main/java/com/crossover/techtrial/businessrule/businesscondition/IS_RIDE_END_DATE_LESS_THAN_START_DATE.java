package com.crossover.techtrial.businessrule.businesscondition;

import com.crossover.techtrial.model.Ride;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.function.Predicate;

@Component
public class IS_RIDE_END_DATE_LESS_THAN_START_DATE implements Predicate<Ride> {
    @Override
    public boolean test(Ride ride) {
        LocalDateTime startDateTime = ride.getStartTime();
        LocalDateTime endDateTime = ride.getEndTime();

        return endDateTime.isBefore(startDateTime) || endDateTime.isEqual(startDateTime);
    }
}
