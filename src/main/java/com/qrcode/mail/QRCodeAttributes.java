
package com.qrcode.mail;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class QRCodeAttributes extends InputSourceStreamAttributes{

    String qrCode;
    BufferedImage bufferedImage;

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String name) {
        this.qrCode= name;
    }

    public InputStreamSource getInputStreamSource() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStreamSource source ;
        try {
            ImageIO.write(bufferedImage, extension, baos);
        } catch (IOException ex) {
            //handle it here.... not implemented yet...
            ex.printStackTrace();
        }
        source = new
                ByteArrayResource(baos.toByteArray());
        return source;
    }
}