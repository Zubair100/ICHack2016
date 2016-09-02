package ichack.ichack2016.repeatingChecker;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by yianni on 21/02/16.
 */
public class ScheduledChecker {
  private static final int DEFAULT_SCHEDULER_INTENT_ID = 1244;
  private static final int SCHEDULE_ALARM_HOUR = 23;
  private static final int SCHEDULE_ALARM_MINUTE = 35;

  AlarmManager alarmManager;
  PendingIntent pendingReapter;

  public ScheduledChecker(AlarmManager am, Activity activity){
    alarmManager = am;

    Intent intent = new Intent(activity, RepeaterReceiver.class);
    pendingReapter = PendingIntent.getBroadcast(activity,DEFAULT_SCHEDULER_INTENT_ID,intent,0);

  }

  public void createScheduledRepeater(){
    Calendar c = Calendar.getInstance();
    c.setTimeZone(TimeZone.getDefault());
    c.set(Calendar.MINUTE, SCHEDULE_ALARM_MINUTE);
    c.set(Calendar.HOUR_OF_DAY , SCHEDULE_ALARM_HOUR);

    alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,c.getTimeInMillis(),AlarmManager.INTERVAL_DAY,
        pendingReapter);

  }
}
