package com.crossover.techtrial.controller;

import com.crossover.techtrial.dto.TopDriverDTO;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.PersonRepository;
import com.crossover.techtrial.repositories.RideRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RideControllerTest {

    MockMvc mockMvc;

    @Mock
    private RideController rideController;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    PersonRepository personRepository;

    @Autowired
    RideRepository rideRepository;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
        rideRepository.deleteAll();
        personRepository.deleteAll();
    }

    @Test
    public void registered_user_should_be_able_to_create_a_ride() {
        //Register users
        Person person1 = new Person();
        person1.setName("Ashutosh Singh");
        person1.setEmail("agate.ashu@gmail.com");
        person1.setRegistrationNumber("0509809617");

        Person response1 = template.postForObject("/api/person", person1, Person.class);
        assertNotNull(response1.getId());

        Person person2 = new Person();
        person2.setName("Abhishek Singh");
        person2.setEmail("abhishek.singh@gmail.com");
        person2.setRegistrationNumber("055564895");

        Person response2 = template.postForObject("/api/person", person2, Person.class);
        assertNotNull(response2.getId());


        //Create a ride
        Ride ride = new Ride();
        LocalDateTime startDateTime = LocalDateTime.parse("2018-09-18T21:15:38");
        LocalDateTime endDateTime = startDateTime.plus(1, ChronoUnit.HOURS);

        ride.setDistance(20L);
        ride.setDriver(response1);
        ride.setRider(response2);
        ride.setStartTime(startDateTime);
        ride.setEndTime(endDateTime);

        Ride rideResponse = template.postForObject("/api/ride", ride, Ride.class);

        //Verify
        //Fetch created ride from API
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        Ride response = template.getForObject("/api/ride/"+rideResponse.getId(), Ride.class);

        assertNotNull(response.getId());
        assertEquals("Ashutosh Singh", response.getDriver().getName());
        assertEquals((Long) 20L, response.getDistance());
        assertEquals(startDateTime.format(formatter), response.getStartTime().format(formatter));
        assertEquals(endDateTime.format(formatter), response.getEndTime().format(formatter));


    }

    @Test
    public void user_try_to_create_ride_where_end_date_is_less_than_start_date() {
        //Register users
        Person person1 = new Person();
        person1.setName("Ashutosh Singh");
        person1.setEmail("agate.ashu@gmail.com");
        person1.setRegistrationNumber("0509809617");

        Person response1 = template.postForObject("/api/person", person1, Person.class);
        assertNotNull(response1.getId());

        Person person2 = new Person();
        person2.setName("Abhishek Singh");
        person2.setEmail("abhishek.singh@gmail.com");
        person2.setRegistrationNumber("055564895");

        Person response2 = template.postForObject("/api/person", person2, Person.class);
        assertNotNull(response2.getId());


        //Create a ride
        Ride ride = new Ride();
        LocalDateTime startDateTime = LocalDateTime.now();
        LocalDateTime endDateTime = startDateTime.minus(1, ChronoUnit.MINUTES);

        ride.setDistance(20L);
        ride.setDriver(response1);
        ride.setRider(response2);
        ride.setStartTime(startDateTime);
        ride.setEndTime(endDateTime);

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<Ride> requestEntity = new HttpEntity<>(ride, requestHeaders);

        ResponseEntity<Map<String, String>> response = template.exchange("/api/ride", HttpMethod.POST, requestEntity,  new ParameterizedTypeReference<Map<String, String>>(){});

        assertEquals(400, response.getStatusCode().value());
        assertEquals(response.getBody().get("INVALID_DATA"), "End time can not be less than or equal to start time");
    }

    @Test
    public void user_should_be_able_to_list_top_drivers() {
        //Register users
        Person person1 = new Person();
        person1.setName("Ashutosh Singh");
        person1.setEmail("agate.ashu@gmail.com");
        person1.setRegistrationNumber("0509809617");

        Person response1 = template.postForObject("/api/person", person1, Person.class);
        assertNotNull(response1.getId());

        Person person2 = new Person();
        person2.setName("Abhishek Singh");
        person2.setEmail("abhishek.singh@gmail.com");
        person2.setRegistrationNumber("055564895");

        Person response2 = template.postForObject("/api/person", person2, Person.class);
        assertNotNull(response2.getId());

        //Crete few rides
        //Create Ride - 1
        Ride ride1 = new Ride();
        LocalDateTime startDateTime1 = LocalDateTime.now();
        LocalDateTime endDateTime1 = LocalDateTime.now().plus(1, ChronoUnit.HOURS);

        ride1.setDistance(20L);
        ride1.setDriver(response1);
        ride1.setRider(response1);
        ride1.setStartTime(startDateTime1);
        ride1.setEndTime(endDateTime1);

        Ride rideResponse1 = template.postForObject("/api/ride", ride1, Ride.class);

        //Create Ride - 2
        Ride ride2 = new Ride();
        LocalDateTime startDateTime2 = LocalDateTime.now().plus(2, ChronoUnit.HOURS);
        LocalDateTime endDateTime2 = startDateTime2.plus(3, ChronoUnit.HOURS);

        ride2.setDistance(30L);
        ride2.setDriver(response1);
        ride2.setRider(response2);
        ride2.setStartTime(startDateTime2);
        ride2.setEndTime(endDateTime2);

        Ride rideResponse2 = template.postForObject("/api/ride", ride2, Ride.class);

        //Create Ride - 3 (created with different driver than other two)
        Ride ride3 = new Ride();
        LocalDateTime startDateTime3 = LocalDateTime.now().plus(4, ChronoUnit.HOURS);
        LocalDateTime endDateTime3 = startDateTime3.plus(2, ChronoUnit.HOURS);

        ride3.setDistance(40L);
        ride3.setDriver(response2);
        ride3.setRider(response1);
        ride3.setStartTime(startDateTime3);
        ride3.setEndTime(endDateTime3);

        Ride rideResponse3 = template.postForObject("/api/ride", ride3, Ride.class);

        //fetch top 2 drivers
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String queryFromDate = LocalDateTime.now().minus(1, ChronoUnit.DAYS).format(formatter);
        String queryToDate = LocalDateTime.now().plus(1, ChronoUnit.DAYS).format(formatter);
        int count = 2;
        ResponseEntity<List<TopDriverDTO>> response =  template.exchange("/api/top-rides?max="+count+"&startTime="+queryFromDate+ "&endTime="+queryToDate, HttpMethod.GET, null,  new ParameterizedTypeReference<List<TopDriverDTO>>(){});

        List<TopDriverDTO> result = response.getBody();

        assertEquals(count, result.size());

        assertEquals(result.get(0).getName(), "Ashutosh Singh");
        assertEquals(result.get(0).getTotalRideDurationInSeconds(), (Long)14400L);
        assertEquals(result.get(0).getMaxRideDurationInSecods(), (Long)10800L);

        assertEquals(result.get(1).getName(), "Abhishek Singh");
        assertEquals(result.get(1).getTotalRideDurationInSeconds(), (Long)7200L);
        assertEquals(result.get(1).getMaxRideDurationInSecods(), (Long)7200L);
    }
}
