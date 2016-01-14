import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
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
	String type;

	public Template(String p, String n, String e, byte[] c) {
		this.path = p;
		this.name = n;
		this.ext = e;
		this.content = c;
		this.type = null;
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
		DbxEntry.WithChildren listing;
		String name_aux = name;
		String ext_aux = ext;

		String[] tags ={"http://imgur.com/t/archery","http://imgur.com/t/cat","http://imgur.com/t/food","http://imgur.com/t/earthporn"};
		List<String> images = ImgurConnecter
				.getImgsFromSite("http://imgur.com/t/pixelart");
		List<String> imgfiles = new ArrayList<String>();
		int i = 0;
		int k = 0;

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
					type = child.name.substring(j + 1);
					ext_aux = type;

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
			System.out
					.println("Blocking operation (qry/get) was unsucessful, downloading an image... ");
			try {
				if (i < images.size()) {
					System.out.println("Downloading an image then uploading to dropbox \n");
					ImgurConnecter.downloadFromImgur(images.get(i));
				}
				if (i == images.size() - 1) {
					System.out.println("collected images, switching tag \n");
					i=0;
					images = ImgurConnecter
							.getImgsFromSite(tags[k]);
					k++;
					
				}
				if (Dropbox.pictureAmount() >= 16) {
					DbxEntry.WithChildren imagesdbx = client.getMetadataWithChildren("/space/collage/imgur");
					System.out.println("Downloading images from dropbox \n");
					for ( DbxEntry child : imagesdbx.children )  {
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						client.getFile(child.path, null, out);
						byte[] bytes = out.toByteArray();
						FileOutputStream fos = new FileOutputStream(Dropbox.path + child.name);
						fos.write(bytes);
						fos.close();
						imgfiles.add(child.name);
						
					}
					System.out.println("Begin collaging \n");
					Collage.multi(imgfiles,"CollageIMGUR.jpg");
				}
				i++;
				Thread.sleep(1000);

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
