package com.qrcode.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.InputStreamSource;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class Application implements ApplicationRunner {

    private static Logger log = LoggerFactory.getLogger(Application.class);

    @Autowired
    private EmailService emailService;
    @Autowired
    private CSVUtilService csvUtilService;
    @Autowired
    private HTMLReadService htmlReadService;
    @Autowired
    private YAMLConfig yamlConfig;
    @Autowired
    private LogingService logingService;
    @Autowired
    private QRCodeGenService qrCodeGenService;
    @Autowired
    private ImageMergeService imageMergeService;
    @Autowired
    private ReadExcelService readExcelService;

    public static void main(String[] args) throws Exception {
       // System.setProperty("log.name", "mails");
        System.out.println("inside .... mail ");
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {

        System.out.println("yamlConfig.getEmailColumnName()" + yamlConfig.toString());

System.out.println("yamlConfig.getEmailColumnName()" + yamlConfig.getEmailColumnName());
        htmlReadService.setHtmlFileName(yamlConfig.getHtmlFileName());
        log.info("Spring Mail - Sending Email with Inline Attachment Example");
        //String html = htmlReadService.getHTML();
        // csvUtilService.setCsvFile(yamlConfig.getCsvFile());
        //List employees = csvUtilService.readCSVinList();

        readExcelService.setExcelFile(yamlConfig.getCsvFile());
        List<List> employees = readExcelService.readExcel();
        Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes> colHeadingAttributes =
                            readExcelService.readColHeadingParams();
        List fileHeader  = (List)employees.get(0);
        employees.remove(0);

        System.out.println("records to be processed " + employees.size());

        Iterator<List> itrEmployees =  employees.iterator();

        while (itrEmployees.hasNext()) {
            List employee = itrEmployees.next();

            if (employee.size()<fileHeader.size())
            {
                System.out.println("header size " + fileHeader.size());
                System.out.println("attribute count before " + employee.size());
                for (int i=employee.size();i<fileHeader.size();i++)
                {
                    employee.add("");
                }
                System.out.println("attribute count after " + employee.size());
            }
            //String employeeName = employee.get(0).toString();
            String finalContent = htmlReadService.parseHTML(fileHeader, employee);
            Mail mail = new Mail();
            mail.setFrom(yamlConfig.getFromAddress());
            //mail.setImageExtension(yamlConfig.getImageExtn());
            Iterator<String> itrFileHeader = fileHeader.iterator();
            int counter=0;
            List<String> emailTos = new ArrayList<String>();

            List<ImageAttributes> images = new ArrayList<ImageAttributes>();
            List<QRCodeAttributes> qrCodes = new ArrayList<QRCodeAttributes>();
            List<XLColumnHeadingAttributes> lstText = new ArrayList<>();

            List<ImageAttributes> mergeImages = new ArrayList<ImageAttributes>();
            List<QRCodeAttributes> mergeQrCodes = new ArrayList<QRCodeAttributes>();
            List<XLColumnHeadingAttributes> mergeLstText = new ArrayList<>();

            List<InputSourceStreamAttributes> inputStreamSources= new ArrayList<InputSourceStreamAttributes>();

            while(itrFileHeader.hasNext()) {
                String header= itrFileHeader.next();
                XLColumnHeadingAttributes attributes = new XLColumnHeadingAttributes();
                attributes.setName(header);
                attributes.setHeader(header);
                XLColumnHeadingAttributes resultAttribs = colHeadingAttributes.get(attributes);
                resultAttribs.setValue(employee.get(counter).toString());
                boolean toMerge = resultAttribs.getColumnType().contains(yamlConfig.getMergeValue());
                if (resultAttribs.getColumnType().contains(yamlConfig.getEmailColumnName())) {
                    //mail.setTo(employee.get(fileHeader.indexOf(emailColumnName)).toString());
                    if(!employee.get(counter).toString().equals(""))
                        emailTos.add(employee.get(counter).toString());
                }
                else if (resultAttribs.getColumnType().contains(yamlConfig.getTextColumnName()))
                {
                    if (toMerge) {
                        mergeLstText.add(resultAttribs);
                    }
                    else {
                        lstText.add(resultAttribs);
                    }
                }
                else if(resultAttribs.getColumnType().contains(yamlConfig.getImageColumnName()) ||
                        resultAttribs.getColumnType().contains(yamlConfig.getAttachmentColName()))
                {
                    ImageAttributes imageAttributes = new ImageAttributes();
                    imageAttributes.setHeader(resultAttribs.getName());
                    imageAttributes.setName(employee.get(counter).toString());
                    imageAttributes.setExtension(resultAttribs.getExtension());
                    if (resultAttribs.getColumnType().contains(yamlConfig.getAttachmentColName()))
                    {
                        imageAttributes.setExtension("");
                    }
//                    mail.setImageName(employee.get(fileHeader.indexOf(imageColumnName)).toString());
                    //images.add(employee.get(counter).toString());
                    if (toMerge) {
                        mergeImages.add(imageAttributes);
                    }else {
                        images.add(imageAttributes);
                    }
                    //imageHeaders.add(header);

                }
                else if(resultAttribs.getColumnType().contains(yamlConfig.getQrCodeColumnName()))
                {
                    QRCodeAttributes qrCodeAttributes= new QRCodeAttributes();
                    qrCodeAttributes.setHeader(resultAttribs.getName());
                    qrCodeAttributes.setQrCode(employee.get(counter).toString());
                    System.out.println("QRCODE Value to print "+qrCodeAttributes.getQrCode());
                    qrCodeAttributes.setExtension(resultAttribs.getExtension());
                    qrCodeAttributes.setScale(resultAttribs.getScale());
                    qrCodeAttributes.setBorder(resultAttribs.getBorder());
                    qrCodeAttributes.setX(resultAttribs.getX());
                    qrCodeAttributes.setY(resultAttribs.getY());
                    qrCodeAttributes.setBufferedImage(qrCodeGenService.getQRCodeImage(qrCodeAttributes));
                    if (toMerge) {
                        mergeQrCodes.add(qrCodeAttributes);
                    }
                    else {
                        qrCodes.add(qrCodeAttributes);

                    }
                }
                counter++;
            }
            mail.setTo(emailTos.toArray(new String[0]));
           // mail.setQrCodes(qrCodes);
            if (yamlConfig.getTemplateHTMLOrImage().equals("Image"))
            {
                mergeImages.addAll(images);
                mergeQrCodes.addAll(qrCodes);
                mergeLstText.addAll(lstText);
                inputStreamSources.add(mergeImages(mergeImages, mergeQrCodes, mergeLstText));
            }
            else {
                inputStreamSources.add(mergeImages(mergeImages, mergeQrCodes, mergeLstText));
                inputStreamSources.addAll(qrCodes);
                mail.setImageNames(images);
            }
            mail.setInputStreamSourceSources(inputStreamSources);
            mail.setSubject(yamlConfig.getSubject());
            mail.setContent(finalContent);
            try {
                emailService.sendSimpleMessage(mail);
                logingService.logDetails(mail, "Success");
            }
            catch (Exception e)
            {
                logingService.logDetails(mail, "Error - Not able to send email");
                System.out.println("Error - Not able to send email " + mail.getPrintMailData());
                e.printStackTrace(System.out);
            }
        }
    }

    private InputSourceStreamAttributes mergeImages(List<ImageAttributes> images,
                                              List<QRCodeAttributes> qrcodes,
                                                    List<XLColumnHeadingAttributes> lstText)
    {
        InputSourceStreamAttributes inputSourceStreamAttributes=null;
        try {
            inputSourceStreamAttributes = new InputSourceStreamAttributes();
            List otherImages = new ArrayList();
            Iterator<XLColumnHeadingAttributes> itrText = lstText.iterator();
            while(itrText.hasNext()) {
                XLColumnHeadingAttributes attributes = itrText.next();
                ImageAttributes imageAttributes = new ImageAttributes();
                imageAttributes.setName(attributes.getValue());
                imageAttributes.setBufferedImage(imageMergeService.getTextToImage(attributes));
                //imageAttributes.setX(30);
                //imageAttributes.setY(25);
                imageAttributes.setX(attributes.getX());
                imageAttributes.setY(attributes.getY());
                otherImages.add(imageAttributes);
            }
            inputSourceStreamAttributes.setInputStreamSource(
                    imageMergeService.mergeImages(images, qrcodes, otherImages, yamlConfig));

            inputSourceStreamAttributes.setHeader(yamlConfig.getMasterImageName());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return inputSourceStreamAttributes;
    }
    public void writeToCsv(XLColumnHeadingAttributes xlColumnHeadingAttributes) {
        try {
            xlColumnHeadingAttributes.setExecDateTime(Calendar.getInstance().getTime().toString());
            csvUtilService.writeToCsv(xlColumnHeadingAttributes, csvLocation);
        } catch (Exception e) {
            logger.error("Exception occurred when writing a row to excel sheet. xLColumnHeadingAttributes: " + xlColumnHeadingAttributes.toString(), e);
        }
    }
}
