package com.kmaebashi.accesscounter;

import java.io.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

import com.kmaebashi.accesscounter.dbaccess.AccessCounterDbAccess;

public class AccessCounterServlet extends HttpServlet {
    private static Image[] numberImages = null;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
        if (numberImages == null) {
            loadNumberImages();
        }

        try {
            String counterId = request.getParameter("counterid");

            int count = AccessCounterDbAccess.getCount(counterId);

            BufferedImage image = generateImage(count);

            response.setHeader("pragma","no-cache");
            response.setHeader("Cache-Control","no-cache");

            response.setContentType("image/png");
            ImageIO.write(image, "png", response.getOutputStream());
        } catch (Exception ex) {
            throw new ServletException("Exception happend in AccessCounter", ex);
        }
    }

    private static BufferedImage generateImage(int counter) throws Exception {

        final int NUMBER_WIDTH = 15;
        final int NUMBER_HEIGHT = 20;
        final int NUMBER_OF_DIGITS = 7;

        int workCounter = counter;
        BufferedImage image;
        Graphics g = null;

        try {
            image = new BufferedImage(NUMBER_WIDTH * NUMBER_OF_DIGITS,
                                                NUMBER_HEIGHT,
                                                BufferedImage.TYPE_INT_RGB);
            g = image.createGraphics();
            for (int i = 0; i < NUMBER_OF_DIGITS; i++) {
                int num = workCounter % 10;

                g.drawImage(numberImages[num],
                            (NUMBER_OF_DIGITS - i - 1) * NUMBER_WIDTH,
                            0, null);

                workCounter /= 10;
            }
        } finally {
            g.dispose();
        }

        return image;
    }

    private void loadNumberImages() throws IOException {
        String imagesDirectory = this.getServletContext().getRealPath("WEB-INF/images");

        numberImages = new Image[10];
        for (int i = 0; i <= 9; i++) {
            String path = imagesDirectory + File.separator + i + ".png";
            numberImages[i] = ImageIO.read(new File(path));
        }
    }
}
