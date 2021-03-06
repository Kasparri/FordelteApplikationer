import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class Collage {

	static int counter = 0;
	static int width = 960;
	static int widthh = width * 2;
	static int height = 540;
	static int heightt = height * 2;
	static int type = BufferedImage.TYPE_INT_RGB;

	// The "main" collage making method.
	static void makeCollage(List<String> imgfiles, String n) {

		if (imgfiles.size() == 1) {
			try {
				collage(imgfiles.get(0), n);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {

			String name;
			while (imgfiles.size() > 1) {

				if (imgfiles.size() <= 5) {
					name = n;
				} else {
					name = "collage" + counter + ".jpg";
					counter++;
				}

				switch (imgfiles.size()) {
				case 2:
					try {
						collage(imgfiles.get(0), imgfiles.get(1), name);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.add(imgfiles.size(), name);

					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case 3:
					try {
						collage(imgfiles.get(0), imgfiles.get(1), imgfiles.get(2), name);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.add(imgfiles.size(), name);

					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				case 5:
					try {
						collage(imgfiles.get(0), imgfiles.get(1), imgfiles.get(2), imgfiles.get(3), imgfiles.get(4), name);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.add(imgfiles.size(), name);

					} catch (IOException e) {
						e.printStackTrace();
					}
					break;
				default:
					try {
						collage(imgfiles.get(0), imgfiles.get(1), imgfiles.get(2), imgfiles.get(3), name);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.remove(0);
						imgfiles.add(imgfiles.size(), name);

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("The collage '" + n + "' has been created");
	}

	// Collage of one picture
	static void collage(String im0, String name) throws IOException {

		// fetching image files
		File imgFiles = new File(Dropbox.localPath + im0);

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

		// Naming the file// naming the file
		ImageIO.write(finalImg, "jpg", new File(Dropbox.localPath + name));
	}

	// Collage of two pictures
	static void collage(String im0, String im1, String name) throws IOException {

		// fetching image files
		File[] imgFiles = new File[2];

		imgFiles[0] = new File(Dropbox.localPath + im0);
		imgFiles[1] = new File(Dropbox.localPath + im1);

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

		// Naming the file// naming the file
		ImageIO.write(finalImg, "jpg", new File(Dropbox.localPath + name));
	}

	// Collage of three pictures
	static void collage(String im0, String im1, String im2, String name) throws IOException {

		// fetching image files
		File[] imgFiles = new File[3];

		imgFiles[0] = new File(Dropbox.localPath + im0);
		imgFiles[1] = new File(Dropbox.localPath + im1);
		imgFiles[2] = new File(Dropbox.localPath + im2);

		// Creating a image array from image files
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

		// Naming the file// naming the file
		ImageIO.write(finalImg, "jpg", new File(Dropbox.localPath + name));
	}

	// Collage of four pictures
	static void collage(String im0, String im1, String im2, String im3, String name) throws IOException {

		// fetching image files
		File[] imgFiles = new File[4];

		imgFiles[0] = new File(Dropbox.localPath + im0);
		imgFiles[1] = new File(Dropbox.localPath + im1);
		imgFiles[2] = new File(Dropbox.localPath + im2);
		imgFiles[3] = new File(Dropbox.localPath + im3);

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

		// naming the file// naming the file
		ImageIO.write(finalImg, "jpg", new File(Dropbox.localPath + name));
	}

	// Collage of five pictures
	static void collage(String im0, String im1, String im2, String im3, String im4, String name) throws IOException {

		// fetching image files
		File[] imgFiles = new File[5];

		imgFiles[0] = new File(Dropbox.localPath + im0);
		imgFiles[1] = new File(Dropbox.localPath + im1);
		imgFiles[2] = new File(Dropbox.localPath + im2);
		imgFiles[3] = new File(Dropbox.localPath + im3);
		imgFiles[4] = new File(Dropbox.localPath + im4);

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
		finalImg.createGraphics().drawImage(buffImage[num], width / 2, height / 2, null);

		// naming the file
		ImageIO.write(finalImg, "jpg", new File(Dropbox.localPath + name));
	}
}