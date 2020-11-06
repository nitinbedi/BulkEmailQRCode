package com.qrcode.mail;

import com.barclays.mortgages.paymentholiday.ValidationUtil;
import com.barclays.mortgages.paymentholiday.XLColumnHeadingAttributes;
import io.poi.reader.XlsxReader;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


@Service
public class ReadExcelService {

    private static Logger logger = LoggerFactory.getLogger(ReadExcelService.class);
    private String excelFile;

    public String getExcelFile() {
        return excelFile;
    }

    public void setExcelFile(String csvFile) {
        this.excelFile = csvFile;
    }

    public static void main(String[] args) throws Exception {
        /*List<List> lst = readExcel("upload.xlsx",1);
*/
        XlsxReader reader = new XlsxReader();
        Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes> hs
                = reader.readHash(XLColumnHeadingAttributes.class,
                new File("C:\\paymentHoliday\\splitText.xlsm"),2);
        Set s = hs.keySet();
        Iterator<XLColumnHeadingAttributes> keys =  s.iterator();
        Iterator<XLColumnHeadingAttributes> itrValues = hs.values().iterator();
        int i=0;
        while (keys.hasNext())
        {
            XLColumnHeadingAttributes xl = keys.next();

            logger.info(" xl.getName = " + xl.getFirstName()  +
                    " xl.getMortgageAccountNumber = " + xl.getMortgageAccountNumber() +
                    " xl.getDob = " + xl.getDob() +
                    " xl.getPostCode = " + xl.getPostCode() +
                    " xl.getHolidayTerm = " + xl.getHolidayTerm());
            logger.info(" converted DOB "+ ValidationUtil.dateConvertor(xl.getDob()));
            i++;
            //logger.infoln("keys.next() = " + keys.next());
        }
        logger.info(" total count= " + i);

        /*
        XLColumnHeadingAttributes xlAtrs = new XLColumnHeadingAttributes();
        xlAtrs.setColumnType("image");
        xlAtrs.setName("image.png");
        XLColumnHeadingAttributes xl = ls.get(xlAtrs);

        Iterator<XLColumnHeadingAttributes> itr =  ls.values().iterator();
        while (itr.hasNext()) {
            xl = itr.next();
        }
*/
    }

    public  Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes>
                        readColHeadingParams() throws Exception {
        Map map = readColHeadingParams(getExcelFile());
        return map;
    }

    public  Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes>
                        readColHeadingParams(int sheet) throws Exception {
        Map map = readColHeadingParams(getExcelFile(), sheet);
        return map;
    }


    public  Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes>
                        readColHeadingParams(String filename) throws Exception {
        Map map = readColHeadingParams(filename, 0);
        return map;
    }

    public  Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes>
                        readColHeadingParams(String filename, int sheet) throws Exception {
        XlsxReader reader = new XlsxReader();
        logger.debug("File to read is "+ filename);
        Map<XLColumnHeadingAttributes, XLColumnHeadingAttributes> map =
                        reader.readHash(XLColumnHeadingAttributes.class,
                                    new File(filename), sheet);
                                    //new ClassPathResource(filename).getFile(),sheet);
        return map;
    }

    public  List readExcel() throws Exception
    {
        return readExcel(getExcelFile());
    }

    public  List readExcel(String FILE_NAME) throws Exception
    {
        return readExcel(FILE_NAME, 1);
    }

    public  List readExcel(String FILE_NAME, int sheet) throws Exception
    {
        try {
            FileInputStream excelFile = new FileInputStream((new ClassPathResource(FILE_NAME)).getFile());
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(sheet);
            Iterator<Row> iterator = datatypeSheet.iterator();
            List<List> lstRow = new ArrayList<List>();
            while (iterator.hasNext()) {
                List<String> lstColumns = new ArrayList<String>();
                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                while (cellIterator.hasNext()) {

                    Cell currentCell = cellIterator.next();
                    //getCellTypeEnum shown as deprecated for version 3.15
                    //getCellTypeEnum ill be renamed to getCellType starting from version 4.0
                    if (currentCell.getCellTypeEnum() == CellType.STRING) {
                        logger.debug("{}--", currentCell.getStringCellValue());
                        lstColumns.add(currentCell.getStringCellValue());
                    } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                    	logger.debug("{}--", currentCell.getNumericCellValue());
                        lstColumns.add(currentCell.getNumericCellValue()+"");
                    }
                }
                lstRow.add(lstColumns);
            }
            return lstRow;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
