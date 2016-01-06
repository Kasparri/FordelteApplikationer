import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class Template {
	
		static DbxClient client;
	
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
						if (supportedTypes(child.name.substring(j+1))){
							ext_aux = "pictures";
						} else {
							ext_aux = child.name.substring(j + 1);
						}
						
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
		
		public boolean supportedTypes(String s){
			if (s.equals("PNG") || s.equals("JPG") || s.equals("JPEG")){
				return true;
			}
			return false;
		}
	}