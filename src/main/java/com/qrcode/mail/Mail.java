package com.qrcode.mail;

import java.util.List;

public class Mail {

    private String from;
    private String[] to;
    private String subject;
    private String content;
    private String printMailData;


    private List<ImageAttributes> imageNames;

    private List<InputSourceStreamAttributes> inputStreamSourceSources;

    public String getPrintMailData() {
        String mailString= "Mail sent" ;

        for (int i=0;i<to.length;i++) {
            mailString = mailString + ", to["+i+"]='" + to[i] + '\'';
        }
        return mailString;
        //return printMailData;
    }


    public List<InputSourceStreamAttributes> getInputStreamSourceSources() {
        return inputStreamSourceSources;
    }

    public void setInputStreamSourceSources(List<InputSourceStreamAttributes> inputStreamSourceSources) {
        this.inputStreamSourceSources = inputStreamSourceSources;
    }


    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }

    private String imageExtension;

    public Mail() {
    }

    public Mail(String from, String[] to, String subject, String content, List imageNames) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.imageNames=imageNames;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String[] getTo() {
        return to;
    }

    public void setTo(String[] to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List getImageNames() {
        return imageNames;
    }

    public void setImageNames(List imageNames) {
        this.imageNames = imageNames;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        String mailString= "Mail{" +
                "from='" + from + '\'';
                for (int i=0;i<to.length;i++) {
                    mailString = mailString + ", to["+i+"]='" + to[i] + '\'';
                }
        mailString=mailString+ ", subject='" + subject + '\'' +
                ", content='" + content + '\'' +
                '}';
        return mailString;
    }
}
