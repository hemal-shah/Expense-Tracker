package com.hemal.shah;

/**
 * Exception indicating that TimeTravel is not possible.
 * Created by hemal on 4/1/17.
 */

public class TimeTravelException extends Exception {

    public TimeTravelException(){
        System.out.println("The end time should be larger than start time.");
    }

    public TimeTravelException(String s) {
        super(s);
        System.out.println("The end time should be larger than start time.");
    }
}
