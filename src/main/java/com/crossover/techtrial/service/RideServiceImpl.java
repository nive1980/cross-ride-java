/**
 * 
 */
package com.crossover.techtrial.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.crossover.techtrial.businessrule.RideBusinessRule;
import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;
import com.crossover.techtrial.service.helper.RiderServiceHelper;

/**
 * @author crossover
 *
 */
@Service
public class RideServiceImpl implements RideService{

  @Autowired
  private RideRepository rideRepository;
  @Autowired
  private RideBusinessRule rideBusinessRule;
  @Autowired
  private RiderServiceHelper riderServiceHelper;
  public Ride save(Ride ride) {
	  //Business rule to check ride
	    rideBusinessRule.execute(ride);
	    return rideRepository.save(ride); }
  
  public Ride findById(Long rideId) {
    Optional<Ride> optionalRide = rideRepository.findById(rideId);
    if (optionalRide.isPresent()) {
      return optionalRide.get();
    }else return null;
  }

  public List<TopDriverDTO> findTopDrivers(Long count, LocalDateTime startTime, LocalDateTime endTime){


	    List<Ride> ridesForTheDuration = rideRepository.findAllByEndTimeGreaterThanAndStartTimeLessThan(startTime, endTime);

	    //Calculate total duration of rides for each driver in sorted order by duration
	    SortedMap<Person, Duration>  driverDurationMap = riderServiceHelper.getSortedRideDurationMap(ridesForTheDuration, count);

	    List<TopDriverDTO> topDriverDTOS = new ArrayList<>();

	    driverDurationMap.forEach((person, duration) -> {
	      TopDriverDTO topDriverDTO = new TopDriverDTO();
	      topDriverDTO.setName(person.getName());
	      topDriverDTO.setEmail(person.getEmail());
	      topDriverDTO.setTotalRideDurationInSeconds(riderServiceHelper.getTotalDurationIgnoringOverlap(rideRepository.findAllByDriver(person)).getSeconds());
	      topDriverDTO.setMaxRideDurationInSecods(riderServiceHelper.getMaxRideDuration(person).getSeconds());
	      //topDriverDTO.setAverageDistance();
	      topDriverDTOS.add(topDriverDTO);
	    });

	    return topDriverDTOS;
	  }

}
