package com.crossover.techtrial.exceptions;

import java.util.HashMap;
import java.util.Map;

public class CrossRideBusinessException extends RuntimeException {
    private Map<String, String> businessExceptions;

    public CrossRideBusinessException(Map exceptions) {
        this.businessExceptions = exceptions;
    }

    public CrossRideBusinessException(String code, String message) {
        this.businessExceptions = new HashMap<>();
        this.businessExceptions.put(code, message);
    }

    public Map<String, String> getBusinessExceptions() {
        return businessExceptions;
    }
}
