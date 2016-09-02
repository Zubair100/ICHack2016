package ichack.ichack2016.mapping;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Thomas on 21/02/2016.
 */
public class isValidASync extends AsyncTask<URL, Void, Boolean> {

    @Override
    protected Boolean doInBackground(URL... params) {
        URL url = params[0];
        InputStream input = null;


        try {
            input = url.openStream();
        } catch (IOException e) {
            return false;
        }

        Scanner textInput = new Scanner(input);

        while(textInput.hasNext()) {
            if (textInput.hasNext("\"ZERO_RESULTS\"")) {
                return false;
            }
            textInput.next();
        }
        return true;
    }
}
