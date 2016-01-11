import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;
import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

public class Main {

	// Some global variables
	static DbxAppInfo appInfo;
	static DbxRequestConfig config;
	static DbxClient client;
	static String[] paths = {"C:\\Users\\Mads\\Pictures\\Imgur\\","C:\\Users\\Frederik\\Desktop\\collages\\","/Users/Kasper/Pictures/imgur"};
	static String path = paths[0];

	public static void main(String[] args) throws IOException, DbxException {

		// Get your app key and secret from the Dropbox developers website and
		// insert them below
		final String APP_KEY = "6gm4dggg0pm4qd3";
		final String APP_SECRET = "mnm27mqi749672i";
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		config = new DbxRequestConfig("JavaTutorial/1.0ab", Locale.getDefault().toString());

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
			// folder
			if (isPicture(t1.type)) {
				t2 = new Template(space + "/pics/", t1.name, t1.ext, t1.content);
				System.out.println("Putting the file in the 'picture' subfolder");
			} else if (isText(t1.type)) {
				t2 = new Template(space + "/text/", t1.name, t1.ext, t1.content);
				System.out.println("Putting the file in the 'text' subfolder");
			} else {
				t2 = new Template(space + "/others/", t1.name, t1.ext, t1.content);
				System.out.println("Putting the file in the 'others' subfolder...");
			}
			t2.put(client);

			readTextCommands();
		}

	}

	// reads the .txt file command
	public static void readTextCommands() throws DbxException, IOException {
		String path = "/space/collage/text";
		DbxEntry.WithChildren listing = client.getMetadataWithChildren(path);

		for (DbxEntry child : listing.children) {
			switch (child.name) {
			case "create.txt":
				// data is a list of structure [Collagename, picname 1, ...,
				// picname N]
				ArrayList<String> data = readCreateCommand(path, child);
				System.out.println(data);
				
				// collage code here

				// deletes the command file after executing it
				delete(child);
				break;

			case "move.txt":
				// code here
				break;

			case "delete.txt":
				delete(readDeleteCommand(path, child));
				delete(child);
				break;

			case "upload.txt":
				// imgur upload method here
				break;

			default:
				// code here
				break;
			}
		}

	}

	// return amount of files in the picture folder
	public static int pictureAmount() throws DbxException {
		String path = "/space/collage/pics";
		return client.getMetadataWithChildren(path).children.size();
	}

	// whether or not a file is either a .png, .jpg or .jpeg file
	private static boolean isPicture(String s) {
		if (s.equals("PNG") || s.equals("JPG") || s.equals("JPEG") || s.equals("png") || s.equals("jpg")
				|| s.equals("jpeg")) {
			return true;
		}
		return false;
	}

	// whether or not a file is a .txt file
	private static boolean isText(String s) {
		if (s.equals("txt") || s.equals("TXT")) {
			return true;
		}
		return false;
	}

	private static void delete(DbxEntry file) throws DbxException {
		client.delete(file.path);
	}

	private static ArrayList<String> readCreateCommand(String path, DbxEntry child) throws DbxException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		client.getFile(path + "/" + child.name, null, out);
		// putting the ByteArrayOutputStream in the scanner as a String
		Scanner sc = new Scanner(out.toString());
		// making an ArrayList holding the name and filenames
		ArrayList<String> data = new ArrayList<String>();
		// the name of the collage
		data.add(sc.nextLine());
		int amount = sc.nextInt();
		sc.nextLine();
		for (int i = 0; i < amount; i++) {
			data.add(sc.nextLine());
		}
		out.close();
		sc.close();
		return data;
	}

	private static DbxEntry readDeleteCommand(String path, DbxEntry child) throws DbxException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		client.getFile(path + "/" + child.name, null, out);
		Scanner sc = new Scanner(out.toString());
		String name = sc.nextLine();
		String folderPath = sc.nextLine();
		out.close();
		sc.close();
		DbxEntry file = client.getMetadata("/space" + folderPath + "/" + name);
		// the file to be deleted is returned
		return file;
	}

}
