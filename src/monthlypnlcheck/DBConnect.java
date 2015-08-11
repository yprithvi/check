/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monthlypnlcheck;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author prithvi
 */
public class DBConnect {
    
    List tkrChg(String newTik, Connection con) throws ClassNotFoundException, SQLException{
        List oldTik = new ArrayList();
        Statement statement = con.createStatement();
        String queryString = "select * from VV_GGID_Chg";
        ResultSet rs = statement.executeQuery(queryString);
        while (rs.next()) {
            if (rs.getString(2).equals(newTik)){
                oldTik.add(rs.getString(1));
            }
        }
        return oldTik;
    }
    Map<Integer, Double> pnlChg(String ggid, String section, Connection con) throws ClassNotFoundException, SQLException{
        Statement statement = con.createStatement();
        String queryString = "select * from (\n" +
        "select asofdate, GGID, TotalYTDPnL from openposition where GGID = '"+ggid+"' and "
                + "Section = '"+section+"'\n" +
        "and asofdate in ('12/31/2014','11/28/2014','10/31/2014','09/30/2014','08/29/2014','07/31/2014','05/30/2014','04/30/2014','03/31/2014','02/28/2014','01/31/2014')  \n" +
        "union all\n" +
        "select asofdate, GGID, TotalYTDPnL from closedposition where GGID = '"+ggid+"' and "
                + "Section = '"+section+"'\n" +
        "and asofdate in ('12/31/2014','11/28/2014','10/31/2014','09/30/2014','08/29/2014','07/31/2014','05/30/2014','04/30/2014','03/31/2014','02/28/2014','01/31/2014') \n" +
        ") X order by asofdate desc ";
        ResultSet rs = statement.executeQuery(queryString);
        
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        while (rs.next()) {
            double val = rs.getDouble(3);
            int month = rs.getDate(1).getMonth()+1;
            if(map.containsKey(month)){
                double tempVal = map.get(month);
                tempVal = tempVal+val;
                map.put(month, tempVal);
            }
            else
                map.put(month,val);
        }
        return map;
    }
}
