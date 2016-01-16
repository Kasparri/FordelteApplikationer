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

	// Pre-loading the variables for imgur
	static List<String> sections = new ArrayList<String>();
	static List<String> images = ImgurConnecter.getImgsFromSite("/r/pixelart");
	static List<String> imgfiles = new ArrayList<String>();
	static int i = 0;
	static int k = 0;

	public Template(String p, String n, String e, byte[] c) {
		this.path = p;
		this.name = n;
		this.ext = e;
		this.content = c;
	}

	// reads the name, extension and content
	public void read(DbxClient client) throws DbxException, IOException {
		DbxEntry.WithChildren listing;
		String name_aux = name;
		String ext_aux = ext;

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
			// We hence insert a delay in which images can be downloaded from
			// imgur
			try {
				if (i < images.size()) {
					// Downloading in image then uploading to dropbox
					System.out.println("Downloading an image then uploading to dropbox \n");
					String[] info = ImgurConnecter.getInfo(images.get(i).substring(19, images.get(i).length() - 4));
					for (String part : info) {
						if (part.contains("\"section\":")) {
							part = part.substring(part.indexOf(':') + 1, part.length() - 1);
							part = "/r/" + part;
							sections.add(part);
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
				if (Dropbox.pictureAmount(Dropbox.space + "/imgur") >= 16) {
					DbxEntry.WithChildren imagesdbx = client.getMetadataWithChildren(Dropbox.space + "/imgur");
					System.out.println("Downloading images from dropbox \n");
					for (DbxEntry child : imagesdbx.children) {
						Dropbox.downloadFromDropbox(child.path, child.name);
						imgfiles.add(child.name);
					}
					System.out.println("Begin collaging \n");

					// Combining names to make a name for the collage
					DbxEntry.WithChildren namelist;
					String collageName = "";
					namelist = client.getMetadataWithChildren(Dropbox.space + "/imgur");
					for (int i = 0; i < 8; i++) {
						int random = (int) Math.random() * (namelist.children.get(i).name.lastIndexOf('.') - 1);
						collageName = collageName + namelist.children.get(i).name.charAt(random);
					}
					collageName = collageName + ".jpg";
					Collage.multi(imgfiles, collageName);

					// Uploading the finished collage to dropbox
					File collage = new File(Dropbox.localPath + collageName);
					try {
						FileInputStream inputStream = new FileInputStream(collage);
						Dropbox.client.uploadFile(Dropbox.space + "/ImgurCollages/" + collageName, DbxWriteMode.add(),
								collage.length(), inputStream);
						inputStream.close();
					} catch (DbxException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

					// Uploading the finished collage to imgur
					System.out.println(ImgurConnecter.uploadToImgur(Dropbox.localPath + collageName));
					collage.delete();

					// Emptying the dropbox folder
					DbxEntry.WithChildren imgurPictures;
					imgurPictures = client.getMetadataWithChildren(Dropbox.space + "/imgur");
					for (DbxEntry child : imgurPictures.children) {
						client.delete(child.path);
					}
					
					// Emptying local folder
					File folder = new File(Dropbox.localPath);
					for (File file : folder.listFiles()) {
						file.delete();
					}
					Dropbox.downloadFromDropbox(Dropbox.space + "/pics/default.jpg", "default.jpg");

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
