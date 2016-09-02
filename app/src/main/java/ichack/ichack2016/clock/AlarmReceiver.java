package ichack.ichack2016.clock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;


/**
 * Created by shiraz on 20/02/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    private static final String toastMsg = "Wake Up!";

    private static Ringtone ringtone;

    @Override
    public void onReceive(Context context, Intent intent) {
        // test: Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(context, uri);

        ringtone.play();
    }

    static Ringtone getRingtone() {
        return ringtone;
    }

    public void setAlarm(long timeInMilliseconds, Context context){
        if(timeInMilliseconds<0){
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context,AlarmReceiver.class);
        PendingIntent pendingSetAlarmIntent = PendingIntent.getBroadcast(context,0,i,0);
        alarmManager.set(AlarmManager.RTC_WAKEUP,timeInMilliseconds,pendingSetAlarmIntent);
    }
}
