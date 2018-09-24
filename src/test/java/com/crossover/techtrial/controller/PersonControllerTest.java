/**
 * 
 */
package com.crossover.techtrial.controller;

import com.crossover.techtrial.repositories.RideRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.crossover.techtrial.model.Person;
import com.crossover.techtrial.repositories.PersonRepository;

import javax.validation.constraints.Null;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author kshah
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PersonControllerTest {
  
  MockMvc mockMvc;
  
  @Mock
  private PersonController personController;
  
  @Autowired
  private TestRestTemplate template;
  
  @Autowired
  PersonRepository personRepository;

  @Autowired
  RideRepository rideRepository;
  
  @Before
  public void setup() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(personController).build();
    rideRepository.deleteAll();
    personRepository.deleteAll();
  }
  
  @Test
  public void testPanelShouldBeRegistered() throws Exception {
    HttpEntity<Object> person = getHttpEntity(
        "{\"name\": \"test 1\", \"email\": \"test10000000000001@gmail.com\"," 
            + " \"registrationNumber\": \"41DCT\",\"registrationDate\":\"2018-08-08T12:12:12\" }");
    ResponseEntity<Person> response = template.postForEntity(
        "/api/person", person, Person.class);
    //Delete this user
    personRepository.deleteById(response.getBody().getId());
    assertEquals("test 1", response.getBody().getName());
    assertEquals(200,response.getStatusCode().value());
  }

  private HttpEntity<Object> getHttpEntity(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new HttpEntity<Object>(body, headers);
  }

  @Test
  public void user_should_be_able_to_fetch_a_registered_person(){
    //Register a user
     Person person = new Person();
     person.setName("Ashutosh Singh");
     person.setEmail("agate.ashu@gmail.com");
     person.setRegistrationNumber("0509809617");

    Person response = template.postForObject("/api/person", person, Person.class);
    //Verify user registration
    assertEquals("Ashutosh Singh", response.getName());
    assertNotNull(response.getId());

    //fetch user detail through API
    Person fetchedPerson = template.getForObject("/api/person/" + response.getId(), Person.class);
    //Verify
    assertEquals("Ashutosh Singh", fetchedPerson.getName());
    assertNotNull(fetchedPerson.getId());

  }

  @Test
  public void user_should_get_exception_when_query_with_bad_data(){
    //Register a user
    Person person = new Person();
    person.setName("Ashutosh Singh");
    person.setEmail("agate.ashu@gmail.com");
    person.setRegistrationNumber("0509809617");

    Person response = template.postForObject("/api/person", person, Person.class);
    //Verify user registration
    assertEquals("Ashutosh Singh", response.getName());
    assertNotNull(response.getId());

    ResponseEntity<Map<String, String>> result = template.exchange("/api/person/" + "Ashu", HttpMethod.GET, null,  new ParameterizedTypeReference<Map<String, String>>(){});

    assertEquals(400, result.getStatusCode().value());
    assertEquals(result.getBody().get("message"), "Unable to process this request.");
  }

  @Test
  public void user_want_to_list_all_persons(){
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

    ResponseEntity<List<Person>> response  = template.exchange("/api/person", HttpMethod.GET, null,  new ParameterizedTypeReference<List<Person>>(){});

    //verify
    assertNotNull(response.getBody().get(0).getId());
    assertNotNull(response.getBody().get(1).getId());
    assertEquals(2, response.getBody().size());

  }

}
