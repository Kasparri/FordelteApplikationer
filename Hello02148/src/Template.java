import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class Template {

	String path; // File folder
	String name; // File name (without extension), "?" denotes any value
	String ext; // File extension, "?" denotes any value
	byte[] content; // File content (as byte array)
	String type;
	// Preloading the variables for imgur

	// Pre-loading the variables for imgur
	static List<String> sections = new ArrayList<String>();
	static List<String> images;
	static List<String> imgfiles = new ArrayList<String>();
	static int i = 0;
	static int k = 0;

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
	public void qry(DbxClient client) throws DbxException, IOException {
		read(client, false);
	}

	// As qry() but the file is removed from the space
	public void get(DbxClient client) throws DbxException, IOException {
		read(client, true);
	}

	// Auxiliary function that implements the both qry() and get()
	public void read(DbxClient client, boolean delete) throws DbxException,
			IOException {
	}

	// reads the name, extension and content
	public void read(DbxClient client) throws DbxException, IOException {
		DbxEntry.WithChildren listing;
		String name_aux = name;
		String ext_aux = ext;

		if (sections.isEmpty()) {
			sections.add("/t/pics");
			sections.add("/t/funny");
			sections.add("/t/blackpeopletwitter");
			images = ImgurConnecter.getImgsFromSite(sections.get(0));
		}

		// Repeat until one file/tuple is found
		while (true) {
			listing = client.getMetadataWithChildren(path);
			for (DbxEntry child : listing.children) {
				if (name == "?") {
					int j = child.name.lastIndexOf('.');
					if (j == -1) {
						continue;
					}
					name_aux = child.name.substring(0, j);
				}
				if (ext == "?") {
					int j = child.name.lastIndexOf('.');
					if (j == -1) {
						continue;
					}
					ext_aux = child.name.substring(j + 1);

				}
				if (child.name.equals(name_aux + "." + ext_aux)) {
					name = name_aux;
					ext = ext_aux;
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					client.getFile(child.path, null, out);
					content = out.toByteArray();
					out.close();
					client.delete(child.path);
					return;
				}
			}
			System.out
					.println("No files to sort, executing text commands then downloading an image from imgur...");
			Dropbox.readTextCommands();

			if (Dropbox.makeCollageFromDropboxImages()) {
				System.out
						.println("Making a collage from the pictures currently on Dropbox");
			}

			downloadFromImgur(client);

		}
	}

	private void downloadFromImgur(DbxClient client) {
		try {
			if (i < images.size()) {
				// Downloading in image then uploading to dropbox
				System.out
						.println("Downloading an image then uploading to dropbox \n");
				String[] info = ImgurConnecter.getInfo(images.get(i).substring(
						19, images.get(i).length() - 4));
				for (String part : info) {
					if (part.contains("\"section\":") && !part.contains("null")) {
						part = part.substring(part.indexOf(':') + 2,
								part.length() - 1);
						part = "/t/" + part;
						if (!sections.contains(part.toLowerCase())) {
							sections.add(part);
							System.out.println("Found new section: " + part);
						}
					}
				}
				ImgurConnecter.downloadFromImgur(images.get(i));
				if (i == images.size() - 1) {
					// Finished the current list of images, getting a new
					// one
					System.out.println("collected images, switching tag \n");
					i = 0;
					images = ImgurConnecter.getImgsFromSite(sections.get(k));
					k++;

				}
				if (Dropbox.pictureAmount(Dropbox.space + "/imgur") >= 16) {
					DbxEntry.WithChildren imagesdbx = client
							.getMetadataWithChildren(Dropbox.space + "/imgur");
					System.out.println("Downloading images from dropbox \n");
					for (DbxEntry child : imagesdbx.children) {
						Dropbox.downloadFromDropbox(child.path, child.name);
						imgfiles.add(child.name);
					}
					System.out.println("Begin collaging \n");

					// Combining names to make a name for the collage
					DbxEntry.WithChildren namelist;
					String collageName = "";
					namelist = client.getMetadataWithChildren(Dropbox.space
							+ "/imgur");
					for (int i = 0; i < 8; i++) {
						int random = (int) (Math.random()
								* (namelist.children.get(i).name
										.lastIndexOf('.') - 1));
						collageName = collageName
								+ namelist.children.get(i).name.charAt(random);
					}
					File collage = new File(Dropbox.localPath + collageName);
					collageName = collageName + ".jpg";
					Collage.makeCollage(imgfiles, collageName);

					// Uploading the finished collage to dropbox
					collage = new File(Dropbox.localPath + collageName);
					try {
						FileInputStream inputStream = new FileInputStream(
								collage);
						Dropbox.client.uploadFile(Dropbox.space
								+ "/ImgurCollages/" + collageName,
								DbxWriteMode.add(), collage.length(),
								inputStream);
						inputStream.close();
					} catch (DbxException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Uploading the finished collage to imgur
					addLink(ImgurConnecter.uploadToImgur(Dropbox.localPath
							+ collageName));
					collage.delete();

					// Emptying the dropbox folder
					System.out.println("Cleaning folders");
					DbxEntry.WithChildren imgurPictures;
					imgurPictures = client
							.getMetadataWithChildren(Dropbox.space + "/imgur");
					for (DbxEntry child : imgurPictures.children) {
						client.delete(child.path);
					}

					// Emptying local folder
					File folder = new File(Dropbox.localPath);
					for (File file : folder.listFiles()) {
						file.delete();
					}
					Dropbox.downloadFromDropbox(Dropbox.space
							+ "/pics/default.jpg", "default.jpg");

					// Emptying the list
					imgfiles.clear();

				}
				i++;
				Thread.sleep(5000);
			}
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		} catch (DbxException e1) {
			Thread.currentThread().interrupt();
		}
	}

	private void addLink(String link) {
		List<String> lines = new ArrayList<String>();
		String filename = "links.txt";
		try {
			// Downloading the file from Dropbox

			// Reading the file into the list
			Scanner scan = Dropbox.fetchCommandInScanner(Dropbox.space
					+ "/ImgurCollages", filename);
			while (scan.hasNextLine()) {
				lines.add(scan.nextLine() + "\n");
			}
			// Adding the link
			lines.add(link + "\n");

			File linkfile = new File(Dropbox.localPath + filename);
			// Writing the list back to the file
			BufferedWriter out = new BufferedWriter(new FileWriter(linkfile));
			for (String s : lines)
				out.write(s);
			out.flush();
			out.close();

			// Uploading the file back to Dropbox
			FileInputStream inputStream = new FileInputStream(linkfile);

			Dropbox.client.delete(Dropbox.space + "/ImgurCollages/" + filename);

			Dropbox.client.uploadFile(Dropbox.space + "/ImgurCollages/"
					+ filename, DbxWriteMode.add(), linkfile.length(),
					inputStream);

			inputStream.close();
			// linkfile.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void put(DbxClient client) throws DbxException, IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(content);
		client.uploadFile(path + name + "." + ext, DbxWriteMode.add(), -1, in);
		return;
	}

}
