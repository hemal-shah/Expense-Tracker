package com.hemal.shah;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Java class for supporting conversion of long millis to suitable time format.
 */
public class TimeTravel {

  private static final int SECONDS = 1000;
  private static final int MINUTES = 60000;
  private static final int HOURS = 3600000;

  //static long currentTime = System.currentTimeMillis();

    /*public static void main(String[] args) {

        try {
            System.out.println("one");
            long t = 1483331400000L;
            System.out.println(TimeTravel.getTimeElapsed(currentTime, currentTime + 7200450));
            t = 1446316200000L;
            System.out.println("Two");
            System.out.println(
                    TimeTravel.getTimeElapsed(t, currentTime));
            t = 1481308200000L;
            System.out.println("Three");
            System.out.println(
                    TimeTravel.getTimeElapsed(t, currentTime));
        } catch (TimeTravelException e) {
            e.getMessage();
        }

    }*/

  /**
   * @param start milliseconds when something was created.
   * @param end current system milliseconds
   * @return appropriate time format as String
   */
  public static String getTimeElapsed(long start, long end) throws TimeTravelException {

    if (end < start) {
      throw new TimeTravelException();
    }

    long diff = end - start;

    String answer = "";

    float hours_ago = (float) diff / HOURS;

    if (hours_ago > 24) {
      //Time is more than 24 hours, hence return the string in format of date.
      Date date = new Date(start);
      SimpleDateFormat format = new SimpleDateFormat("dd MMM, YYYY", Locale.ENGLISH);
      answer = format.format(date);
    } else if (hours_ago >= 1 && hours_ago <= 24) {
      answer = ((int) hours_ago) + " Hour ago";
    } else if (hours_ago < 1) {
      float minutes_ago = (float) diff / MINUTES;

      if (minutes_ago >= 1 && minutes_ago <= 60) {
        answer = ((int) minutes_ago) + " Min ago";
      } else if (minutes_ago < 1) {
        int seconds_ago = (int) diff / SECONDS;
        answer = seconds_ago + " Sec ago";
      }
    }
    return answer;
  }
}
