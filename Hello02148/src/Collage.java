import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Collage {
	
	static int counter = 0;
	// jkk
	// one
	String collage(String im0) throws IOException {

		int width = 300*2;
		int height = 255*2;
		int type;

		// fetching image files
		File imgFiles = new File(im0);

		// creating a image array from image files
		BufferedImage tempImg = ImageIO.read(imgFiles);
		Image buffImage = tempImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		
		type = ImageIO.read(imgFiles).getType();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(width * 2, height, type);
		finalImg.createGraphics().drawImage(buffImage, 0, 0, null);
		
		// nameing the file
		counter++;
		String name = "collage" + counter + ".jpg";
		System.out.println(name + "was created");
		ImageIO.write(finalImg, "jpeg", new File(name));
		return name;
	}
	
	// two
	String collage(String im0, String im1) throws IOException {

		int width = 300;
		int height = 255*2;
		int type;

		// fetching image files
		File[] imgFiles = new File[2];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);

		// creating a image array from image files
		Image[] buffImage = new BufferedImage[2];
		for (int i = 0; i < 2; i++) {
			BufferedImage tempImg = ImageIO.read(imgFiles[i]);
			buffImage[i] = tempImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		}

		type = ImageIO.read(imgFiles[0]).getType();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(width * 2, height, type);
		
		finalImg.createGraphics().drawImage(buffImage[0], 0, 0, null);
		finalImg.createGraphics().drawImage(buffImage[1], width, 0, null);
		
		// nameing the file
		counter++;
		String name = "collage" + counter + ".jpg";
		System.out.println(name + "was created");
		ImageIO.write(finalImg, "jpeg", new File(name));
		return name;
	}
	
	// three 
	String collage(String im0, String im1, String im2) throws IOException {

		int width = 300;
		int height = 255;
		int type;

		// fetching image files
		File[] imgFiles = new File[3];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);
		imgFiles[2] = new File(im2);

		// creating a image array from image files
		Image[] buffImage = new BufferedImage[3];
		for (int i = 0; i < 3; i++) {
			BufferedImage tempImg = ImageIO.read(imgFiles[i]);
			if (i==2)
				width = width*2;
			buffImage[i] = tempImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
			width = 300;
		}

		type = ImageIO.read(imgFiles[0]).getType();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(width * 2, height * 2, type);

		
		finalImg.createGraphics().drawImage(buffImage[0], 0, 0, null);
		finalImg.createGraphics().drawImage(buffImage[1], width, 0, null);
		finalImg.createGraphics().drawImage(buffImage[2], 0, height, null);
		
		// nameing the file
		counter++;
		String name = "collage" + counter + ".jpg";
		System.out.println(name + "was created");
		ImageIO.write(finalImg, "jpeg", new File(name));
		return name;
	}
	
	// four
	String collage(String im0, String im1, String im2, String im3) throws IOException {

		int width = 300;
		int height = 255;
		int type;

		// fetching image files
		File[] imgFiles = new File[4];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);
		imgFiles[2] = new File(im2);
		imgFiles[3] = new File(im3);

		// creating a image array from image files
		Image[] buffImage = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			BufferedImage tempImg = ImageIO.read(imgFiles[i]);
			buffImage[i] = tempImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		}

		type = ImageIO.read(imgFiles[0]).getType();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(width * 2, height * 2, type);

		int num = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				finalImg.createGraphics().drawImage(buffImage[num], width * j, height * i, null);
				num++;
			}
		}
		
		// nameing the file
		counter++;
		String name = "collage" + counter + ".jpg";
		System.out.println(name + "was created");
		ImageIO.write(finalImg, "jpeg", new File(name));
		return name;
	}

	// five
	String collage(String im0, String im1, String im2, String im3, String im4) throws IOException {

		int width = 300;
		int height = 255;
		int type;

		// fetching image files
		File[] imgFiles = new File[5];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);
		imgFiles[2] = new File(im2);
		imgFiles[3] = new File(im3);
		imgFiles[4] = new File(im4);

		// creating a image array from image files
		Image[] buffImage = new BufferedImage[5];
		for (int i = 0; i < 5; i++) {
			BufferedImage tempImg = ImageIO.read(imgFiles[i]);
			buffImage[i] = tempImg.getScaledInstance(width, height, Image.SCALE_DEFAULT);
		}

		type = ImageIO.read(imgFiles[0]).getType();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(width * 2, height * 2, type);

		int num = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				finalImg.createGraphics().drawImage(buffImage[num], width * j, height * i, null);
				num++;
			}
		}
		finalImg.createGraphics().drawImage(buffImage[num], 150, 127, null);
		
		
		// nameing the file
		counter++;
		String name = "collage" + counter + ".jpg";
		System.out.println(name + "was created");
		ImageIO.write(finalImg, "jpeg", new File(name));
		return name;
	}
}
