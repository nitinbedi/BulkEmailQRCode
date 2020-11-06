
package com.qrcode.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties("setup")
public class YAMLConfig {
    //@NotNull
    private String emailColumnName;
    private String subject;
    private String fromAddress;
    private String csvFile;
    private String htmlFileName;
    private String imageExtn;
    private String imageColumnName;
    private String qrCodeColumnName;
    private String attachmentColName;
    private String masterImageName;
    private String templateHTMLOrImage;
    private String textColumnName;
    private String mergeValue;


    public String getMergeValue() {
        return mergeValue;
    }

    public void setMergeValue(String mergeValue) {
        this.mergeValue = mergeValue;
    }

    public String getTextColumnName() {
        return textColumnName;
    }

    public void setTextColumnName(String textColumnName) {
        this.textColumnName = textColumnName;
    }

    public String getTemplateHTMLOrImage() {
        return templateHTMLOrImage;
    }

    public void setTemplateHTMLOrImage(String templateHTMLOrImage) {
        this.templateHTMLOrImage = templateHTMLOrImage;
    }

    public String getMasterImageName() {
        return masterImageName;
    }

    public void setMasterImageName(String masterImageName) {
        this.masterImageName = masterImageName;
    }

    public String getAttachmentColName() {
        return attachmentColName;
    }

    public void setAttachmentColName(String attachmentColName) {
        this.attachmentColName = attachmentColName;
    }

    public String getQrCodeColumnName() {
        return qrCodeColumnName;
    }

    public void setQrCodeColumnName(String qrCodeColumnName) {
        this.qrCodeColumnName = qrCodeColumnName;
    }

    public String getImageColumnName() {
        return imageColumnName;
    }

    public void setImageColumnName(String imageColumnName) {
        this.imageColumnName = imageColumnName;
    }

    @Override
    public String toString() {
        return "YAMLConfig{" +
                "emailColumnName='" + emailColumnName + '\'' +
                ", subject='" + subject + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", csvFile='" + csvFile + '\'' +
                ", htmlFileName='" + htmlFileName + '\'' +
                ", imageExtn='" + imageExtn + '\'' +
                ", imageColumnName='" + imageColumnName + '\'' +
                ", attachmentColName='" + attachmentColName + '\'' +

                '}';
    }

    public String getImageExtn() {
        return imageExtn;
    }

    public void setImageExtn(String imageExtn) {
        this.imageExtn = imageExtn;
    }

    public String getEmailColumnName() {
        return emailColumnName;
    }

    public void setEmailColumnName(String emailColumnName) {
        this.emailColumnName = emailColumnName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }

    public String getHtmlFileName() {
        return htmlFileName;
    }

    public void setHtmlFileName(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }
// standard getters and setters

}

