import java.io.IOException;
import java.util.Locale;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

public class Main {

	// Some global variables
		//PUSH TEST
		static DbxAppInfo appInfo;
		static DbxRequestConfig config;
		static DbxClient client;

		public static void main(String[] args) throws IOException, DbxException {

			// Get your app key and secret from the Dropbox developers website and
			// insert them below
			final String APP_KEY = "6gm4dggg0pm4qd3";
			final String APP_SECRET = "mnm27mqi749672i";
			appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
			config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());

			// Insert here the access token from your app.
			final String accessToken = "2IXvlsnWAFAAAAAAAAAAEPhoKJM-eyCMjv3hmLJncYB_x536trI0mGHg3U-OIYep";

			client = new DbxClient(config, accessToken);

			// Name of the shared folder to be used as shared space
			final String space = "/space/collage";

			// Two tuple templates for the main loop of the app
			Template t1;
			Template t2;

			// Main loop that processes files in the shared space
			while (true) {

				// Template t1 represents "any file in the space folder"
				t1 = new Template(space, "?", "?", null);
				System.out.println("Looking for some file...");
				t1.get(client);
				System.out.println("Found file " + t1.name + "." + t1.ext);

				// t2 is a tuple representing the previously retreived file in a new
				// folder (named after the extension of the file)
				if (t1.supported){
					t2 = new Template(space + "/pics/", t1.name, t1.ext, t1.content);
					System.out.println("Putting the file in the picture subfolder");
					t2.put(client);
				} else {
					t2 = new Template(space + "/others/", t1.name, t1.ext, t1.content);
					System.out.println("Putting the file in the 'other' subfolder...");
					t2.put(client);
				}
				

			}

		}
		
		public static DbxClient getClient() {
			return client;
		}
	
}
