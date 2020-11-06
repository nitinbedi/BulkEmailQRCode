package com.qrcode.mail;

import javax.imageio.ImageIO;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.awt.*;

import java.awt.image.BufferedImage;

import java.io.File;

public class Test {


    public static void main(String[] args) throws Exception {
        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
            System.out.println(url.getFile());
        }


        /*
        ImageMergeService imageMergeService=null;

        imageMergeService = new ImageMergeService();

        imageMergeService.getTextToImage("Nitin Bedi");
        */
    }

    public static void callMe() throws Exception {
        System.out.println("inside ");
        CSVUtilService csvUtilService = new CSVUtilService();
        csvUtilService.setCsvFile("upload.csv");
        ImageMergeService imageMergeService=new ImageMergeService();
        QRCodeGenService qrCodeGenService=new QRCodeGenService();
        List employees = csvUtilService.readCSVinList();
        List fileHeader = (List)employees.get(0);

        File f = new File("C:\\emailsolve\\target\\classes\\img001.png");

        BufferedImage bf = ImageIO.read(f);

        employees.remove(0);

        Iterator<List> itrEmployees =  employees.iterator();
        int employeCnt=0;
        while (itrEmployees.hasNext()) {
            List employee = itrEmployees.next();

            Iterator<String> itrFileHeader = fileHeader.iterator();
            int counter = 0;

            BufferedImage combined = new BufferedImage(bf.getWidth(), bf.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            Graphics g = combined.getGraphics();
            g.drawImage(bf, 0, 0, null);
            System.out.println("Name is "+employee.get(0).toString());
            //g.drawImage(imageMergeService.getTextToImage(employee.get(0).toString()),10,10,null);

            while (itrFileHeader.hasNext()) {
                String header = itrFileHeader.next();


                QRCodeAttributes qrCodeAttributes=null;
                if(header.contains("qrcode.")) {
                    qrCodeAttributes = new QRCodeAttributes();
                    qrCodeAttributes.setHeader(header);
                    qrCodeAttributes.setQrCode(employee.get(counter).toString());
                    qrCodeAttributes.setExtension("png");
                    qrCodeAttributes.setScale(4);
                    qrCodeAttributes.setBorder(1);
                    qrCodeAttributes.setX(377);
                    qrCodeAttributes.setY(562);

                    g.drawImage(qrCodeGenService.getQRCodeImage(qrCodeAttributes),
                            qrCodeAttributes.getX(), qrCodeAttributes.getY(),
                            //qrCodeAttributes.getWidth(), qrCodeAttributes.getHeight(),
                            null);
                }
                counter++;
            }
            ImageIO.write(combined, "PNG",
                    new File("C:\\emailsolve\\target\\classes\\combined"+employeCnt+".png"));
            employeCnt++;

        }



    }
/*
    private static BufferedImage getTextToImage(String text) throws IOException
    {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);// Represents an image with 8-bit RGBA color components packed into integer pixels.
        Graphics2D graphics2d = image.createGraphics();
        Font font = new Font("TimesNewRoman", Font.BOLD, 24);
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        int width = fontmetrics.stringWidth(text);
        int height = fontmetrics.getHeight();
        graphics2d.dispose();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2d.setFont(font);
        fontmetrics = graphics2d.getFontMetrics();
        graphics2d.setColor(Color.GREEN);
        graphics2d.drawString(text, 0, fontmetrics.getAscent());
        graphics2d.dispose();
        try {
            ImageIO.write(image, "png", new File("C:\\emailsolve\\target\\classes\\"+text+".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return image;
    }
    private static BufferedImage getQRCodeImage(QRCodeAttributes qrCodeAttributes)
    {
        String text = qrCodeAttributes.getQrCode();          // User-supplied Unicode text
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
        QrCode qr = QrCode.encodeText(text, errCorLvl);  // Make the QR Code symbol
        BufferedImage img = qr.toImage(qrCodeAttributes.getScale(), qrCodeAttributes.getBorder());           // Convert to bitmap image

        return img;
    }
*/
}

