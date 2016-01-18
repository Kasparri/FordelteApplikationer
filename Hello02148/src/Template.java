import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class Template {

	String path; // File folder
	String name; // File name (without extension), "?" denotes any value
	String ext; // File extension, "?" denotes any value
	byte[] content; // File content (as byte array)
<<<<<<< HEAD
	String type;
	// Preloading the variables for imgur
=======

	// Pre-loading the variables for imgur
>>>>>>> refs/remotes/origin/master
	static List<String> sections = new ArrayList<String>();
<<<<<<< HEAD
	static List<String> images;
=======
	static List<String> images = ImgurConnecter.getImgsFromSite("/r/pixelart");
>>>>>>> refs/remotes/origin/master
	static List<String> imgfiles = new ArrayList<String>();
	static int i = 0;
	static int k = 0;

	public Template(String p, String n, String e, byte[] c) {
		this.path = p;
		this.name = n;
		this.ext = e;
		this.content = c;
	}

<<<<<<< HEAD
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
=======
	// reads the name, extension and content
	public void read(DbxClient client) throws DbxException, IOException {
>>>>>>> refs/remotes/origin/master
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
			// Simple implementation based on busy-wait
<<<<<<< HEAD
			// We hence insert a delay of 10 seconds to minimise unsucessful
			// checks
			System.out
					.println("Blocking operation (qry/get) was unsucessful, downloading an image... ");
=======
			// We hence insert a delay in which images can be downloaded from
			// imgur
>>>>>>> refs/remotes/origin/master
			try {
				if (i < images.size()) {
					// Downloading in image then uploading to dropbox
<<<<<<< HEAD
					System.out
							.println("Downloading an image then uploading to dropbox \n");
					String[] info = ImgurConnecter.getInfo(images.get(i)
							.substring(19, images.get(i).length() - 4));
=======
					System.out.println("Downloading an image then uploading to dropbox \n");
					String[] info = ImgurConnecter.getInfo(images.get(i).substring(19, images.get(i).length() - 4));
>>>>>>> refs/remotes/origin/master
					for (String part : info) {
<<<<<<< HEAD
						if (part.contains("\"section\":") && !part.contains("null")) {
							part = part.substring(part.indexOf(':') + 2,
									part.length() - 1);
							part = "/t/" + part;
							if (!sections.contains(part)) {
								sections.add(part);
								System.out.println("Found new section: " + part);
							}
=======
						if (part.contains("\"section\":")) {
							part = part.substring(part.indexOf(':') + 1, part.length() - 1);
							part = "/r/" + part;
							sections.add(part);
>>>>>>> refs/remotes/origin/master
						}
					}
					ImgurConnecter.downloadFromImgur(images.get(i));
				}
				if (i == images.size() - 1) {
					// Finished the current list of images, getting a new one
					System.out.println("collected images, switching tag \n");
					i = 0;
					images = ImgurConnecter.getImgsFromSite(sections.get(k));
					k++;

				}
<<<<<<< HEAD
				if (Dropbox.pictureAmount("/space/collage/imgur") >= 16) {
					DbxEntry.WithChildren imagesdbx = client
							.getMetadataWithChildren("/space/collage/imgur");
=======
				if (Dropbox.pictureAmount(Dropbox.space + "/imgur") >= 16) {
					DbxEntry.WithChildren imagesdbx = client.getMetadataWithChildren(Dropbox.space + "/imgur");
>>>>>>> refs/remotes/origin/master
					System.out.println("Downloading images from dropbox \n");
					for (DbxEntry child : imagesdbx.children) {
						Dropbox.downloadFromDropbox(child.path, child.name);
						imgfiles.add(child.name);
					}
					System.out.println("Begin collaging \n");

					// Combining names to make a name for the collage
					DbxEntry.WithChildren namelist;
<<<<<<< HEAD
					String collagename = "";
					namelist = client
							.getMetadataWithChildren("/space/collage/imgur");
					for (int i = 0; i < 8; i++) {
						int random = (int) Math.random()
								* (namelist.children.get(i).name
										.lastIndexOf('.') - 1);
						collagename = collagename
								+ namelist.children.get(i).name.charAt(random);
=======
					String collageName = "";
					namelist = client.getMetadataWithChildren(Dropbox.space + "/imgur");
					for (int i = 0; i < 8; i++) {
						int random = (int) Math.random() * (namelist.children.get(i).name.lastIndexOf('.') - 1);
						collageName = collageName + namelist.children.get(i).name.charAt(random);
>>>>>>> refs/remotes/origin/master
					}
<<<<<<< HEAD
					collagename = collagename + ".jpg";

					Collage.multi(imgfiles, collagename);

					// Uploading the finished collage to dropbox

					File collage = new File(Dropbox.path + collagename);
=======
					collageName = collageName + ".jpg";
					Collage.multi(imgfiles, collageName);

					// Uploading the finished collage to dropbox
					File collage = new File(Dropbox.localPath + collageName);
>>>>>>> refs/remotes/origin/master
					try {
<<<<<<< HEAD
						FileInputStream inputStream = new FileInputStream(
								collage);
						DbxEntry.File uploadedFile = Dropbox.client.uploadFile(
								"/space/collage/collages/" + collagename,
								DbxWriteMode.add(), collage.length(),
								inputStream);

=======
						FileInputStream inputStream = new FileInputStream(collage);
						Dropbox.client.uploadFile(Dropbox.space + "/ImgurCollages/" + collageName, DbxWriteMode.add(),
								collage.length(), inputStream);
>>>>>>> refs/remotes/origin/master
						inputStream.close();
					} catch (DbxException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Uploading the finished collage to imgur
<<<<<<< HEAD
					System.out.println(ImgurConnecter
							.uploadToImgur(Dropbox.path + collagename));
=======
					System.out.println(ImgurConnecter.uploadToImgur(Dropbox.localPath + collageName));
>>>>>>> refs/remotes/origin/master
					collage.delete();

					// Emptying the dropbox folder
					DbxEntry.WithChildren imgurPictures;
<<<<<<< HEAD
					imgurPictures = client
							.getMetadataWithChildren("/space/collage/imgur");
=======
					imgurPictures = client.getMetadataWithChildren(Dropbox.space + "/imgur");
>>>>>>> refs/remotes/origin/master
					for (DbxEntry child : imgurPictures.children) {
						client.delete(child.path);
					}
<<<<<<< HEAD
					// Emptying local folder
					File folder = new File(Dropbox.path);
=======
					
					// Emptying local folder
					File folder = new File(Dropbox.localPath);
>>>>>>> refs/remotes/origin/master
					for (File file : folder.listFiles()) {
						file.delete();
					}
<<<<<<< HEAD
					Dropbox.downloadFromDropbox(
							"/space/collage/pics/default.jpg", "default.jpg");
=======
					Dropbox.downloadFromDropbox(Dropbox.space + "/pics/default.jpg", "default.jpg");
>>>>>>> refs/remotes/origin/master

					// Emptying the list
					imgfiles.clear();

				}
				i++;
				Thread.sleep(1);

			} catch (InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}
	}

	public void put(DbxClient client) throws DbxException, IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(content);
		client.uploadFile(path + name + "." + ext, DbxWriteMode.add(), -1, in);
		return;
	}

}
