// Include the Dropbox SDK.
import com.dropbox.core.*;
import java.io.*;
import java.util.Locale;

public class Hello_02148 {

	// Some global variables
	static DbxAppInfo appInfo;
	static DbxRequestConfig config;
	static DbxClient client;

	// This is a very simple implementation of tuple-based operations for files
	// as tuples
	// Note that we only allow name and extension to be binders
	public class Template {
		String path; // File folder
		String name; // File name (without extension), "?" denotes any value
		String ext; // File extension, "?" denotes any value
		byte[] content; // File content (as byte array)

		public Template(String p, String n, String e, byte[] c) {
			this.path = p;
			this.name = n;
			this.ext = e;
			this.content = c;
		}

		// Blocking operation to check the existence of a file matching a
		// template
		// The template is updated with the matched values for the tuple
		// arguments that were binders (i.e. "?")
		public void qry() throws DbxException, IOException {
			read(false);
		}

		// As qry() but the file is removed from the space
		public void get() throws DbxException, IOException {
			read(true);
		}

		// Auxiliary function that implements the both qry() and get()
		public void read(boolean delete) throws DbxException, IOException {
			DbxEntry.WithChildren listing;
			String name_aux = name;
			String ext_aux = ext;

			// Repeat until one file/tuple is found
			while (true) {
				listing = client.getMetadataWithChildren(path);
				for (DbxEntry child : listing.children) {
					if (name == "?") {
						int j = child.name.lastIndexOf('.');
						if (j == -1)
							continue;
						name_aux = child.name.substring(0, j);
					}
					if (ext == "?") {
						int j = child.name.lastIndexOf('.');
						if (j == -1)
							continue;
						ext_aux = child.name.substring(j + 1);
					}
					if (child.name.equals(name_aux + "." + ext_aux)) {
						name = name_aux;
						ext = ext_aux;
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						client.getFile(child.path, null, out);
						content = out.toByteArray();
						out.close();
						// Delete the file if this is a get()
						if (delete) {
							client.delete(child.path);
						}
						return;
					}
				}
				// Simple implementation based on busy-wait
				// We hence insert a delay of 10 seconds to minimise unsucessful
				// checks
				System.out.println("Blocking operation (qry/get) was unsucessful, sleeping for a while...");
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
				}
			}
		}

		public void put() throws DbxException, IOException {
			ByteArrayInputStream in = new ByteArrayInputStream(content);
			client.uploadFile(path + name + "." + ext, DbxWriteMode.add(), -1, in);
			return;
		}
	}

	public static void main(String[] args) throws IOException, DbxException {
		Hello_02148 me = new Hello_02148();

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
		final String space = "/space";

		// Two tuple templates for the main loop of the app
		Template t1;
		Template t2;

		// Main loop that processes files in the shared space
		while (true) {

			// Template t1 represents "any file in the space folder"
			t1 = me.new Template(space, "?", "?", null);
			System.out.println("Looking for some file...");
			t1.get();
			System.out.println("Found file " + t1.name + "." + t1.ext);

			// t2 is a tuple representing the previously retreived file in a new
			// folder (named after the extension of the file)
			t2 = me.new Template(space + "/" + t1.ext + "/", t1.name, t1.ext, t1.content);
			System.out.println("Putting the file in its corresponding subfolder...");
			t2.put();

		}

	}
}