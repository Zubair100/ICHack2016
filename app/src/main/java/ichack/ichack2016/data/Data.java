package ichack.ichack2016.data;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import ichack.ichack2016.calendar.Event;

/**
 * Created by yianni on 20/02/16.
 */
public class Data {

  private static Context context;

  private static String readyTime = "";
  private static String prefTransportMode = "";
  private static Event scheduledEvent = new Event(0, "", "");
  private static String filename = "data.dat";
  private static long alarmTime = 0;
  private static Map<String,String> aliases = new HashMap<>();
  private static boolean fileExists;

  public static void load(Context context) {
    Data.context = context;

    FileInputStream inputStream;
    Scanner input;
    try{
      inputStream = context.openFileInput(filename);
      input = new Scanner(inputStream);

      prefTransportMode = input.nextLine();
      readyTime = input.nextLine();

      String startTime = input.nextLine();
      String location = input.nextLine();
      String title = input.nextLine();
      scheduledEvent = new Event(Long.valueOf(startTime),location,title);

      alarmTime = Long.valueOf(input.nextLine());

      while (input.hasNextLine()) {
        String key = input.nextLine();
        String value = input.nextLine();
        aliases.put(key,value);
        System.out.println("Key: " + key);
        System.out.println(aliases.get(key));
      }

      input.close();
      fileExists = true;
    } catch (IOException e) {
      //ERROR
      System.out.println("Error - date file not found.");
      fileExists = false;
    }
  }

  public static Map<String, String> getMap() {
    return aliases;
  }

  public static void addToMap(String key, String value) {
    aliases.put(key, value);
    save();
  }

  public static Event getScheduledEvent(){
    return scheduledEvent;
  }

  public static String getReadyTime() {
    return readyTime;
  }

  public static String getPrefTransportMode() {
    return prefTransportMode;
  }

  public static long getAlarmTime() { return alarmTime; }

  public static void updateReadyTime(String newReadyTime) {
    readyTime = newReadyTime;
    save();
  }

  public static void updatePrefTransMode(String newMode) {
    prefTransportMode = newMode;
    save();
  }

  public static void updateScheduledEvent(Event event) {
    scheduledEvent = event;
    save();
  }

  public static void updateAlarmTime(long alarmTime) {
    Data.alarmTime = alarmTime;
    save();
  }

  public static void save() {
    FileOutputStream outputStream;
    PrintStream printStream;
    try {
      outputStream = context.openFileOutput(filename, Context.MODE_PRIVATE);
      printStream = new PrintStream(outputStream);

      printStream.println(prefTransportMode);
      printStream.println(readyTime);
      printStream.println(scheduledEvent.getStartTimeMilli());
      printStream.println(scheduledEvent.getLocation());
      printStream.println(scheduledEvent.getTitle());
      printStream.println(alarmTime);
      printStream.close();

      Iterator it = aliases.entrySet().iterator();

      Map<String, String> temp = new HashMap<>();
      while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        printStream.println(pair.getKey());
        printStream.println(pair.getValue());
        temp.put((String) pair.getKey(), (String) pair.getValue());
        it.remove(); // avoids a ConcurrentModificationException
      }
      aliases = temp;

      fileExists = true;

    } catch (IOException e) {
      System.out.println("ERROR - Could not save data.");
    }
  }

  public static boolean exists() {
    return fileExists;
  }

  public static void deleteData() {
    File dataFile = new File(context.getFilesDir().getPath() + "/" + filename);
    dataFile.delete();
    fileExists = false;
    System.exit(0);
  }
}
