/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monthlypnlcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author prithvi
 */
public class ExcelCheck {
//    method to check all old tickers and calculate for each month
    Map<Integer, Double> check(List oldTcks, String section) throws FileNotFoundException, IOException{
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        FileInputStream file = new FileInputStream(new File(
                "/home/prithvi/Desktop/MonthlyPnLVerify_AllSecs_2014.xlsm"));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(1);
        Iterator<Row> rowIterator = sheet.iterator();
        int rowId = 0;
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            if(rowId<2){
                rowId++;
                continue;
            }
            Cell ggid = row.getCell(26);
            Cell sec = row.getCell(27);
            Double pnl = row.getCell(28).getNumericCellValue();
            Double month = row.getCell(31).getNumericCellValue();
//            add values to the 
            if(oldTcks.contains(ggid.getStringCellValue()) && section.equals(sec.getStringCellValue())){
                if(map.containsKey(month.intValue())){
                    double tempVal = map.get(month.intValue());
                    tempVal = tempVal+pnl;
                    map.put(month.intValue(), tempVal);
                }
                else if(pnl==null)
                    map.put(month.intValue(),0.0);
                else
                    map.put(month.intValue(),pnl);
            }
        }
        return map;
    }
}
