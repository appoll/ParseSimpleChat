package paul.antton.parsesimplechat;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by Paul's on 03-Jan-15.
 */
public class ChatApplication extends  Application {

    public static final String APPLICATION_ID = "q0oJyDE2qIU7SsPCMhqWYAsjlnVQ477ifAozCqPB";
    public static final String CLIENT_KEY = "MiFa8pGWzdKoV54KpMpIm5016x0CsCQADD9Uxqt4";

    public void onCreate ()
    {
        super.onCreate();

        ParseObject.registerSubclass(Message.class);
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
    }
}
