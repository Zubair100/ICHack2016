package ichack.ichack2016.calendar;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.*;

public class Calendar {

    public static final String[] EVENT_PROJECTION = new String[]{
            Events.DTSTART,                // 0
            Events.TITLE,                  // 1
            Events.EVENT_LOCATION,         // 2
            Events.CALENDAR_ID,            //3
    };

    private static final int PROJECTION_EVENT_START_INDEX = 0;
    private static final int PROJECTION_EVENT_TITLE_INDEX = 1;
    private static final int PROJECTION_EVENT_LOCATION_INDEX = 2;
    private static final int PROJECTION_EVENT_CALENDAR_ID_INDEX = 3;

    public static final String[] INSTANCE_PROJECTION = new String[] {
            Instances.START_MINUTE,      //0
            Instances.EVENT_ID           //1
    };

    private static final int PROJECTION_INSTANCE_START_MINUTE_INDEX = 0;
    private static final int PROJECTION_INSTANCE_EVENT_ID_INDEX = 1;

    private ContentResolver contentResolver;

    public Calendar(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public Event getEarliestEvent() {
        long eventID = getEarliestInstanceID();
        return eventID != -1 ? getEvent(eventID) : null;
    }

    private long getEarliestInstanceID() {
        Cursor cur = null;
        ContentResolver cr = contentResolver;
        Uri uri = Instances.CONTENT_URI;
        String selection = "((" + Instances.START_MINUTE + ") AND ("
                + Instances.EVENT_ID + "))";

        // Submit the query and get a Cursor object back.

        long timeOffset = System.currentTimeMillis() % 86400000;
        long dayStart = System.currentTimeMillis() - timeOffset + 86400000;
        long dayEnd = dayStart + 86400000;
        cur = Instances.query(cr, INSTANCE_PROJECTION, dayStart, dayEnd);

        int currentEarliestTime = Integer.MAX_VALUE;
        long currentEarliestTimeID = -1;

        while (cur.moveToNext()) {
            int startTimeMin;
            long eventID;

            // Get the field values
            startTimeMin = cur.getInt(PROJECTION_INSTANCE_START_MINUTE_INDEX);
            eventID = cur.getLong(PROJECTION_INSTANCE_EVENT_ID_INDEX);
            if (startTimeMin < currentEarliestTime) {
                currentEarliestTime = startTimeMin;
                currentEarliestTimeID = eventID;
            }
        }
        return currentEarliestTimeID;
    }

    private Event getEvent(long event_id) {

        Cursor cur = null;
        ContentResolver cr = contentResolver;
        Uri uri = Events.CONTENT_URI;
        String selection = "((" + Events._ID + " = ?))";

        String[] selectionArgs = new String[] {String.valueOf(event_id)};
        // Submit the query and get a Cursor object back.
        try {
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        }catch (SecurityException e) {
            //Bad exceptions.
            System.out.println("ERROR!");
        }

        long startTime = 0;
        String title = null;
        String eventLocation = null;
        long calID = 0;

        while (cur.moveToNext()) {
            // Get the field values
            startTime       = cur.getLong(PROJECTION_EVENT_START_INDEX);
            title           = cur.getString(PROJECTION_EVENT_TITLE_INDEX);
            eventLocation   = cur.getString(PROJECTION_EVENT_LOCATION_INDEX);
            calID           = cur.getLong(PROJECTION_EVENT_CALENDAR_ID_INDEX);
        }
        cur.close();
        return event_id != -1 ? new Event(startTime, eventLocation, title) : null;
    }
}
