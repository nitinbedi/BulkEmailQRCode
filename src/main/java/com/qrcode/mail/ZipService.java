package com.qrcode.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    private static Logger logger = LoggerFactory.getLogger(ZipService.class);

    public String zipAttachmentFile(String attachmentFolderLocation, String fileName) {
        {
            FileInputStream csvFileInputStream = null;
            FileOutputStream fos = null;
            ZipOutputStream zos = null;
            String zipFileName = fileName.split("\\.")[0] + ".zip";
            try {
                csvFileInputStream = new FileInputStream(attachmentFolderLocation + fileName);
                fos = new FileOutputStream(attachmentFolderLocation + zipFileName);
                zos = new ZipOutputStream(fos);
                zos.putNextEntry(new ZipEntry(fileName));

                byte[] buffer = new byte[1024];
                int len;
                while ((len = csvFileInputStream.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
            } catch (FileNotFoundException e) {
                logger.error("FileNotFoundException occurred while zipping the file.", e);
            } catch (IOException e) {
                logger.error("IOException occurred while zipping the file.", e);
            } finally {
                try {
                    if (csvFileInputStream != null) {
                        csvFileInputStream.close();
                    }
                    if(fos != null){
                        fos.close();
                    }
                    if(zos !=null) {
                        zos.closeEntry();
                    }
                    if(zos != null){
                        zos.close();
                    }
                } catch (Exception e){
                    logger.error("Exception occurred while closing resources.");
                }
            }
            return zipFileName;
        }
    }
}

