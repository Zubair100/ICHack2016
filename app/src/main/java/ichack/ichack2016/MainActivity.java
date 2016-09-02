package ichack.ichack2016;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import ichack.ichack2016.calendar.Calendar;
import ichack.ichack2016.calendar.Event;
import ichack.ichack2016.clock.Alarm;
import ichack.ichack2016.clock.AlarmReceiver;
import ichack.ichack2016.data.Data;
import ichack.ichack2016.mapping.GoogleMapping;
import ichack.ichack2016.mapping.Mapping;
import ichack.ichack2016.repeatingChecker.ScheduledChecker;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

  private GoogleApiClient mGoogleApiClient;
  private Location mLastLocation;
  public static double startLat;
  public static double startLong;
  private String newAddress;
  private String transportMode;
  private String endLocation;
  private long eventTimeMS;
  private Event earliest;
  private long getReadyTimeMS;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Data.load(this);
    if (Data.exists()) {
      setContentView(R.layout.activity_main);
    } else {
      optionsMenu();
    }

    // Create an instance of GoogleAPIClient.
    if (mGoogleApiClient == null) {
      mGoogleApiClient = new GoogleApiClient.Builder(this)
              .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
              .addApi(LocationServices.API)
              .build();
    }

  }

  @Override
  protected void onStart() {
    super.onStart();
    if (Data.exists()) updateUI();
    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mGoogleApiClient.disconnect();
  }

  private void optionsMenu() {
    setContentView(R.layout.activity_first_run);
    Spinner spinner = (Spinner) findViewById(R.id.transportspinner);
// Create an ArrayAdapter using the string array and a default spinner layout
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
            R.array.transports_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
    spinner.setAdapter(adapter);


    Button saveChanges = (Button) findViewById(R.id.save_changes);
    saveChanges.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Spinner spinner = (Spinner) findViewById(R.id.transportspinner);
        EditText readyTime = (EditText) findViewById(R.id.getReadyMin);
        Data.updatePrefTransMode((String) spinner.getSelectedItem());
        Data.updateReadyTime(readyTime.getText().toString());
        setContentView(R.layout.activity_main);

        setAlarm();
        updateUI();
      }
    });

  }

  private ScheduledChecker getScheduleChecker() {
    return new ScheduledChecker((AlarmManager) this.getSystemService(ALARM_SERVICE), this);
  }


  public void setAlarm() {

    getReadyTimeMS = Integer.valueOf(Data.getReadyTime()) * 60 * 1000;
    transportMode = Data.getPrefTransportMode();

    Calendar cal = new Calendar(this.getContentResolver());
    earliest = cal.getEarliestEvent();
    if (earliest == null) return;
    endLocation = earliest.getLocation();
    eventTimeMS = earliest.getStartTimeMilli();
    Mapping routeTime = new GoogleMapping(endLocation,
            startLat + "," + startLong, transportMode, 1200);

    if (!routeTime.validDestination()) {
      Map<String, String> aliases = Data.getMap();
      String value = aliases.get(endLocation);
      if (value != null) {
        routeTime = new GoogleMapping(value,
                startLat + "," + startLong, transportMode, 1200);
      } else {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(this);

        View promptView = layoutInflater.inflate(R.layout.dialogprompt, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set prompts.xml to be the layout file of the alertdialog builder
        alertDialogBuilder.setView(promptView);

        final EditText input = (EditText) promptView.findViewById(R.id.userInput);
        final TextView label = (TextView) promptView.findViewById(R.id.titleText);
        label.setText("Could not find " + endLocation + ". Please enter an addesss to associate with this location.");

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
                            runMapTime(endLocation);
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

    ScheduledChecker sc = getScheduleChecker();
    sc.createScheduledRepeater();


  }

  public void runMapTime (String newAddress) {
    Data.addToMap(endLocation, newAddress);

    Mapping routeTime = new GoogleMapping(newAddress,
            startLat + "," + startLong, transportMode, 1200);
    routeTime.getTime();
    long routeTimeMS = routeTime.getTime() * 1000;
    long alarmTime = eventTimeMS - getReadyTimeMS - routeTimeMS;

    Data.updateScheduledEvent(earliest);
    Data.updateAlarmTime(alarmTime);

    ScheduledChecker sc = getScheduleChecker();
    sc.createScheduledRepeater();

    updateUI();
  }

  public void updateUI() {
    TextView title = (TextView) findViewById(R.id.eventtitle);
    TextView time = (TextView) findViewById(R.id.eventtime);
    TextView date = (TextView) findViewById(R.id.eventdate);
    TextView location = (TextView) findViewById(R.id.eventlocation);
    TextView alarmTime = (TextView) findViewById(R.id.alarmtime);
    title.setText(Data.getScheduledEvent().getTitle());

    Date timeD = new Date(Data.getScheduledEvent().getStartTimeMilli());
    DateFormat formatterT = new SimpleDateFormat("HH:mm");
    String timeFormatted = formatterT.format(timeD);
    time.setText(timeFormatted);

    Date dateC = new Date(Data.getScheduledEvent().getStartTimeMilli());
    DateFormat formatterD = new SimpleDateFormat("dd MMM yy");
    String dateFormatted = formatterD.format(dateC);
    date.setText(dateFormatted);


    location.setText(Data.getScheduledEvent().getLocation());

    Date dateA = new Date(Data.getAlarmTime());
    DateFormat formatterA = new SimpleDateFormat("HH:mm");
    String alarmFormatted = formatterA.format(dateA);

    alarmTime.setText(alarmFormatted);
  }

  public void resetSettings(View view) {
    Data.deleteData();
  }

  public void stopAlarm(View view) {
    System.out.println("Still working!");
    Alarm.stopAlarm();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onConnected(Bundle bundle) {
    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (mLastLocation != null) {
      startLat = mLastLocation.getLatitude();
      startLong = mLastLocation.getLongitude();
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  @Override
  public void onConnectionFailed(ConnectionResult connectionResult) {

  }
}
