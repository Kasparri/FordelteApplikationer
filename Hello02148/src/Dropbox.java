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

public class Dropbox {

	// Some global variables
	static DbxAppInfo appInfo;
	static DbxRequestConfig config;
	static DbxClient client;
	static String localPath = "./images/"; // the local path used to store
											// pictures for the collage making
	// Name of the shared folder to be used as shared space
	static String space = "/space";

	public static void main(String[] args) throws IOException, DbxException {
		final String APP_KEY = "6gm4dggg0pm4qd3";
		final String APP_SECRET = "mnm27mqi749672i";
		appInfo = new DbxAppInfo(APP_KEY, APP_SECRET);
		config = new DbxRequestConfig("JavaTutorial/1.0", Locale.getDefault().toString());
		final String accessToken = "2IXvlsnWAFAAAAAAAAAAEPhoKJM-eyCMjv3hmLJncYB_x536trI0mGHg3U-OIYep";

		// configuring the Dropbox client
		client = new DbxClient(config, accessToken);

		// Two tuple templates for the main loop of the application
		Template t1;
		Template t2;

		// Main loop of the program
		while (true) {

			// reads and executes the text commands
			readTextCommands();

			// Template t1 represents "any file in the space folder"
			t1 = new Template(space, "?", "?", null);
			System.out.println("Looking for some file... \n");
			t1.read(client);
			System.out.println("Found file '" + t1.name + "." + t1.ext + "'");

			// t2 represents the previously retrieved file in a new folder
			if (isPicture(t1.ext)) {
				t2 = new Template(space + "/pics/", t1.name, t1.ext, t1.content);
				System.out.println("Putting the file in the 'picture' subfolder \n");
			} else if (isText(t1.ext)) {
				t2 = new Template(space + "/text/", t1.name, t1.ext, t1.content);
				System.out.println("Putting the file in the 'text' subfolder \n");
			} else {
				t2 = new Template(space + "/others/", t1.name, t1.ext, t1.content);
				System.out.println("Putting the file in the 'others' subfolder \n");
			}
			t2.put(client);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	// reads and executes the text command
	public static void readTextCommands() throws IOException, DbxException {
		String textpath = space + "/text";
		DbxEntry.WithChildren listing = null;
		try {
			listing = client.getMetadataWithChildren(textpath);
		} catch (DbxException e1) {
			e1.printStackTrace();
		}

		// loops through all the text files
		for (DbxEntry child : listing.children) {
			switch (child.name) {

			// creates a collage specified by the text file
			case "create.txt":
				// data is a list of structure [Collagename, picname 1,
				// ...,picname n] with n amount of pictures used in the collage
				List<String> data = readCreateCommand(textpath, child);
				String collageName = data.get(0);
				data.remove(0);
				for (int i = 0; i < data.size(); i++) {
					downloadFromDropbox(space + "/pics/" + data.get(i), data.get(i));

					// TODO: Hvad er det her godt for Frederik?
					// String newpath = data.get(i);
					// data.set(i, newpath);
				}
				Collage.multi(data, collageName);
				File collage = new File(localPath + collageName);
				FileInputStream inputStream = new FileInputStream(collage);
				Dropbox.client.uploadFile(space + "/DropboxCollages/" + collageName, DbxWriteMode.add(),
						collage.length(), inputStream);
				System.out.println("'" + collageName + "' has been uploaded to " + space + "/DropboxCollages/");
				inputStream.close();
				collage.delete();
				System.out.println("Deleting the command file 'create.txt' \n");
				delete(child);
				break;

			case "move.txt":
				// fetches the fromPath and toPath from the move.txt text file
				String[] paths = fetchMovePaths(textpath, child);
				System.out.println("Moving the file at placement: '" + paths[0] + "', to: '" + paths[1] + "'");
				try {
					client.move(paths[0], paths[1]);
					System.out.println("Deleting the command file 'move.txt'");
					delete(child);
				} catch (DbxException e) {
					e.printStackTrace();
					System.out.println("Invalid path/s");
					System.out.println("Moving the text file to the invalid folder");
					client.move(space + "/text/" + child.name, space + "/text/invalid/" + child.name);
					System.out.println("Moving on to the next command");
					continue;
				}
				break;

			// deleting the file specified by the text file
			case "delete.txt":
				DbxEntry deletefile = fetchDeleteOrUpload(textpath, child);
				if (deletefile == null) {
					System.out.println("Moving on to the next command");
					continue;
				}
				System.out.println("Deleting the file: '" + deletefile.name + "', at placement: '" + deletefile.path
						+ "'");
				delete(deletefile);
				System.out.println("Deleting the command file 'delete.txt'");
				delete(child);
				break;

			case "upload.txt":
				DbxEntry uploadfile = fetchDeleteOrUpload(textpath, child);
				if (uploadfile == null) {
					System.out.println("Moving on to the next command");
					continue;
				}
				System.out.println("Uploading the file: '" + uploadfile.name + "', from placement: '" + uploadfile.path
						+ "'");
				downloadFromDropbox(uploadfile.path, uploadfile.name);
				System.out.println(ImgurConnecter.uploadToImgur(localPath + uploadfile.name));
				System.out.println("Deleting the command file 'upload.txt'");
				delete(child);
				break;

			default:
				if (child.isFile()) {
					System.out.println("The .txt file " + child.name + " is an invalid command");
					System.out.println("Moving the text file to the invalid folder");
					client.move(space + "/text/" + child.name, space + "/text/invalid/" + child.name);
				}
				break;
			}
		}
	}

	public static int pictureAmount(String path) {
		try {
			return client.getMetadataWithChildren(path).children.size();
		} catch (DbxException e) {
			e.printStackTrace();
			return 0;
		}
	}

	private static boolean isPicture(String s) {
		s = s.toLowerCase();
		if (s.equals("png") || s.equals("jpg") || s.equals("jpeg")) {
			return true;
		}
		return false;
	}

	private static boolean isText(String s) {
		s = s.toLowerCase();
		if (s.equals("txt")) {
			return true;
		}
		return false;
	}

	private static void delete(DbxEntry file) {
		try {
			client.delete(file.path);
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

	private static List<String> readCreateCommand(String path, DbxEntry child) {
		Scanner sc = fetchCommandInScanner(path, child);
		// making an ArrayList holding the name and filenames
		List<String> data = new ArrayList<String>();
		// the name of the collage
		data.add(sc.nextLine());
		int amount = sc.nextInt();
		sc.nextLine();
		for (int i = 0; i < amount; i++) {
			data.add(sc.nextLine());
		}
		sc.close();
		// an ArrayList of the files to be included and the name is returned
		return data;

	}

	private static DbxEntry fetchDeleteOrUpload(String path, DbxEntry child) {
		try {
			Scanner sc = fetchCommandInScanner(path, child);
			String name = sc.nextLine();
			String folderPath = sc.nextLine();
			sc.close();
			DbxEntry file = client.getMetadata(space + folderPath + "/" + name);
			if (file == null) {
				System.out.println("Invalid filename or path");
				System.out.println("Moving the text file to the invalid folder");
				client.move(space + "/text/" + child.name, space + "/text/invalid/" + child.name);
				return null;
			} else {
				// the file to be deleted or uploaded is returned
				return file;
			}
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String[] fetchMovePaths(String path, DbxEntry child) {
		Scanner sc = fetchCommandInScanner(path, child);
		String[] paths = new String[2];
		paths[0] = sc.nextLine();
		paths[1] = sc.nextLine();
		sc.close();
		// the string array is returned
		return paths;
	}

	// fetches the command.txt file and returns it in a Scanner
	private static Scanner fetchCommandInScanner(String path, DbxEntry child) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			client.getFile(path + "/" + child.name, null, out);
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Scanner sc = new Scanner(out.toString());
		return sc;
	}

	public static void downloadFromDropbox(String childPath, String childName) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			client.getFile(childPath, null, out);
			byte[] fileArray = out.toByteArray();
			FileOutputStream fos = new FileOutputStream(localPath + childName);
			fos.write(fileArray);
			fos.close();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
