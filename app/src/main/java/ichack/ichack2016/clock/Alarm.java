package ichack.ichack2016.clock;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;


/**
 * Created by shiraz on 20/02/16.
 */
public class Alarm{
    AlarmManager alarmManager;
    PendingIntent pendingSetAlarmIntent;

    public Alarm(AlarmManager systemService, Activity activity){
        alarmManager = systemService;
        Intent setAlarmIntent = new Intent(activity, AlarmReceiver.class);
        pendingSetAlarmIntent = PendingIntent.getBroadcast(activity, 0,
                setAlarmIntent, 0);
    }

    public void setAlarm(long timeInMilliseconds){
        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInMilliseconds,
                pendingSetAlarmIntent);
    }

    // may need to pass in activity (if we need to remove the alarm intent)
    public static void stopAlarm() {
        // test: Toast.makeText(activity, "Stopping Alarm!", Toast.LENGTH_LONG)
        // .show();
        if(AlarmReceiver.getRingtone() == null){
            return;
        }
        AlarmReceiver.getRingtone().stop();
    }
}
