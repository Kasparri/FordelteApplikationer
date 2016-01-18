import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;

public class ImgurConnecter {

	public static String uploadToImgur(String filepath) {

		BufferedImage image = null;
		URL url;
		String output = "upload failed";
		String[] split = null;
		File file = new File(filepath);
		// Reading and preparing the image in base64
		try {
			image = ImageIO.read(file);
			// Encoding the image
			ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
			ImageIO.write(image, "png", byteArray);
			byte[] byteImage = byteArray.toByteArray();
			String dataImage = Base64.getEncoder().encodeToString(byteImage);
			String data = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(dataImage, "UTF-8");

			url = new URL("https://api.imgur.com/3/image");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Client-ID " + "b34d2583bb8a43f");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			conn.connect();
			StringBuilder stb = new StringBuilder();
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(data);
			wr.flush();

			// Get the response
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				stb.append(line).append("\n");
			}
			wr.close();
			rd.close();
			output = stb.toString();

			// Splitting the string to get the info we want
			split = output.split(",");
			for (String part : split) {
				if (part.contains("link")) {
					part = part.replace("\\", "");
					output = (part.substring(8, part.length() - 2));

				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output;
	}

	public static String downloadFromImgur(String imageURL) {
		URL url;
		String name = imageURL.substring(19, imageURL.length());
		File image = null;

		// Checking if the image were about to download is animated or nsfw
		String[] info = getInfo(name.substring(0, name.length() - 4));
		for (String part : info) {
			if (part.contains("\"nsfw\":true")) {
				System.out.println("Image was nsfw");
				return "default.jpg";
			}
			if (part.contains("\"animated\":true")) {
				System.out.println("Image was animated");
				return "default.jpg";
			}
			if (part.contains("\"size\":")) {
				int size = Integer.parseInt(part.substring(7));
				if (size >= 3999999) {
					size = size / 1000000;
					System.out.println("Image was too large, size was: " + size + "MB");
					return "default.jpg";
				}

			}
		}

		try {
			url = new URL(imageURL);
			InputStream inStream = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int n = 0;
			while (-1 != (n = inStream.read(buff))) {
				outStream.write(buff, 0, n);
			}
			outStream.close();
			inStream.close();
			byte[] result = outStream.toByteArray();
			FileOutputStream fileStream = new FileOutputStream(Dropbox.localPath + name);
			fileStream.write(result);
			fileStream.close();
			// Testing the file
			image = new File(Dropbox.localPath + name);
			@SuppressWarnings("unused")
			BufferedImage BI = ImageIO.read(image);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			image.delete();
			return "default.jpg";
		}

		// Uploading the file to dropbox
		try {
			FileInputStream inputStream = new FileInputStream(image);
			Dropbox.client
					.uploadFile(Dropbox.space + "/imgur/" + name, DbxWriteMode.add(), image.length(), inputStream);

			inputStream.close();
			image.delete();
		} catch (DbxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return name;

	}

	public static List<String> getImgsFromSite(String tag) {
		InputStream input;
		List<String> srcs = new ArrayList<String>();
		
//		if (tagIsUsed(tag)) {
			try {
				tag = "http://imgur.com" + tag;
				input = new URL(tag).openStream();
				Tidy tidy = new Tidy();
				tidy.setShowErrors(0);
				tidy.setShowWarnings(false);
				Document document = tidy.parseDOM(input, null);
				NodeList imgs = document.getElementsByTagName("img");
				srcs = new ArrayList<String>();

<<<<<<< HEAD
				for (int i = 0; i < imgs.getLength(); i++) {
					srcs.add(imgs.item(i).getAttributes().getNamedItem("src")
							.getNodeValue());
=======
			for (int i = 0; i < imgs.getLength(); i++) {
				srcs.add(imgs.item(i).getAttributes().getNamedItem("src").getNodeValue());
			}
			for (int i = 0; i < srcs.size(); i++) {
				srcs.set(
						i,
						srcs.get(i).substring(2, srcs.get(i).length() - 5)
								+ srcs.get(i).substring(srcs.get(i).length() - 4));
				if (srcs.get(i).startsWith("s")) {
					srcs.remove(i);
				} else {
					srcs.set(i, "http://" + srcs.get(i));
>>>>>>> refs/remotes/origin/master
				}
				for (int i = 0; i < srcs.size(); i++) {
					srcs.set(
							i,
							srcs.get(i).substring(2, srcs.get(i).length() - 5)
									+ srcs.get(i).substring(
											srcs.get(i).length() - 4));
					if (srcs.get(i).startsWith("s")) {
						srcs.remove(i);
					} else {
						srcs.set(i, "http://" + srcs.get(i));
					}

				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		//}
		return srcs;
	}

	public static String[] getInfo(String imageID) {
		URL url;
		String output = "Failed to get the info";
		String[] split = null;
		try {
			// Setting up the connection

			url = new URL("https://api.imgur.com/3/image/" + imageID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Authorization", "Client-ID " + "b34d2583bb8a43f");
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			// Connecting
			conn.connect();

			// Getting the response
			StringBuilder stb = new StringBuilder();
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = rd.readLine()) != null) {
				stb.append(line).append("\n");
			}
			rd.close();
			output = stb.toString();

			// Splitting the string to get the info we want
			split = output.split(",");

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return split;

	}

//	public static boolean tagIsUsed(String tag) {
//		URL url;
//		String output = "Failed";
//		String[] split = null;
//		try {
//			// Setting up the connection
//
//			url = new URL("https://api.imgur.com/3/gallery" + tag);
//			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setDoOutput(true);
//			conn.setDoInput(true);
//			conn.setRequestMethod("GET");
//			conn.setRequestProperty("Authorization", "Client-ID "
//					+ "b34d2583bb8a43f");
//			conn.setRequestMethod("GET");
//			conn.setRequestProperty("Content-Type",
//					"application/x-www-form-urlencoded");
//
//			// Connecting
//			conn.connect();
//
//			// Getting the response
//			StringBuilder stb = new StringBuilder();
//			BufferedReader rd = new BufferedReader(new InputStreamReader(
//					conn.getInputStream()));
//			String line;
//			while ((line = rd.readLine()) != null) {
//				stb.append(line).append("\n");
//			}
//			rd.close();
//			output = stb.toString();
//
//			// Splitting the string to get the info we want
//			split = output.split(",");
//
//			// Figuring out of the tag is used
//			for (String part : split) {
//				if (part.contains("\"total_items\":")) {
//					int size = Integer.parseInt(part.substring(14));
//					if (size > 10) {
//						return true;
//					} else {
//						return false;
//					}
//
//				}
//			}
//
//		} catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return false;
//	}
}