package com.appsinventiv.mrappliancestaff.Activities.Calender;

import java.util.Calendar;

/**
 * Created by Raquib on 1/6/2015.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
    String interpretWeek(int date);
}
