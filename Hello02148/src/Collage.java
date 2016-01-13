import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

public class Collage {

	static int counter = 0;
	static int width = 300;
	static int widthh = width * 2;
	static int height = 255;
	static int heightt = height * 2;
	static int type = 1;
	static String collagename = "collage" + counter;
	static String finalName;
	
	// multi
	static String multi(List<String> imgfiles){
		
		while (imgfiles.size() > 1) {
			switch (imgfiles.size()) {
			case 2:
				try {
					String path = Collage.collage(Main.path + imgfiles.get(0),
							Main.path + imgfiles.get(1));
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.add(imgfiles.size(), path);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 3:
				try {
					String path = Collage.collage(Main.path + imgfiles.get(0),
							Main.path + imgfiles.get(1),
							Main.path + imgfiles.get(2));
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.add(imgfiles.size(), path);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			case 5:
				try {
					String path = Collage.collage(Main.path + imgfiles.get(0),
							Main.path + imgfiles.get(1),
							Main.path + imgfiles.get(2),
							Main.path + imgfiles.get(3),
							Main.path + imgfiles.get(4));
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.add(imgfiles.size(), path);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			default:
				try {
					String path = Collage.collage(Main.path + imgfiles.get(0),
							Main.path + imgfiles.get(1),
							Main.path + imgfiles.get(2),
							Main.path + imgfiles.get(3));
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.remove(0);
					imgfiles.add(imgfiles.size(), path);

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		System.out.println("done collaging");
		return null;
	}

	// one
	static String collage(String im0) throws IOException {

		// fetching image files
		File imgFiles = new File(im0);

		// creating a image array from image files

		BufferedImage temp = ImageIO.read(imgFiles);
		BufferedImage tempImg = new BufferedImage(widthh, heightt, type);
		Graphics2D g = tempImg.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.drawImage(temp, 0, 0, widthh, heightt, 0, 0, temp.getWidth(), temp.getHeight(), null);
		BufferedImage buffImage = tempImg;
		g.dispose();

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(widthh, heightt, type);
		finalImg.createGraphics().drawImage(buffImage, 0, 0, null);

		// nameing the file
		return path(finalImg);
	}

	// two
	static String collage(String im0, String im1) throws IOException {

		// fetching image files
		File[] imgFiles = new File[2];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);

		// creating a image array from image files
		BufferedImage[] buffImage = new BufferedImage[2];
		for (int i = 0; i < 2; i++) {
			BufferedImage temp = ImageIO.read(imgFiles[i]);
			BufferedImage tempImg = new BufferedImage(width, heightt, type);
			Graphics2D g = tempImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(temp, 0, 0, width, heightt, 0, 0, temp.getWidth(), temp.getHeight(), null);
			buffImage[i] = tempImg;
			g.dispose();
		}

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(widthh, heightt, type);

		finalImg.createGraphics().drawImage(buffImage[0], 0, 0, null);
		finalImg.createGraphics().drawImage(buffImage[1], width, 0, null);

		// nameing the file
		return path(finalImg);
	}

	// three
	static String collage(String im0, String im1, String im2) throws IOException {

		// fetching image files
		File[] imgFiles = new File[3];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);
		imgFiles[2] = new File(im2);

		// creating a image array from image files
		BufferedImage[] buffImage = new BufferedImage[3];
		int tempWidth = width;
		for (int i = 0; i < 3; i++) {
			if (i == 2) {
				width = width * 2;
			}
			BufferedImage temp = ImageIO.read(imgFiles[i]);
			BufferedImage tempImg = new BufferedImage(width, height, type);
			Graphics2D g = tempImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(temp, 0, 0, width, height, 0, 0, temp.getWidth(), temp.getHeight(), null);
			buffImage[i] = tempImg;
			g.dispose();
		}
		width = tempWidth;

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(widthh, heightt, type);

		finalImg.createGraphics().drawImage(buffImage[0], 0, 0, null);
		finalImg.createGraphics().drawImage(buffImage[1], width, 0, null);
		finalImg.createGraphics().drawImage(buffImage[2], 0, height, null);

		// nameing the file
		return path(finalImg);
	}

	// four
	static String collage(String im0, String im1, String im2, String im3) throws IOException {

		// fetching image files
		File[] imgFiles = new File[4];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);
		imgFiles[2] = new File(im2);
		imgFiles[3] = new File(im3);

		// creating a image array from image files
		BufferedImage[] buffImage = new BufferedImage[4];
		for (int i = 0; i < 4; i++) {
			BufferedImage temp = ImageIO.read(imgFiles[i]);
			BufferedImage tempImg = new BufferedImage(width, height, type);
			Graphics2D g = tempImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(temp, 0, 0, width, height, 0, 0, temp.getWidth(), temp.getHeight(), null);
			buffImage[i] = tempImg;
			g.dispose();
		}

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(widthh, heightt, type);

		int num = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				finalImg.createGraphics().drawImage(buffImage[num], width * i, height * j, null);
				num++;
			}
		}

		// nameing the file
		return path(finalImg);
	}

	// five
	static String collage(String im0, String im1, String im2, String im3, String im4) throws IOException {

		// fetching image files
		File[] imgFiles = new File[5];

		imgFiles[0] = new File(im0);
		imgFiles[1] = new File(im1);
		imgFiles[2] = new File(im2);
		imgFiles[3] = new File(im3);
		imgFiles[4] = new File(im4);

		// creating a image array from image files
		BufferedImage[] buffImage = new BufferedImage[5];
		for (int i = 0; i < 5; i++) {
			BufferedImage temp = ImageIO.read(imgFiles[i]);
			BufferedImage tempImg = new BufferedImage(width, height, type);
			Graphics2D g = tempImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.drawImage(temp, 0, 0, width, height, 0, 0, temp.getWidth(), temp.getHeight(), null);
			buffImage[i] = tempImg;
			g.dispose();
		}

		// Initializing the final image
		BufferedImage finalImg = new BufferedImage(widthh, heightt, type);

		int num = 0;
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				finalImg.createGraphics().drawImage(buffImage[num], width * j, height * i, null);
				num++;
			}
		}
		finalImg.createGraphics().drawImage(buffImage[num], width/2, height/2, null);

		// nameing the file
		return path(finalImg);
	}

	static String path(BufferedImage image) throws IOException {
		String name = collagename + ".jpg";
		counter++;
		collagename = "collage" + counter;
		System.out.println(name + " was created");
		// mangler en ordentlig path
		String path = Main.path + name;
		ImageIO.write(image, "jpg", new File(path));
		finalName = name;
		return name;
	}
}