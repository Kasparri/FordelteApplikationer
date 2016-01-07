import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

import javax.imageio.ImageIO;

public class ImgurConnecter {
 	public static void main(String[] args)  {
		File file = new File("C:\\Users\\Mads\\Pictures\\beta.PNG");
		try {
			System.out.println(uploadToImgur(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String image = "http://i.imgur.com/TJRrIdJ.jpg";
		downloadFromImgur(image);

	}
	public static String uploadToImgur(File file) throws IOException {

	    BufferedImage image = null;
	    URL url;
	    //Reading and preparing the image in base64
	    image = ImageIO.read(file);
	    
	    //Encoding the image 
	    ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
	    ImageIO.write(image, "png", byteArray);
	    byte[] byteImage = byteArray.toByteArray();
	    		//String dataImage = Base64.encode(byteImage);
	    String dataImage = Base64.getEncoder().encodeToString(byteImage);
	    String data = URLEncoder.encode("image", "UTF-8") + "="
	    + URLEncoder.encode(dataImage, "UTF-8");
	    
	    url = new URL("https://api.imgur.com/3/image");
	    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	    conn.setDoOutput(true);
	    conn.setDoInput(true);
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Authorization", "Client-ID " + "b34d2583bb8a43f");
	    conn.setRequestMethod("POST");
	    conn.setRequestProperty("Content-Type",
	            "application/x-www-form-urlencoded");

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

	    return stb.toString();
	}
	public static String downloadFromImgur(String imageURL) {
		URL url;
		try {
			url = new URL(imageURL);
			InputStream inStream = new BufferedInputStream(url.openStream());
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buff = new byte[1024];
			int n=0;
			while (-1!=(n=inStream.read(buff))) {
				outStream.write(buff,0,n);
			}
			outStream.close();
			inStream.close();
			byte[] result = outStream.toByteArray();
			String name = imageURL.substring(imageURL.length()-11, imageURL.length());
			FileOutputStream fileStream = new FileOutputStream("C:\\Users\\Mads\\Pictures\\"+name);
			fileStream.write(result);
			fileStream.close();
			
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		return null;
		
	}
}

