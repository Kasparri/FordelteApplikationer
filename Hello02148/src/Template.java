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
	String type;
	//Preloading the variables for imgur
	static List<String> sections = new ArrayList<String>();
	static List<String> images = ImgurConnecter
			.getImgsFromSite("/r/pixelart");
	static List<String> imgfiles = new ArrayList<String>();
	static int i = 0;
	static int k = 0;

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
					//Downloading in image then uploading to dropbox
					System.out.println("Downloading an image then uploading to dropbox \n");
					String[] info = ImgurConnecter.getInfo(images.get(i).substring(19, images.get(i).length() - 4));
					for (String part : info) {
						if (part.contains("\"section\":")) {
							part=part.substring(part.indexOf(':')+1,part.length()-1);
							part="/r/"+part;
							sections.add(part);
						}
					}
					ImgurConnecter.downloadFromImgur(images.get(i));
				}
				if (i == images.size() - 1) {
					//Finished the current list of images, getting a new one
					System.out.println("collected images, switching tag \n");
					i=0;
					images = ImgurConnecter
							.getImgsFromSite(sections.get(k));
					k++;
					
				}
				if (Dropbox.pictureAmount("/space/collage/imgur") >= 16) {
					DbxEntry.WithChildren imagesdbx = client.getMetadataWithChildren("/space/collage/imgur");
					System.out.println("Downloading images from dropbox \n");
					for ( DbxEntry child : imagesdbx.children )  {
						Dropbox.downloadFromDropbox(child.path, child.name);
						imgfiles.add(child.name);
					}
					System.out.println("Begin collaging \n");
					
					//Combining names to make a name for the collage
					DbxEntry.WithChildren namelist;
					String collagename = "";
					namelist = client.getMetadataWithChildren("/space/collage/imgur");
					for(int i = 0 ; i < 8 ; i++) {
						int random = (int) Math.random() * (namelist.children.get(i).name.lastIndexOf('.')-1);
						collagename = collagename + namelist.children.get(i).name.charAt(random);
					}
					collagename = collagename + ".jpg";
					
					Collage.multi(imgfiles,collagename);
					
					//Uploading the finished collage to dropbox
					
					File collage = new File(Dropbox.path + collagename);
					try {
						FileInputStream inputStream = new FileInputStream(collage);
						DbxEntry.File uploadedFile = Dropbox.client.uploadFile(
								"/space/collage/collages/" + collagename, DbxWriteMode.add(),
								collage.length(), inputStream);

						inputStream.close();
					} catch (DbxException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					//Uploading the finished collage to imgur
					System.out.println(ImgurConnecter.uploadToImgur(Dropbox.path + collagename));
					collage.delete();
					
					//Emptying the dropbox folder
					DbxEntry.WithChildren imgurPictures;
					imgurPictures = client.getMetadataWithChildren("/space/collage/imgur");
					for (DbxEntry child : imgurPictures.children){
						client.delete(child.path);
					}
					//Emptying local folder
					File folder = new File(Dropbox.path);
					for(File file: folder.listFiles()) { 
						file.delete();
					}
					Dropbox.downloadFromDropbox("/space/collage/pics/default.jpg", "default.jpg");
					
					//Emptying the list
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
