package com.qrcode.mail;

import com.barclays.mortgages.paymentholiday.XLColumnHeadingAttributes;
import io.poi.model.annotations.SheetColumn;

import io.poi.util.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.io.IOException;

@Service
public class CSVUtilService {

	private static Logger logger = LoggerFactory.getLogger(CSVUtilService.class);

	private static final char DEFAULT_SEPARATOR = ',';
	private static final char DEFAULT_QUOTE = '"';

	private String csvFile;

	public String getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}

	public static void main(String[] args) throws Exception {
		CSVUtilService service = new CSVUtilService();
		XLColumnHeadingAttributes xl = new XLColumnHeadingAttributes();
		xl.setFirstName("Nitin");
		xl.setLastName("Bedi");
		xl.setCity("Pune");

		service.writeToCsv(xl, "c:\\paymentHoliday\\output.csv");

		 /*
		 * ClassLoader cl = ClassLoader.getSystemClassLoader();
		 *
		 * URL[] urls = ((URLClassLoader)cl).getURLs();
		 *
		 * cl=Thread.currentThread().getContextClassLoader();
		 *
		 * urls = ((URLClassLoader)cl).getURLs();
		 *
		 *
		 * String csvFile = "upload.csv"; CSVUtilService service = new CSVUtilService();
		 * ClassPathResource classPathResource = new ClassPathResource(csvFile);
		 * //Scanner scanner = new Scanner(new File(csvFile)); Scanner scanner = new
		 * Scanner(classPathResource.getFile()); List header=null; int counter=0; while
		 * (scanner.hasNext()) { List<String> line =
		 * service.parseLine(scanner.nextLine()); if (counter==0) { header=line; }
		 * Iterator itrLine =line.iterator(); int inCounter=0; while (itrLine.hasNext()
		 * ) { inCounter++; } counter++; } scanner.close();
		 */
	}

	public void writeToCsv(Object copytoCSV, String location) throws Exception {
		Class inputClass = copytoCSV.getClass();
		StringBuffer header = new StringBuffer();
		String data = "";
		logger.debug("Inside the code: {} Inside the fields: {}", inputClass.getFields().length,
				inputClass.getDeclaredFields().length);
		for (Field f : inputClass.getDeclaredFields()) {
			SheetColumn column = f.getAnnotation(SheetColumn.class);
			if (column != null) {
				String dataValue = "";
				header.append(column.value()).append(",");
				f.setAccessible(true);
				if (f.get(copytoCSV) != null)
					dataValue = f.get(copytoCSV).toString();
				data = data + '"' + dataValue.trim() + '"' + ",";

				f.setAccessible(false);
			}

		}
		logger.debug("Value of Header is: {}", header);
		logger.debug("Value of data is: {}", data);
		appendinCSV(header, data, location);
	}

	public void appendinCSV(StringBuffer header, String data, String filename) throws IOException {
		StringBuffer dataToAppend = new StringBuffer();
		File fr = new File(filename);
		if (fr.length() <= 0) {
			dataToAppend.append(header);
			dataToAppend.append("\n");
			dataToAppend.append(data);
			dataToAppend.append("\n");
		} else {
			dataToAppend.append(data);
			dataToAppend.append("\n");
		}
		FileWriter pw = new FileWriter(filename, true);
		try {
			pw.append(dataToAppend.toString());
			pw.flush();
		} catch(Exception e){
			logger.error("Error appending in CSV.", e);
		} finally {
			if(pw != null){
				pw.close();
			}
		}
	}

	public List readCSVinList() throws Exception {

		// csvFile = "/Users/mkyong/csv/country2.csv";
		List lines = new ArrayList();
		ClassPathResource classPathResource = new ClassPathResource(csvFile);
		// Scanner scanner = new Scanner(classPathResource.getFile());
		Scanner scanner = new Scanner(classPathResource.getInputStream());
		while (scanner.hasNext()) {
			List<String> line = parseLine(scanner.nextLine());
			lines.add(line);
		}
		scanner.close();
		return lines;

	}

	public List<String> parseLine(String cvsLine) {
		return parseLine(cvsLine, DEFAULT_SEPARATOR, DEFAULT_QUOTE);
	}

	public List<String> parseLine(String cvsLine, char separators) {
		return parseLine(cvsLine, separators, DEFAULT_QUOTE);
	}

	public List<String> parseLine(String cvsLine, char separators, char customQuote) {

		List<String> result = new ArrayList<String>();

		// if empty, return!
		if (cvsLine == null && cvsLine.isEmpty()) {
			return result;
		}

		if (customQuote == ' ') {
			customQuote = DEFAULT_QUOTE;
		}

		if (separators == ' ') {
			separators = DEFAULT_SEPARATOR;
		}

		StringBuffer curVal = new StringBuffer();
		boolean inQuotes = false;
		boolean startCollectChar = false;
		boolean doubleQuotesInColumn = false;

		char[] chars = cvsLine.toCharArray();

		for (char ch : chars) {

			if (inQuotes) {
				startCollectChar = true;
				if (ch == customQuote) {
					inQuotes = false;
					doubleQuotesInColumn = false;
				} else {

					// Fixed : allow "" in custom quote enclosed
					if (ch == '\"') {
						if (!doubleQuotesInColumn) {
							curVal.append(ch);
							doubleQuotesInColumn = true;
						}
					} else {
						curVal.append(ch);
					}

				}
			} else {
				if (ch == customQuote) {

					inQuotes = true;

					// Fixed : allow "" in empty quote enclosed
					if (chars[0] != '"' && customQuote == '\"') {
						curVal.append('"');
					}

					// double quotes in column will hit this!
					if (startCollectChar) {
						curVal.append('"');
					}

				} else if (ch == separators) {

					result.add(curVal.toString());

					curVal = new StringBuffer();
					startCollectChar = false;

				} else if (ch == '\r') {
					// ignore LF characters
					continue;
				} else if (ch == '\n') {
					// the end, break!
					break;
				} else {
					curVal.append(ch);
				}
			}

		}

		result.add(curVal.toString());

		return result;
	}

	public <T> Map<T, T> readCSVinList(Class<T> className, String location) throws Exception {
		Map<T, T> lines = new HashMap<>();
		File f = new File(location);
		Scanner scanner = new Scanner(f);
		List headerLine = null;
		boolean isHeader = true;
		Map hs = Spreadsheet.getColumnToPropertyMap(className);

		while (scanner.hasNext()) {
			List<String> line = parseLine(scanner.nextLine());
			Map dataLine = new HashMap();
			if (isHeader) {
				headerLine = line;
			} else {
				Iterator<String> itrHeaderLine = headerLine.iterator();
				Iterator<String> itrLine = line.iterator();
				while(itrHeaderLine.hasNext()) {
					dataLine.put(itrHeaderLine.next(), itrLine.next());
				}
				T xl = (T) Spreadsheet.rowAsBean(className, hs,
						dataLine);
				lines.put(xl,xl);
			}
			isHeader = false;
		}
		scanner.close();
		return lines;
	}

}
