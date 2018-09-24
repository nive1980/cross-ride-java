package com.crossover.techtrial.businessrule;

import com.crossover.techtrial.exceptions.CrossRideBusinessException;

import java.util.HashMap;
import java.util.Map;

public interface BusinessRule<T> {
    void execute(T t) throws CrossRideBusinessException ;
}
