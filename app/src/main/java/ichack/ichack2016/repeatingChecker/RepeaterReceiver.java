package ichack.ichack2016.repeatingChecker;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Map;

import ichack.ichack2016.MainActivity;
import ichack.ichack2016.R;
import ichack.ichack2016.calendar.Calendar;
import ichack.ichack2016.calendar.Event;
import ichack.ichack2016.clock.AlarmReceiver;
import ichack.ichack2016.data.Data;
import ichack.ichack2016.mapping.GoogleMapping;
import ichack.ichack2016.mapping.Mapping;

/**
 * Created by yianni on 21/02/16.
 */
public class RepeaterReceiver extends BroadcastReceiver {
  private Event earliest;
  private String endLocation;
  private String transportMode;
  private long eventTimeMS;
  private long getReadyTimeMS;
  private Context context;

  @Override
  public void onReceive(Context context, Intent intent) {

    System.out.println("Alarm set at " + System.currentTimeMillis());
    AlarmReceiver amr = new AlarmReceiver();

    this.context = context;

    if(!Data.exists()){
      return;
    }

    getReadyTimeMS = Integer.valueOf(Data.getReadyTime()) * 60 * 1000;
    transportMode = Data.getPrefTransportMode();
    Calendar cal = new Calendar(context.getContentResolver());
    earliest = cal.getEarliestEvent();

    if(earliest == null) return;
    endLocation = earliest.getLocation();
    eventTimeMS = earliest.getStartTimeMilli();
    Mapping routeTime = new GoogleMapping(endLocation,
        MainActivity.startLat + "," + MainActivity.startLong, transportMode,3600);

    if (!routeTime.validDestination()) {
      Map<String, String> aliases = Data.getMap();
      String value = aliases.get(endLocation);
      if (value != null) {
        routeTime = new GoogleMapping(value,
                MainActivity.startLat + "," + MainActivity.startLong, transportMode, 1200);
      } else {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        View promptView = layoutInflater.inflate(R.layout.dialogprompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.userInput);

        // setup a dialog window
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                  public void onClick(DialogInterface dialog, int id) {
                    runMapTime(input.getText().toString());
                  }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                          public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                          }
                        });

        // create an alert dialog
        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
        return;
      }
    }

    long routeTimeMS = routeTime.getTime() * 1000;

    long alarmTime = eventTimeMS - getReadyTimeMS - routeTimeMS;

    Data.updateScheduledEvent(earliest);
    Data.updateAlarmTime(alarmTime);

    AlarmReceiver am = new AlarmReceiver();
    amr.setAlarm(alarmTime,context);
  }

  private void runMapTime (String newAddress) {
    Data.addToMap(endLocation, newAddress);

    Mapping routeTime = new GoogleMapping(newAddress,
            MainActivity.startLat + "," + MainActivity.startLong, transportMode, 1200);
    routeTime.getTime();
    long routeTimeMS = routeTime.getTime() * 1000;
    long alarmTime = eventTimeMS - getReadyTimeMS - routeTimeMS;


    Data.updateScheduledEvent(earliest);
    Data.updateAlarmTime(alarmTime);

    AlarmReceiver amr = new AlarmReceiver();
    amr.setAlarm(alarmTime,context);
  }
}
