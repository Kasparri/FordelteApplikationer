import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import com.dropbox.core.DbxAppInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxWriteMode;

public class Main {

	// Some global variables
	static DbxAppInfo appInfo;
	static DbxRequestConfig config;
	static DbxClient client;
	static String path = "./images/";

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

		List<String> images = ImgurConnecter.getImgsFromSite("http://imgur.com/t/archer");
		List<String> imgfiles = new ArrayList<String>();
		for (String img : images) {
			imgfiles.add(ImgurConnecter.downloadFromImgur(img));
		}
		System.out.println("collected images");

		String image = "http://i.imgur.com/0IDhAcc.jpg";
		ImgurConnecter.downloadFromImgur(image);

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
		String textpath = "/space/collage/text";
		DbxEntry.WithChildren listing = client.getMetadataWithChildren(textpath);

		for (DbxEntry child : listing.children) {
			switch (child.name) {
			case "create.txt":
				// data is a list of structure [Collagename, picname 1,
				// ...,picname N]
				ArrayList<String> data = readCreateCommand(textpath, child);
				System.out.println(data);

				// collage code here
				Collage.collagename = data.get(0);
				Collage.collagename = Collage.collagename.substring(0, Collage.collagename.length() - 4);
				data.remove(0);
				for (int i = 0; i < data.size(); i++) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					client.getFile("/space/collage/pics/" + data.get(i), null, out);
					byte[] fileArray = out.toByteArray();
					String newpath = path + data.get(i);
					FileOutputStream fos = new FileOutputStream(newpath);
					fos.write(fileArray);
					fos.close();
					data.set(i, newpath);
				}
				Collage.multi(data);
				File collage = new File(path + Collage.finalName);

				try {
					FileInputStream inputStream = new FileInputStream(collage);
					DbxEntry.File uploadedFile = Main.client.uploadFile("/space/collage/collages/" + Collage.finalName,
							DbxWriteMode.add(), collage.length(), inputStream);

					System.out.println("Uploaded: " + uploadedFile.toString());
					inputStream.close();
					collage.delete();
					collage.deleteOnExit();

				} catch (DbxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Deleting the command file 'create.txt'");
				delete(child);
				break;

			case "move.txt":
				// fetches the fromPath and toPath from the move.txt text file
				String[] paths = fetchMovePaths(textpath, child);
				System.out.println("Moving the file at placement: '" + paths[0] + "', to: '" + paths[1] + "'");
				client.move(paths[0], paths[1]);
				System.out.println("Deleting the command file 'move.txt'");
				delete(child);
				break;

			case "delete.txt":
				DbxEntry deletefile = fetchDeleteOrUpload(textpath, child);
				System.out.println("Deleting the file: '" + deletefile.name + "', at placement: '" + deletefile.path
						+ "'");
				delete(deletefile);
				System.out.println("Deleting the command file 'delete.txt'");
				delete(child);
				break;

			case "upload.txt":
				DbxEntry uploadfile = fetchDeleteOrUpload(textpath, child);
				System.out.println("Uploading the file: '" + uploadfile.name + "', from placement: '" + uploadfile.path
						+ "'");
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				client.getFile(uploadfile.path, null, out);
				byte[] fileArray = out.toByteArray();
				FileOutputStream fos = new FileOutputStream(Main.path + uploadfile.name);
				fos.write(fileArray);
				fos.close();
				System.out.println(ImgurConnecter.uploadToImgur(Main.path + uploadfile.name));
				System.out.println("Deleting the command file 'upload.txt'");
				delete(child);
				break;

			default:
				if (child.isFile()) {
					System.out.println("The .txt file " + child.name + " is an invalid command");
					System.out.println("Moving the text file to the invalid folder");
					client.move("/space/collage/text/" + child.name, "/space/collage/text/invalid/" + child.name);
				}
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

	private static ArrayList<String> readCreateCommand(String path, DbxEntry child) {

		try {
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
			// an ArrayList of the files to be included and the name is returned
			return data;
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	private static DbxEntry fetchDeleteOrUpload(String path, DbxEntry child) throws DbxException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		client.getFile(path + "/" + child.name, null, out);
		Scanner sc = new Scanner(out.toString());
		String name = sc.nextLine();
		String folderPath = sc.nextLine();
		out.close();
		sc.close();
		DbxEntry file = client.getMetadata("/space" + folderPath + "/" + name);
		// the file to be deleted or uploaded is returned
		return file;
	}

	private static String[] fetchMovePaths(String path, DbxEntry child) throws DbxException, IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		client.getFile(path + "/" + child.name, null, out);
		String[] paths = new String[2];
		Scanner sc = new Scanner(out.toString());
		paths[0] = sc.nextLine();
		paths[1] = sc.nextLine();
		sc.close();
		out.close();
		// the string array is returned
		return paths;
	}

}
