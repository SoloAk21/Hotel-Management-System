
package com.example.hotelmanagement;

import com.example.hotelmanagement.db.DBInterface;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;


public class Callback_Service implements Callback<ListView<String>,ListCell<String>>{

    @Override
    public ListCell<String> call(ListView<String> p) {
        ListCell<String> cell = new ListCell<String>(){
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                                try { 
                                   ResultSet rs = DBInterface.getResultSet("SELECT description, pricetype FROM service WHERE id = " + t);
                                   String str = t + ": ";
                                   while (rs.next()){
                                         ResultSet rs1 = DBInterface.getResultSet("SELECT description FROM pricetype WHERE id = " + rs.getString("pricetype"));
                                         while (rs1.next()){
                                             str = str + rs.getString("description") + " - " + rs1.getString("description");
                                         }
                                    }
                                    setText(str);
                                } catch (SQLException ex) {
                                    Logger.getLogger(CustomizerController.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                    }
                };
                return cell;
    }
    
}
