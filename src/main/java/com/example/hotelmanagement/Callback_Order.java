
package com.example.hotelmanagement;

import com.example.hotelmanagement.db.DBInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;


public class Callback_Order implements Callback<ListView<String>,ListCell<String>>{

    @Override
    public ListCell<String> call(ListView<String> p) {
        ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            if (t.equals("New Order")){
                               setText(t);
                            } else {
                                try { 
                                   ResultSet rs = DBInterface.getResultSet("SELECT room, fromDate, toDate FROM order_room WHERE id = " + t);
                                   String str = t + ": Room №";
                                   while (rs.next()){
                                         str = str + rs.getString("room") + " From :" + rs.getString("fromDate") + " To :" + rs.getString("toDate");
                                   }
                                   setText(str);
                                } catch (SQLException ex) {
                                    Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    }
                };
                return cell;
    }
    
}