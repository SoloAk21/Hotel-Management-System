
package com.example.hotelmanagement.db;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.LinkedList;


public class HelpFunctions {
    

    public static boolean isDouble(String str){
        boolean check = true;
        try{
            Double.parseDouble(str);
        }catch(NumberFormatException ex){
            check = false;
        }
        return check;
    }
    

    public static boolean isDateCorrect(Date start1, Date end1) {
        return start1.before(end1);
    }
    

    public static boolean checkDateRangeIntersection(Date start1, Date end1, Date start2, Date end2) {
        //(StartA <= EndB) and (EndA >= StartB)        
        return start1.getTime()<=end2.getTime() && end1.getTime()>=start2.getTime();
    }
    

    public static LinkedList<Calendar> getMonthFromRange(Date start, Date end) {                
        LinkedList<Calendar> l = new LinkedList<>();
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        while(c1.get(Calendar.MONTH) <= c2.get(Calendar.MONTH)){           
            l.add((Calendar) c1.clone());
            c1.add(Calendar.MONTH, 1);
        }
        return l;
    }
    
     /**
      * liefert Auslastung in einem Monaten zurück
      *
      * @param c
      * @return boolean
      * @throws SQLException
      */
    public static BigDecimal getUsageInMonth(Calendar c) throws SQLException {
       int days = 0;
       int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
       for(int i = 0; i < max; i++){                    
           ResultSet rs = DBInterface.getResultSet("SELECT fromDate, toDate\n" +
                                                   "FROM order_room\n" +
                                                   "WHERE fromDate <= DATE('" + new Date(c.getTimeInMillis()) +
                                                   "') and toDate >= DATE('" + new Date(c.getTimeInMillis()) +"')");
           while(rs.next()){               
               days++;
           }
           c.add(Calendar.DAY_OF_MONTH, 1);
       }
       
       int rooms = 0;
       ResultSet rs = DBInterface.getResultSet("SELECT count(*) as rooms\n" +
                                               "FROM room\n");
        if(rs.next()){ 
            rooms = rs.getInt("rooms");
       }


        BigDecimal daysBigDecimal = new BigDecimal(days);
        BigDecimal roomsBigDecimal = new BigDecimal(rooms);
        BigDecimal maxBigDecimal = new BigDecimal(max);

        BigDecimal bd = daysBigDecimal.multiply(new BigDecimal(100))
                .divide(roomsBigDecimal.multiply(maxBigDecimal), 2, BigDecimal.ROUND_HALF_EVEN);




        return bd;
    }
    

    public static int getUsageOfRoomInMonth(Calendar c, String room) throws SQLException {
       int days = 0;
       c.set(Calendar.DAY_OF_MONTH, 1);
       int max = c.getActualMaximum(Calendar.DAY_OF_MONTH);
       for(int i = 0; i < max; i++){   
           ResultSet rs = DBInterface.getResultSet("SELECT EXISTS(SELECT * FROM order_room "
                                                   + "WHERE room = " + room +
                                                   " and fromDate <= DATE('" + new Date(c.getTimeInMillis()) +
                                                   "') and toDate >= DATE('" + new Date(c.getTimeInMillis()) +"') LIMIT 1) as result");
           while(rs.next()){
               if (rs.getInt("result") == 1) days++;
           }
           c.add(Calendar.DAY_OF_MONTH, 1);
       }
       return days;
    }
    

    public static int getDaysInYear(int year) {
        Calendar cal = Calendar.getInstance();
        cal.clear();                                
        cal.set(Calendar.YEAR, year);  
        return cal.getActualMaximum(Calendar.DAY_OF_YEAR);
    }
    

    public static double getUsageInYear(int year) throws SQLException {
        int days = 0;
        ResultSet rs = DBInterface.getResultSet("SELECT fromDate, toDate\n" +
                                                "FROM order_room\n" +
                                                "WHERE YEAR(fromDate) =" + year + " or YEAR(toDate) = " + year);
        while(rs.next()){             
           Calendar c1 = Calendar.getInstance();
           c1.setTime(rs.getDate("fromDate"));
           Calendar c2 = Calendar.getInstance();
           c2.setTime(rs.getDate("toDate"));         
           if (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)){
               Calendar clone = (Calendar) c1.clone();
               while (clone.before(c2)) {                   
                  clone.add(Calendar.DAY_OF_MONTH, 1);  
                  days++;  
               }         
           }else if (c1.get(Calendar.YEAR) == year) {
               Calendar clone = (Calendar) c1.clone();
               Calendar nextYear = Calendar.getInstance();
               nextYear.set((year + 1), 0, 1);
               while (clone.before(nextYear)) {  
                  clone.add(Calendar.DAY_OF_MONTH, 1);  
                  days++;  
               } 
           }else{
               Calendar thisYear = Calendar.getInstance();
               thisYear.set(year, 0, 1);
               while (thisYear.before(c2)) {  
                  thisYear.add(Calendar.DAY_OF_MONTH, 1);  
                  days++;  
               }
           }
        }
        
        int rooms = 0;
        rs = DBInterface.getResultSet("SELECT count(*) as rooms\n" +
                                      "FROM room\n");
        if(rs.next()){ 
            rooms = rs.getInt("rooms");
        }
        
        return (days * 100 / (rooms * getDaysInYear(year)));
    } 
}
