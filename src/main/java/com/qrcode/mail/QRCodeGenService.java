
package com.qrcode.mail;

import io.nayuki.qrcodegen.QrCode;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


@Service
public class QRCodeGenService extends ConvertToInputStreamService{


    public InputStreamSource getQRCodeInputSource(QRCodeAttributes qrCodeAttributes) throws MessagingException
    {
        String text = qrCodeAttributes.getQrCode();          // User-supplied Unicode text
        BufferedImage img = getQRCodeImage(qrCodeAttributes);
        System.out.println("going to save file .. "+qrCodeAttributes.getQrCode()+"."+qrCodeAttributes.getExtension());
        return imageToInputStream(img,qrCodeAttributes.getExtension());
    }


    public BufferedImage getQRCodeImage(QRCodeAttributes qrCodeAttributes) throws MessagingException
    {
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;  // Error correction level
        QrCode qr = QrCode.encodeText(qrCodeAttributes.getQrCode(), errCorLvl);  // Make the QR Code symbol
        BufferedImage img = qr.toImage(qrCodeAttributes.getScale(), qrCodeAttributes.getBorder());           // Convert to bitmap image
        return img;
    }

}

