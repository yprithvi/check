/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monthlypnlcheck;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author prithvi
 */
public class MonthlyPNLCheck {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        MonthlyPNLCheck mCheck = new MonthlyPNLCheck();
//        read file with unmatched PNL's
        FileInputStream file = new FileInputStream(new File(
                "/home/prithvi/Desktop/MonthlyPnLVerify_AllSecs_2014.xlsm"));
        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(1);
        PrintWriter writer = new PrintWriter("/home/prithvi/Desktop/newfile.csv", "UTF-8");
        
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String connectionUrl = "jdbc:sqlserver://GLOBAL-NYSQL3;user=mikuser;password=mikuser;database=PIDM";
        Connection con = DriverManager.getConnection(connectionUrl);   
        Iterator<Row> rowIterator = sheet.iterator();
        int rowId = 0;
        while(rowIterator.hasNext()){
            Row row = rowIterator.next();
//            cell = TotalYTDPnl 
//            diff = diff b/w calculated and existed PNL's
            Cell cell = row.getCell(3);
            Cell diff = row.getCell(5);
//            ignore 1st 2 records
            if(rowId<2){
                rowId++;
                continue;
            }
            try{
                rowId++;
                double totPnl = cell.getNumericCellValue();
//                only verifying records which have difference in PNL's
                if(diff.getNumericCellValue()!=0){
//                    System.out.println(row.getRowNum()+"|"+row.getCell(1)+","+row.getCell(2)+"->"+totPnl);
                    mCheck.selector(row.getRowNum(),row.getCell(1),row.getCell(2),totPnl, row, writer, con);
                }
            }catch(NullPointerException ne){
                ne.getMessage();
            }
        }
        con.close();
        System.out.println("DB connection closed!");
        writer.close();
        file.close();
    }
    void selector(int rowNo, Cell tck, Cell section, Double totPnl, Row row,PrintWriter writer, 
            Connection con) throws ClassNotFoundException, SQLException, IOException{
        
        DBConnect dbc = new DBConnect();
        ExcelCheck ex = new ExcelCheck();
//        checking if any ticker changes
        List tcks = dbc.tkrChg(tck.getStringCellValue(), con);
        boolean isEqual=false;
        StringBuilder st = new StringBuilder();
        st.append(tck+","+section+","+totPnl+",,,");
        Double yrPnlTotal=0.0;
        Map<Integer, Double> pnlChg=null;
//       if ticker changed:
        if(tcks.size()!=0){
            pnlChg = ex.check(tcks, section.getStringCellValue());
            if(pnlChg.size()!=0){
                for (int i=12; i>=1;i--){
                    if(pnlChg.get(i)!=null){
                        yrPnlTotal = yrPnlTotal+pnlChg.get(i);
                        st=st.append(pnlChg.get(i)).append(",");
                    }
                    else{
                        yrPnlTotal = yrPnlTotal+0.0;
                        st=st.append(0.0).append(",");
                    }
                }
                
                if(yrPnlTotal.intValue() == totPnl.intValue() || yrPnlTotal.intValue() == totPnl.intValue()+1
                    || yrPnlTotal.intValue() == totPnl.intValue()-1){
                    isEqual = true;
                    System.out.println("Ticker changed -> "+yrPnlTotal);
                }
                else
                    isEqual = false;
            }
            else if (pnlChg.size()==0)
                System.out.println("Found ticker change but didn't find any open closed data!!");
        }
        if(!isEqual){
            pnlChg = dbc.pnlChg(tck.getStringCellValue(), section.getStringCellValue(), con);
            Map<Integer, Double> diff = null;
            for (int i=12; i>=1;i--){
                Double db;
                int prevMon = i-1;
                double prevPnl;
                if(pnlChg.get(i)==null){
                    st = st.append(0.0+",");
                    continue;
                }
                else{
                    
                    for(int j=prevMon;j>=0 && pnlChg.get(j)==null;j--){
                        prevMon=prevMon-1;
                    }
                    if(prevMon<=0){
                        db = pnlChg.get(i);
                        yrPnlTotal = yrPnlTotal+db;
                        st = st.append(db+",");
                    }
                    else{
                        db = pnlChg.get(i)-pnlChg.get(prevMon);
                        yrPnlTotal = yrPnlTotal+(db); 
                        st=st.append(db).append(",");
                    }
                }
            }
            if(yrPnlTotal.intValue() == totPnl.intValue() || yrPnlTotal.intValue() == totPnl.intValue()+1
                    || yrPnlTotal.intValue() == totPnl.intValue()-1){
                System.out.println("PNL changed -> "+yrPnlTotal);
                isEqual = true;
            }
            else
                isEqual = false;
        }
        if(!isEqual)
            System.out.println("found no tcks change or PNL change for : \n"+st);
            writer.println(st);
    }
}
