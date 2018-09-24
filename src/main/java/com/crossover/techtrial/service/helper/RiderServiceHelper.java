package com.crossover.techtrial.service.helper;

import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class RiderServiceHelper {

    @Autowired
    private RideRepository rideRepository;

    public Duration getMaxRideDuration(Person driver) {
        List<Ride> rides =  rideRepository.findAllByDriver(driver);
        AtomicReference<Duration> maxDuration = new AtomicReference<>();

        rides.forEach(ride -> {
            if(maxDuration.get() == null || maxDuration.get().getSeconds() < Duration.between(ride.getStartTime(), ride.getEndTime()).getSeconds()){

                maxDuration.set(Duration.between(ride.getStartTime(), ride.getEndTime()));
            }
        });

        return maxDuration.get();
    }

    public Duration getTotalDurationIgnoringOverlap(List<Ride> rides){

        rides.sort(new Comparator<Ride>() {
            @Override
            public int compare(Ride o1, Ride o2) {
                if(o2.getStartTime().isBefore(o1.getStartTime())){
                    return 1;
                }
                else if(o2.getStartTime().isEqual(o1.getStartTime())){
                    return 0;
                }
                else {
                    return -1;
                }
            }
        });

        LocalDateTime baseStart = rides.get(0).getStartTime();
        LocalDateTime baseEnd = rides.get(0).getEndTime();
        Duration duration = Duration.between(baseStart, baseEnd);

        for (int i = 1; i < rides.size(); i++) {
            if(baseEnd.isAfter(rides.get(i).getStartTime()) && rides.get(i).getStartTime().isAfter(baseEnd)){
                baseStart = rides.get(i).getStartTime();
                baseEnd = rides.get(i).getEndTime();
                duration = Duration.of(duration.getSeconds() + Duration.between(baseStart, baseEnd).getSeconds(), ChronoUnit.SECONDS);
            }
            else if(!(baseStart.isEqual(rides.get(i).getStartTime()) && baseEnd.isEqual( rides.get(i).getEndTime()))){
                duration = Duration.of(duration.getSeconds() + Duration.between(rides.get(i).getStartTime(), rides.get(i).getEndTime()).getSeconds(),  ChronoUnit.SECONDS);
                baseStart = rides.get(i).getStartTime();
                baseEnd = rides.get(i).getEndTime();
            }
        }

        return duration;
    }

    public SortedMap<Person, Duration> getSortedRideDurationMap(List<Ride> ridesForTheDuration, Long count) {
        Map<Person, Duration> rideDurationOfDrivers = new HashMap<>();

        ridesForTheDuration.forEach(ride -> {
            if(rideDurationOfDrivers.get(ride.getDriver()) != null) {
                rideDurationOfDrivers.put(ride.getDriver(), Duration.of(rideDurationOfDrivers.get(ride.getDriver()).getSeconds() + ( Duration.between(ride.getStartTime(), ride.getEndTime())).getSeconds(), ChronoUnit.SECONDS));
            }
            else {
                rideDurationOfDrivers.put(ride.getDriver(), Duration.between(ride.getStartTime(), ride.getEndTime()));
            }
        });

        Comparator comparator = new PersonComparatorForMapValue(rideDurationOfDrivers);
        TreeMap<Person, Duration> sortedRideDurationOfDrivers = new TreeMap<Person, Duration>(comparator);

        sortedRideDurationOfDrivers.putAll(rideDurationOfDrivers);
        return subMapByTheLimit(sortedRideDurationOfDrivers, count);
    }

    private  SortedMap<Person, Duration> subMapByTheLimit(TreeMap<Person, Duration> sortedRideDurationOfDrivers, long limit) {
        //Only try put those many record which is required.
        SortedMap<Person, Duration> result = null;
        Object[] keys = sortedRideDurationOfDrivers.keySet().toArray();
        if(sortedRideDurationOfDrivers.size() > limit) {
            result = sortedRideDurationOfDrivers.subMap(sortedRideDurationOfDrivers.firstKey(), (Person) keys[(int) (limit - 1)]);
        }
        else {
            result = sortedRideDurationOfDrivers;
        }
        return result;
    }

}
