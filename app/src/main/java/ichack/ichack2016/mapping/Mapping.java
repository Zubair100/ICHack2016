package ichack.ichack2016.mapping;


public abstract class Mapping {

    private String destination;
    private String origin;
    private String travelMode;
    private int defaultTime;

    public Mapping(String destination, String origin, String travelMode, int defaultTime) {
        this.destination = destination;
        this.origin = origin;
        this. travelMode = travelMode;
        this.defaultTime = defaultTime;
    }

    public int getDefaultTime() {
        return defaultTime;
    }

    public String getDestination() {
        return destination;
    }

    public String getOrigin() {
        return origin;
    }

    public String getTravelMode() {
        return travelMode;
    }

    public void setDefaultTime(int defaultTime){
        this.defaultTime = defaultTime;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setTravelMode(String travelMode){
        this.travelMode = travelMode;
    }

    public abstract int getTime();

    public abstract boolean validDestination();

}
