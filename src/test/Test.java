/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author prithvi
 */
public class Test {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        FileInputStream file = new FileInputStream(new File(
                "/home/prithvi/Desktop/MonthlyPnLVerify_AllSecs_2014.xlsm"));
        PrintWriter writer = new PrintWriter("/home/prithvi/Desktop/newfile.csv", "UTF-8");
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(1);
        
        Iterator<Row> rowIterator = sheet.iterator();
        int rowId = 0;
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
            Cell cell1 = row.getCell(1);
            Cell cell2 = row.getCell(2);
            Cell cell3 = row.getCell(3);
            if(cell1!=null ||cell2!=null ||cell3!=null)
                writer.println(cell1+","+cell2+","+","+cell3);
            
            rowId++;
        }
        file.close();
        writer.close();
//                FileOutputStream fos =new FileOutputStream(new File(
//                        "/home/prithvi/Desktop/MonthlyPnLVerify_AllSecs_2014.xlsm"));
//                workbook.write(fos);
//                fos.close();
    }
    static void write(){
        
    }
    
}
