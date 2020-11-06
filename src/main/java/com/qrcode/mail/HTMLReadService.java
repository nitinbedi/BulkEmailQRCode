package com.qrcode.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class HTMLReadService {

	private static Logger logger = LoggerFactory.getLogger(HTMLReadService.class);

private String htmlFileName;
private String htmlContent;

    public String getHtmlFileName() {
        return htmlFileName;
    }

    public void setHtmlFileName(String htmlFileName) {
        this.htmlFileName = htmlFileName;
    }




    public static void main(String[] args) throws Exception {
//C:\emailsolve\src\main\resources\
        String htmlFileName = "Doc2.htm";
        byte[] b=null;

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        URL[] urls = ((URLClassLoader)cl).getURLs();

        for(URL url: urls){
        	logger.debug(url.getFile());
        }


        /*String contents;
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (classPathResource.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }

        contents=textBuilder.toString();
*/
    }


    public  String getHTML2() throws Exception {
        // String htmlFile = "/Users/mkyong/csv/country2.csv";
        logger.info("HTML File Name: {}", htmlFileName);
        byte[] b=null;

        if(htmlFileName == null || htmlFileName.isEmpty()){
            return "";
        }

        InputStream inputstream = new FileInputStream(htmlFileName);
        String contents;
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputstream, Charset.forName(StandardCharsets.UTF_8.name()))))
        {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception occurred when reading html.", e);
            throw e;
        }
        contents=textBuilder.toString();
        return contents;
    }

    public  String getHTML() throws Exception {
       // String htmlFile = "/Users/mkyong/csv/country2.csv";
    	logger.info("HTML File Name: {}", htmlFileName);
        byte[] b=null;
        ClassPathResource classPathResource = new ClassPathResource(htmlFileName);
        //Paths.get(htmlFileName)
        //String contents =new String(Files.readAllBytes(Paths.get(htmlFileName)));
        //String contents =new String(Files.readAllBytes(classPathResource.getFile().toPath()));
        String contents;
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (classPathResource.getInputStream(), Charset.forName(StandardCharsets.UTF_8.name()))))
        {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        catch(Exception e)
        {
            printClassPath();
            e.printStackTrace();
            throw e;
        }
        contents=textBuilder.toString();
        return contents;
    }
    private void printClassPath()
    {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        URL[] urls = ((URLClassLoader)cl).getURLs();
        logger.error("Class path to refer is : ");
        for(URL url: urls){
        	logger.error(url.getFile());
        }
    }

    public  String parseHTML(List header, List employee) throws Exception {

        // String htmlFile = "/Users/mkyong/csv/country2.csv";
        String contents;
        if (htmlContent==null || htmlContent.equals(""))
        {
            htmlContent= getHTML();
        }
        contents = htmlContent;
        int csvColumnCount =header.size();
        String valueToReplaceColumn="";
        for(int i=0;i<csvColumnCount;i++) {
            if (i<employee.size())
                valueToReplaceColumn=employee.get(i).toString();
            else
                valueToReplaceColumn="";
            contents = contents.replaceAll("<<"+header.get(i).toString() +">>", valueToReplaceColumn);
        }
    return contents;

    }
}
