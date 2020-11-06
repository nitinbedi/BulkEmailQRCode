package com.qrcode.mail;

import com.barclays.mortgages.paymentholiday.service.*;
import io.poi.SpreadsheetReadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    @Autowired
    private EmailService emailService;

    @Autowired
    private YAMLConfig yamlConfig;
    @Autowired
    private LogingService logingService;

    @Autowired
    private HTMLReadService htmlReadService;

    @Autowired
    private ReadExcelService readExcelService;

    @Autowired
    private ZipService zipService;

    @Autowired
    Orchestration orchestrationImpl;

    @Autowired
    CSVUtilService csvUtilService;

    @Bean
    public boolean loadSystemProperties(YAMLConfig yamlConfig){
        System.getProperties().setProperty("javax.net.ssl.keyStoreType", "jks");
        System.getProperties().setProperty("javax.net.ssl.trustStoreType", "jks");
        System.getProperties().setProperty("javax.net.ssl.keyStore",
                yamlConfig.getKeystorePath());
        System.getProperties().setProperty("javax.net.ssl.trustStore",
                yamlConfig.getTruststorePath());
        System.getProperties().setProperty("javax.net.ssl.keyStorePassword", yamlConfig.getKeystorePassword());
        System.getProperties().setProperty("javax.net.ssl.trustStorePassword", yamlConfig.getTruststorePassword());
        return true;
    }

    public static void main(String[] args) throws Exception {
        // System.setProperty("log.name", "mails");
        long startTime = System.currentTimeMillis();

        logger.info("inside .... main ");
        SpringApplication.run(Application.class, args);

        long end = System.currentTimeMillis() - startTime;
        logger.info("Total millis taken: " + end);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        logger.info("inside run method..");
        try {
            while (true) {
                try {
                    logger.info("************* Process started *************.");
                    executeProcess();
                    logger.info("************* Process completed *************.");
                } catch (SpreadsheetReadException fileNotFoundEx) {
                    logger.info("Input file not found, waiting to get the file.");
                } catch (Exception e) {
                    logger.error("Error occurred in method run: ", e);
                    // Send the email
                    try {
                        logger.debug("Sending email attachment");
                        String finalContentPart1 = yamlConfig.getErrorHtmlPart1() + e.toString() + "<BR/> " + e.getMessage();
                        sendEmail(null,
                                yamlConfig.getErrorEmailSubject(), finalContentPart1 + yamlConfig.getErrorHtmlPart2(), null);
                        throw e;
                    } catch (Exception exception) {
                        logger.error("Email sending failed.", exception);
                        throw exception;
                    }
                } finally {
                    Thread.sleep(yamlConfig.getSleepTime());
                }
            }
        }
        catch(Throwable th){
                logger.error("While loop terminated... ", th);
                throw new Exception("program aborted", th);
            }
        finally
            {
                logger.error("Program aborted successfully");
            }
    }

    public void executeProcess() throws Exception {
        String csvLocation = "";
        String xlsLocation = "";
        String htmlLocation = yamlConfig.getHtmlFile();

         int inputFileCount = StringUtils.isEmpty(yamlConfig.getInputFileCount()) ? 0 : Integer.valueOf(yamlConfig.getInputFileCount());

        for (int i = 0; i <= inputFileCount; i++) {
            try {
                String fileName;
                String outputFileName;
                if (i == 0) {
                    fileName = yamlConfig.getXlsFile();
                    xlsLocation = yamlConfig.getFolderPath() + fileName;
                    outputFileName = yamlConfig.getCsvFileToWrite();
                    csvLocation = yamlConfig.getOutputCsvFolderPath() + outputFileName;
                }
                else {
                    fileName = yamlConfig.getXlsFile().split("\\.")[0] + i + "." + yamlConfig.getXlsFile().split("\\.")[1];
                    xlsLocation = yamlConfig.getFolderPath() + fileName;;
                    outputFileName = yamlConfig.getCsvFileToWrite().split("\\.")[0] + i + "." + yamlConfig.getCsvFileToWrite().split("\\.")[1];
                    csvLocation = yamlConfig.getOutputCsvFolderPath() + outputFileName;
                }
                logger.info("Input file with location: " + xlsLocation);
                logger.info("Output file with location: " + csvLocation);
                logger.info("Input file name: " + fileName);
                logger.info("Output file name: " + outputFileName);
                //   logger.info("Input file name is: " + fileNameWithPath);
                multipleFileExecution(xlsLocation, csvLocation, htmlLocation, fileName, outputFileName);
            } catch (SpreadsheetReadException spreadsheetReadException) {
                logger.info("Input file not found, waiting to get the file.");
            } catch (FileNotFoundException fileNotFoundEx){
                logger.info("Input file not found, waiting to get the file.");
            }
        }
    }

    private void multipleFileExecution(String xlsLocation, String csvLocation, String htmlLocation, String fileName, String outputFileName) throws Exception {
        String csvExtention = "csv";
        Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes> colHeadingAttributes = null;
        File file = new File(xlsLocation);

        if(!file.exists()){
            xlsLocation = xlsLocation.split("\\.")[0] + "." + csvExtention;
            fileName = fileName.split("\\.")[0] + "." + csvExtention;
            colHeadingAttributes = readFromCSV(xlsLocation);
        } else if(fileName.split("\\.")[1].equalsIgnoreCase(csvExtention)) {
            colHeadingAttributes = readFromCSV(xlsLocation);
        }   else {
            colHeadingAttributes = readFromExcel(xlsLocation, yamlConfig.getCsvSheet());
        }

        if (colHeadingAttributes==null || colHeadingAttributes.size()==0) {
            logger.error("Either there is no third tab in sheet or third tab is empty.");
            try {
                archiveFiles(xlsLocation, csvLocation, yamlConfig.getArchivePath(), fileName, outputFileName);
            } catch(Exception e) {
                logger.error("Error occurred while archiving input file.", e);
                throw e;
            }
            return;
        }
        logger.info("Total number of records in excel sheet are: " + colHeadingAttributes.size());

        orchestrationImpl.setCsvLocation(csvLocation);
        orchestrationImpl.executePaymentHoliday(colHeadingAttributes);

        try {
            if (yamlConfig.getSendEmailFlag().equalsIgnoreCase("Y")) {
                logger.debug("Sending email attachment");
                List emailsTo = new ArrayList();
                emailsTo.add(yamlConfig.getEmailTo());
                htmlReadService.setHtmlFileName(htmlLocation);
                String finalContent = htmlReadService.getHTML2();
                sendEmail(yamlConfig.getOutputCsvFolderPath(),
                        yamlConfig.getSubject(), finalContent, outputFileName);
            }
        }
        catch(Exception e) {
            logger.error("Error while sending email with output file " + e);
        }
        try {
            archiveFiles(xlsLocation, csvLocation, yamlConfig.getArchivePath(), fileName, outputFileName);
        } catch(Exception e) {
            logger.error("Error while archiving the files " + e);
            throw new Exception("Archival of files unsuccessful program should abort ",e);
        }

    }

    private void archiveFiles(String inputFileLocation, String outputFileLocation, String archivePath, String inputFileName,
                              String outputFilename) throws Exception {
        if (yamlConfig.getArchiveFlag().equalsIgnoreCase("Y")) {
            logger.info("Archiving input and output files.");
            String archiveTimestamp  = Instant.now().truncatedTo( ChronoUnit.SECONDS ).toString().replace( "-" , "" ).
                    replace(":","");
            logger.debug("archiving input file: " + inputFileLocation);
            archiveFiles(inputFileLocation, archivePath, inputFileName, archiveTimestamp);
            logger.debug("archiving output file: " + outputFileLocation);
            archiveFiles(outputFileLocation, archivePath, outputFilename, archiveTimestamp);
        } else {
            logger.info("Can not archive files as archiveFlag is not set to Y.");
        }
    }

    public void archiveFiles(String xlsLocation, String archiveLocation, final String fileName, String archiveTimestamp) throws Exception
    {
        File fileToMove = new File(xlsLocation);
        if(!fileToMove.exists()){
            logger.error("File to archive does not exist.");
            return;
        }
        logger.debug(" value of the object " + fileName + " at zero is ");
        String[] strFileNameArr = fileName.split("\\.");
        logger.debug(" Length of the array of file name object is " + fileName + " " + strFileNameArr.length);
        logger.debug(" value of the object " + fileName + " at zero is " + strFileNameArr[0]);
        String finalFileName = archiveLocation + strFileNameArr[0] + "-" +
                archiveTimestamp
                + "."
                + strFileNameArr[1];

        boolean isMoved = fileToMove.renameTo(new File(finalFileName));

        if (!isMoved) {
            throw new FileSystemException(finalFileName);
        }
        try {
            Path path = Paths.get(finalFileName);
            if (!Files.exists(path)) Files.createFile(path);
            Set<PosixFilePermission> perms = Files.readAttributes(path, PosixFileAttributes.class).permissions();

            logger.debug("Permissions before: %s%n", PosixFilePermissions.toString(perms));
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_EXECUTE);
            perms.add(PosixFilePermission.GROUP_READ);
            perms.add(PosixFilePermission.OTHERS_READ);
            Files.setPosixFilePermissions(path, perms);
            logger.debug("Permissions after: %s%n", PosixFilePermissions.toString(perms));
        } catch (Exception e) {
            logger.error("Exception occurred while adding permissions to file.", e);
        }
    }

    public Map readFromExcel(String xlsLocation, int SheetNo) throws Exception
    {
        logger.debug("yamlConfig.getEmailColumnName()" + yamlConfig.toString());

        logger.debug("Reading from the excel file, START");

        readExcelService.setExcelFile(xlsLocation);

        Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes> colHeadingAttributes =
                readExcelService.readColHeadingParams(SheetNo);
        logger.debug("Reading from the excel file, END");
        return colHeadingAttributes;
    }

    public Map readFromCSV(String xlsLocation) throws Exception {
            logger.debug("Reading from the csv file, START");
            Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes> colHeadingAttributes = csvUtilService.readCSVinList(XLColumnHeadingAttributes.class, xlsLocation);
            logger.debug("Reading from the csv file, END");
            return colHeadingAttributes;

    }

    public void sendEmail(String attachmentFolderLocation,
                          String subject, String finalContent, String outputFileName) throws Exception {
        logger.info("Sending EMAIL with content: " + finalContent);
        Mail mail = new Mail();
        mail.setFrom(yamlConfig.getFromAddress());
        File zipFile = null;

        if(outputFileName != null && yamlConfig.getMaxAttachmentSizeInMb() != null) {
            int maxFileSizeInMB = Integer.valueOf(yamlConfig.getMaxAttachmentSizeInMb());
            File outputFile = new File(attachmentFolderLocation + outputFileName);
            List<ImageAttributes> imageNames = new ArrayList<ImageAttributes>();
            if(ValidationUtil.convertToMB(outputFile.length()) > maxFileSizeInMB){
                // If file size is greater than maxFileSizeInMB then zip and attach
                String zipFileName = zipService.zipAttachmentFile(attachmentFolderLocation, outputFileName);
                zipFile = new File(attachmentFolderLocation + zipFileName);
                if(ValidationUtil.convertToMB(zipFile.length()) < maxFileSizeInMB) {
                    imageNames.add(attachment2(attachmentFolderLocation + zipFileName, zipFileName));
                    mail.setImageNames(imageNames);
                }
            } else {
                // If file size is less than maxFileSizeInMB then attach file as it is
                imageNames.add(attachment2(attachmentFolderLocation + outputFileName, outputFileName));
                mail.setImageNames(imageNames);
            }
        }
        //mail.setTo(emailTos.toArray(new String[0]));
        mail.setTo(yamlConfig.getEmailTo().split(","));
        mail.setSubject(subject);
        mail.setContent(finalContent);
        try {
            emailService.sendSimpleMessage(mail);
            logingService.logDetails(mail, "Success");
        }
        catch (Exception e)
        {
            logingService.logDetails(mail, "Error - Not able to send email");
            logger.error("Error - Not able to send email " + mail.getPrintMailData());
            e.printStackTrace(System.out);
            throw e;
        } finally {
            if(zipFile != null && zipFile.exists()) {
                zipFile.delete();
            }
        }
    }

    public ImageAttributes attachment2(String location, String outPutFileName)
    {
        ImageAttributes imageAttributes=new ImageAttributes();
        imageAttributes.setFilePath(location);
        imageAttributes.setName(outPutFileName.split("\\.")[0]);
        imageAttributes.setExtension(outPutFileName.split("\\.")[1]);
        imageAttributes.setHeader(outPutFileName);

        return imageAttributes;
    }


}

