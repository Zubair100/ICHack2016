package ichack.ichack2016.calendar;

/**
 * Created by yianni on 20/02/16.
 */
public class Event {

    private final long startTimeMilli;
    private final String location;
    private final String title;



    public Event(long startTimeMilli, String location, String title) {
        this.startTimeMilli = startTimeMilli;
        this.location = location;
        this.title = title;

    }

    public long getStartTimeMilli() {
        return startTimeMilli;
    }

    public String getLocation() {
        return location;
    }


    public String getTitle() {
        return title;
    }
}
