package ichack.ichack2016.mapping;



import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Created by Thomas on 20/02/2016.
 */



public class GoogleMapping extends Mapping {

    public GoogleMapping(String destination, String origin, String travelMode, int defaultTime) {
        super(destination,origin,travelMode, defaultTime);
    }

    private String getURL() {
        return "http://maps.googleapis.com/maps/api/directions/json?origin="
                + getOrigin() + "&destination=" + getDestination().replaceAll(" ", "%20") +
                "&mode=" + getTravelMode();
    }

    @Override
    public int getTime() {

        Integer result = getDefaultTime();

        try {
            URL url = new URL(getURL());
            getTimeASyncParams params = new getTimeASyncParams(result, url);
            return new getTimeASync().execute(params).get();

        } catch (MalformedURLException e) {
          return result;
        } catch (InterruptedException e) {
          return result;
        } catch (ExecutionException e) {
          return result;
        }
    }

    @Override
    public boolean validDestination() {
        try {
            URL url = new URL(getURL());
            boolean result = new isValidASync().execute(url).get();
            return result;

        } catch (MalformedURLException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        } catch (ExecutionException e) {
            return false;
        }
    }

}

