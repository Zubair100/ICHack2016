package ichack.ichack2016.mapping;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Scanner;

/**
 * Created by Thomas on 20/02/2016.
 */
public class getTimeASync extends AsyncTask<getTimeASyncParams, Void, Integer> {

    @Override
    protected Integer doInBackground(getTimeASyncParams... params) {
        URL url = params[0].url;
        Integer result = params[0].result;
        InputStream input = null;


        try {
            input = url.openStream();
        } catch (IOException e) {
            return result;
        }

        Scanner textInput = new Scanner(input);

        while (true) {
            if (!textInput.hasNext()) {
                break;
            } else if (textInput.hasNext("\"duration\"")) {
                while (true) {
                    if (!textInput.hasNext()) {
                        break;
                    } else if (textInput.hasNext("\"value\"")) {
                        textInput.next();
                        textInput.next();
                        result = textInput.nextInt();
                        return result;
                    }

                   textInput.next();
                    }
                }
            textInput.next();
            }
        return result;
    }
}
