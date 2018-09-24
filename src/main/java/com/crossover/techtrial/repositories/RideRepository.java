/**
 * 
 */
package com.crossover.techtrial.repositories;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import org.hibernate.sql.Select;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;


/**
 * @author crossover
 *
 */
@RestResource(exported = false)
public interface RideRepository extends CrudRepository<Ride, Long> {
    @Query(value = "SELECT *, SEC_TO_TIME(SUM((TIME_TO_SEC(timediff(end_time, start_time))))) as total_duration FROM ride \n" +
            "where start_time<= :endDateTime and end_time>= :startDateTime group by driver_id order by total_duration desc limit :count", nativeQuery = true)
   List<Ride> findTopDriverForDuration(@Param("count") Long count, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    List<Ride> findAllByEndTimeGreaterThanAndStartTimeLessThan(LocalDateTime startDateTime, LocalDateTime endDateTime);

    List<Ride> findAllByDriver(Person driver);

}
