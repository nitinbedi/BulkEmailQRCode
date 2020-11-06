package com.qrcode.mail;

import io.nayuki.qrcodegen.QrCode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;


@Service
public class ImageMergeService extends ConvertToInputStreamService{


    public InputStreamSource mergeImages(List<ImageAttributes> images, List<QRCodeAttributes> qrcodes,
                                         List<ImageAttributes> otherImages, YAMLConfig yamlConfig )
            throws IOException
    {
        if (images==null
            || images.size()<=0)
        {
            return null;
        }
        System.out.println("images.get(0).getName() " +images.get(0).getName());
        String imageName = null;// images.get(0).getName();
        ClassPathResource classPathResource =
                new ClassPathResource(images.get(0).getName()+"."+images.get(0).getExtension());

        InputStream inputStream = classPathResource.getInputStream();
        BufferedImage bf =  ImageIO.read(inputStream);
System.out.println("bf.getWidth(), bf.getHeight()"+bf.getWidth()+ " " + bf.getHeight());
//        BufferedImage bf = images.get(0).getBufferedImage();
//        images.remove(0);
        Iterator<ImageAttributes> itrImages =  images.iterator();
        Iterator<QRCodeAttributes> itrQRCodes =  qrcodes.iterator();
        Iterator<ImageAttributes> itrOtherImages =  otherImages.iterator();

        int employeCnt=0;

        BufferedImage combined = new BufferedImage(bf.getWidth(), bf.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        while (itrImages.hasNext()) {
            ImageAttributes imageAttributes= itrImages.next();

            classPathResource =
                    new ClassPathResource(imageAttributes.getName()+"."+imageAttributes.getExtension());

            inputStream = classPathResource.getInputStream();
            Graphics g = combined.getGraphics();
            g.drawImage(ImageIO.read(inputStream), imageAttributes.getX(),
                        imageAttributes.getY(), null);

        }
        String qrCodeValue=null;
        while (itrQRCodes.hasNext()) {
            QRCodeAttributes qrCodeAttributes= itrQRCodes.next();
            qrCodeValue=qrCodeAttributes.getQrCode();
            Graphics g = combined.getGraphics();
            System.out.println("qrCodeAttributes.getX(),qrCodeAttributes.getY()" +
                            qrCodeAttributes.getX()+" " + qrCodeAttributes.getY());
            g.drawImage(qrCodeAttributes.getBufferedImage(), qrCodeAttributes.getX(),
                    qrCodeAttributes.getY(), null);
        }

        while (itrOtherImages.hasNext()) {
            ImageAttributes imageAttributes= itrOtherImages.next();
            imageName =imageAttributes.getName();
            Graphics g = combined.getGraphics();
            g.drawImage(imageAttributes.getBufferedImage(), imageAttributes.getX(),
                    imageAttributes.getY(), null);
        }
        employeCnt++;
        System.out.println("Going to write merged image....");
        ImageIO.write(combined, yamlConfig.getImageExtn(),
                new File("C:\\emailsolve\\png\\C_"+imageName+qrCodeValue+"."+yamlConfig.getImageExtn()));
        return imageToInputStream(combined,yamlConfig.getImageExtn());
    }

    public BufferedImage getTextToImage(XLColumnHeadingAttributes imageAttribs)
    {
        //text="Dear " + imageAttribs.getValue();
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);// Represents an image with 8-bit RGBA color components packed into integer pixels.
        Graphics2D graphics2d = image.createGraphics();
        //Font font = new Font("Harrington", Font.PLAIN, 30);
        Font font = new Font(imageAttribs.getFontName(), imageAttribs.getFontType(), imageAttribs.getFontSize());
        graphics2d.setFont(font);
        FontMetrics fontmetrics = graphics2d.getFontMetrics();
        int width = fontmetrics.stringWidth(imageAttribs.getValue());
        int height = fontmetrics.getHeight();
        graphics2d.dispose();

        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2d = image.createGraphics();
        graphics2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        graphics2d.setFont(font);
        fontmetrics = graphics2d.getFontMetrics();
        //graphics2d.setColor(Color.getHSBColor(146,255,96));
        //graphics2d.setColor(Color.BLUE);

        graphics2d.setColor(Color.getColor(imageAttribs.getFontColor()));

        graphics2d.drawString(imageAttribs.getValue(), 0, fontmetrics.getAscent());
        graphics2d.dispose();
        *try {
            ImageIO.write(image, "png", new File("C:\\emailsolve\\target\\classes\\"+text+".png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }*
        return image;
    }

}
