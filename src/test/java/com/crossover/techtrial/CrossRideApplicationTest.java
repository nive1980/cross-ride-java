/**
 * 
 */
package com.crossover.techtrial;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.crossover.techtrial.controller.PersonController;
import com.crossover.techtrial.controller.RideController;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.model.Ride;
import com.crossover.techtrial.repositories.RideRepository;
/**
 * @author crossover
 *
 */
public class CrossRideApplicationTest {
	MockMvc mockMvc;
	@Autowired
	private TestRestTemplate template;

	@Autowired
	RideRepository rideRepository;

	@Mock
	private RideController rideController;

	@BeforeClass
	public void beforeClass() {
		System.out.println("start All Test Cases");
	}

	@Before
	public void setup() throws Exception {
		mockMvc = MockMvcBuilders.standaloneSetup(rideController).build();
		System.out.println("start of Test Case");
	}

	@Test
	public void testPanelShouldBeRegistered() throws Exception {

		HttpEntity<Object> ride = getHttpEntity(
				"{\"startTime\": \"2018-08-08T01:01:01\", \"endTime\": \"2018-08-08T05:05:05\","
						+ " \"distance\": \"5\",\"driver\":\" {\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\","
						+ " \"registrationNumber\": \"41DCT\",\"registrationDate\":\"2018-08-08T12:12:12\" } ,\"rider\":\" {\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\","
						+ " \"registrationNumber\": \"41DCT\",\"registrationDate\":\"2018-08-08T12:12:12\" } \" }");
		ResponseEntity<Ride> response = template.postForEntity("/api/ride", ride, Ride.class);
		// Delete this user
		rideRepository.deleteById(response.getBody().getId());
		Assert.assertEquals("shimaa", response.getBody().getDriver().getName());
		Assert.assertEquals(200, response.getStatusCode().value());
		// Test Create Ride also w can test save validations with try different startDate and endDate 
		assertEquals("error in Create ride", response.getBody(), rideController.createNewRide(response.getBody()));
		assertEquals("error in get all rides", 3, rideController.getAllRides().getBody().size());

	}

	private HttpEntity<Object> getHttpEntity(Object body) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return new HttpEntity<Object>(body, headers);
	}

	@After
	public void after() {

		System.out.println("End of Test Case");

	}

	@AfterClass
	public void afterClass() {

		System.out.println("End All Test Case");

	}
}
