package com.crossover.techtrial.businessrule;

import com.crossover.techtrial.businessrule.businesscondition.IS_RIDE_END_DATE_LESS_THAN_START_DATE;
import com.crossover.techtrial.exceptions.CrossRideBusinessException;
import com.crossover.techtrial.model.Ride;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "prototype")
public class RideBusinessRule implements BusinessRule<Ride> {

    @Autowired
    private IS_RIDE_END_DATE_LESS_THAN_START_DATE is_ride_end_date_less_than_start_date;

    @Override
    public void execute(Ride ride) throws CrossRideBusinessException {
        if (is_ride_end_date_less_than_start_date.test(ride)) {
            throw new CrossRideBusinessException("INVALID_DATA", "End time can not be less than or equal to start time");
        }
    }

}
