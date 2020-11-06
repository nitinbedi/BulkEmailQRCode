
package com.qrcode.mail;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;


@Service
public class ConvertToInputStreamService {


    public InputStreamSource imageToInputStream(BufferedImage img, String extension) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(img, extension, baos);
        } catch (IOException ex) {
            //handle it here.... not implemented yet...
            ex.printStackTrace();
        }

        InputStreamSource source ;
        source = new
                ByteArrayResource(baos.toByteArray());
        return source;

    }

    public InputStreamSource fileToInputStream(BufferedInputStream img, String extension) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStreamSource source =new InputStreamResource(img);
        source = new
                ByteArrayResource(baos.toByteArray());

        return source;

    }
}

