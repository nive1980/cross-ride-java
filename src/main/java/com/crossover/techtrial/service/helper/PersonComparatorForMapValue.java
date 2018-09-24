package com.crossover.techtrial.service.helper;

import com.crossover.techtrial.model.Person;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class PersonComparatorForMapValue implements Comparator<Person> {
    Map<Person, Duration> map = new HashMap<Person, Duration>();

    public PersonComparatorForMapValue(Map<Person, Duration> map){
        this.map.putAll(map);
    }


    @Override
    public int compare(Person o1, Person o2) {
      return map.get(o2).compareTo(map.get(o1));

    }
}
