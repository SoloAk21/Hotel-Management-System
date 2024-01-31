package com.example.hotelmanagement.db;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBInterface {

    public static boolean isTableExist(Connection con, String tableName, ArrayList<String> log){
        try {            
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next()){ 
                log.add(tableName + " exists.");
                return true;
            }
            log.add(tableName + " doesn't exist.");
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    

    public static void createTable(Connection con, String tableName, String body, ArrayList<String> log){
        try {
            Statement stmt = con.createStatement();        
            stmt.executeUpdate("CREATE TABLE " + tableName + "(" + body + ")");
            log.add(tableName + " is created.");
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    public static void dropTable(Connection con, String tableName, ArrayList<String> log){
        try {
            Statement stmt = con.createStatement();       
            if (stmt.executeUpdate("DROP TABLE IF EXISTS " + tableName)!=0)
                log.add(tableName + " is deleted.");
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

     public static int getNextId(String tableName){
        try {
            Connection con = DBConnector.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT (r.Id + 1) AS firstId\n" +
                              "FROM " + tableName + " r\n" +
                              "LEFT JOIN  " + tableName + " r1 ON r1.Id = r.Id + 1\n" +
                              "WHERE r1.Id IS NULL\n" +
                              "ORDER BY r.Id\n" +
                              "LIMIT 0,1");
            if (rs.next()){ return rs.getInt("firstId");}
            else { return 0; }
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
     

     public static ResultSet getResultSet(String str){
        try {
            Connection con = DBConnector.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(str);           
            return rs;            
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);  
            return null; 
        }
    } 
     

    public static void executeStatement(String str){
        try {
             Connection con = DBConnector.getInstance().getConnection();      
             Statement st = con.createStatement();
             st.executeUpdate(str);
        } catch (SQLException ex) {
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);
        } 
    } 
    
     /**
     * erzeugt die Scheme mit allen notwendigen Tabellen.
     * @param con Verbindung
     * @param log LogBuch
    */
    public static void createHotelTables(Connection con, ArrayList<String> log){        
        try{
        
        if (!isTableExist(con, "room", log)) createTable(con, "room", 
                "id                   MEDIUMINT       NOT NULL,\n" +
                "description          VARCHAR(40)     NOT NULL,\n" +
                "price                DECIMAL(6,2)    NOT NULL,\n" +
                "CONSTRAINT           PK_ID           PRIMARY KEY (id)", log);
        
        if (!isTableExist(con, "gender", log)){ createTable(con, "gender", 
                "gender               ENUM('m','w')   NOT NULL,\n" +
                "title                VARCHAR(4)      NOT NULL,\n" +
                "mailform             VARCHAR(25)     NOT NULL,\n" +
                "directtext           VARCHAR(40)     NOT NULL,\n" +
                "CONSTRAINT           PK_gender       PRIMARY KEY (gender)", log);
        
                Statement stmt = con.createStatement();
                stmt.executeUpdate("     INSERT\n" +
                                    "    INTO        gender (gender, title, mailform, directtext)\n" +
                                    "    VALUES      ('m','Herr', 'Sehr geehrter','Sehr geehrter Herr'),\n" +
                                    "                ('w','Frau', 'Sehr geehrte','Sehr geehrte Frau');");
        }
        
        if (!isTableExist(con, "state", log)){ createTable(con, "state",
                "id                   MEDIUMINT       NOT NULL AUTO_INCREMENT,\n" +
                "abbreviation         VARCHAR(2)      NOT NULL,\n" +
                "fullname             VARCHAR(40)     NOT NULL,\n" +
                "CONSTRAINT           PK_ID           PRIMARY KEY (id)", log);

                Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO state (abbreviation, fullname)\n" +
                    "VALUES ('AA', 'Addis Ababa'),\n" +
                    "       ('AF', 'Afar'),\n" +
                    "       ('AM', 'Amhara'),\n" +
                    "       ('BE', 'Benishangul-Gumuz'),\n" +
                    "       ('DD', 'Dire Dawa'),\n" +
                    "       ('GA', 'Gambela'),\n" +
                    "       ('HA', 'Harari'),\n" +
                    "       ('OR', 'Oromia'),\n" +
                    "       ('SN', 'Southern Nations, Nationalities, and Peoples'' Region'),\n" +
                    "       ('SM', 'Somali'),\n" +
                    "       ('TI', 'Tigray');");

        }
        
        if (!isTableExist(con, "nationality", log)){ createTable(con, "nationality",
                                    "    id                      MEDIUMINT       NOT NULL AUTO_INCREMENT,\n" +
                                    "    description             VARCHAR(25)     NOT NULL,\n" +
                                    "    CONSTRAINT              PK_ID           PRIMARY KEY (id)", log);
        
                Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO nationality (description)\n" +
                    "VALUES ('Ethiopia'),\n" +
                    "       ('United States'),\n" +
                    "       ('China'),\n" +
                    "       ('India'),\n" +
                    "       ('Kenya'),\n" +
                    "       ('United Kingdom'),\n" +
                    "       ('Germany'),\n" +
                    "       ('France'),\n" +
                    "       ('Italy'),\n" +
                    "       ('Canada');");

        }
        
        if (!isTableExist(con, "address", log)) createTable(con, "address",
                    "id                      MEDIUMINT       NOT NULL,\n" +
                    "street                  VARCHAR(40)     NOT NULL,\n" +
                    "zip                     VARCHAR(5)      NOT NULL,\n" +
                    "city                    VARCHAR(25)     NOT NULL,\n" +
                    "state                   MEDIUMINT,\n" +
                    "CONSTRAINT              PK_ID           PRIMARY KEY (id),\n" +
                    "CONSTRAINT              FK_ADDRESS_STATE\n" +
                    "                        FOREIGN KEY(state)\n" +
                    "                        REFERENCES state(id) ON DELETE CASCADE\n" +
                    "                                             ON UPDATE CASCADE", log);
        
        if (!isTableExist(con, "person", log)) createTable(con, "person", 
                "    id                      MEDIUMINT       NOT NULL,\n" +
                "    name                    VARCHAR(25)     NOT NULL,\n" +
                "    surname                 VARCHAR(25)     NOT NULL,\n" +
                "    birthdate               DATE,\n" +
                "    nationality             MEDIUMINT,\n" +
                "    gender                  ENUM('m','w'),\n" +
                "    address                 MEDIUMINT,\n" +
                "    CONSTRAINT              PK_ID           PRIMARY KEY (id),\n" +
                "    CONSTRAINT              FK_PERSON_NATIONALITAET\n" +
                "                                            FOREIGN KEY(nationality)\n" +
                "                                            REFERENCES nationality(id) ON DELETE CASCADE\n" +
                "                                                                       ON UPDATE CASCADE,\n" +
                "    CONSTRAINT              FK_PERSON_GESCHLECHT\n" +
                "                                            FOREIGN KEY(gender)\n" +
                "                                            REFERENCES gender(gender) ON DELETE CASCADE\n" +
                "                                                                      ON UPDATE CASCADE,\n" +
                "    CONSTRAINT              FK_PERSON_ADRESSE\n" +
                "                                            FOREIGN KEY(address)\n" +
                "                                            REFERENCES address(id) ON DELETE CASCADE\n" +
                "                                                                   ON UPDATE CASCADE", log);
        
        if (!isTableExist(con, "guest", log)) createTable(con, "guest",
                "    id                      MEDIUMINT       NOT NULL,\n" +
                "    address                 MEDIUMINT,      \n" +
                "    CONSTRAINT              PK_ID           PRIMARY KEY (id),\n" +
                "    CONSTRAINT              FK_GUEST_ADDRESS\n" +
                "                                            FOREIGN KEY(address)\n" +
                "                                            REFERENCES  address(id)  ON DELETE CASCADE\n" +
                "                                                                     ON UPDATE CASCADE", log);
        
        if (!isTableExist(con, "person_is_guest", log)) createTable(con, "person_is_guest",
                "    person                  MEDIUMINT,\n" +
                "    guest                   MEDIUMINT,\n" +
                "    CONSTRAINT              PK_ID           PRIMARY KEY (person,guest),\n" +
                "    CONSTRAINT              FK_PERSONISGUEST_PERSON\n" +
                "                                            FOREIGN KEY(person)\n" +
                "                                            REFERENCES person(id) ON DELETE CASCADE\n" +
                "                                                                  ON UPDATE CASCADE,\n" +
                "    CONSTRAINT              FK_PERSONISGUEST_GUEST\n" +
                "                                            FOREIGN KEY(guest)\n" +
                "                                            REFERENCES guest(id) ON DELETE CASCADE\n" +
                "                                                                 ON UPDATE CASCADE", log);
        
        if (!isTableExist(con, "order_room", log)) createTable(con, "order_room",
                "    id                      MEDIUMINT       NOT NULL, \n" +
                "    fromDate                    DATE,\n" +
                "    toDate                      DATE,\n" +
                "    guest                   MEDIUMINT,\n" +
                "    room                    MEDIUMINT,\n" +
                "    CONSTRAINT              PK_ID           PRIMARY KEY (id),\n" +
                "    CONSTRAINT              FK_ORDER_GUEST\n" +
                "                                            FOREIGN KEY(guest)\n" +
                "                                            REFERENCES guest(id) ON DELETE CASCADE\n" +
                "                                                                 ON UPDATE CASCADE,\n" +
                "    CONSTRAINT              FK_ORDER_ROOM\n" +
                "                                            FOREIGN KEY(room)\n" +
                "                                            REFERENCES room(id) ON DELETE CASCADE\n" +
                "                                                                ON UPDATE CASCADE", log);
        
        if (!isTableExist(con, "pricetype", log)){ createTable(con, "pricetype",
                "    id                      MEDIUMINT       NOT NULL AUTO_INCREMENT,\n" +
                "    description             VARCHAR(40)     NOT NULL ,\n" +
                "    PRIMARY KEY (id)", log);

                Statement stmt = con.createStatement();
            stmt.executeUpdate("INSERT INTO pricetype (description)\n" +
                    "VALUES ('Room Rate'),\n" +
                    "       ('Full Board'),\n" +
                    "       ('Half Board'),\n" +
                    "       ('All-Inclusive');");

        }

        if (!isTableExist(con, "service", log)) createTable(con, "service",
                "    id                      MEDIUMINT       NULL,\n" +
                "    description             VARCHAR(40)     NOT NULL,\n" +
                "    price                   DECIMAL(6,2)    NOT NULL,\n" +
                "    pricetype               MEDIUMINT,\n" +
                "    CONSTRAINT              PK_ID           PRIMARY KEY (id),\n" +
                "    CONSTRAINT              FK_SERVICE_PRICETYPE\n" +
                "                                            FOREIGN KEY(pricetype)\n" +
                "                                            REFERENCES pricetype(id)  ON DELETE CASCADE\n" +
                "                                                                      ON UPDATE CASCADE", log);
        
        if (!isTableExist(con, "order_service", log)) createTable(con, "order_service",
                "    count                   SMALLINT        NOT NULL,\n" +
                "    order_room                   MEDIUMINT,\n" +
                "    service                 MEDIUMINT,\n" +
                "    CONSTRAINT              PK_ID           PRIMARY KEY (order_room,service),\n" +
                "    CONSTRAINT              FK_ORDERSERVICE_ORDER\n" +
                "                                            FOREIGN KEY(order_room)\n" +
                "                                            REFERENCES order_room(id) ON DELETE CASCADE\n" +
                "                                                                      ON UPDATE CASCADE,\n" +
                "    CONSTRAINT              FK_ORDERSERVICE_SERVICE\n" +
                "                                            FOREIGN KEY(service)\n" +
                "                                            REFERENCES service(id)  ON DELETE CASCADE\n" +
                "                                                                    ON UPDATE CASCADE", log);
        }catch(SQLException ex){
            Logger.getLogger(DBInterface.class.getName()).log(Level.SEVERE, null, ex);            
        } finally {
       //     for (String str : log)
       //         System.out.println(str);
        }
    }
    
    /**
     * liefert Statistik "Age of guests per year"
     * @return ResultSet
    */
    public static ResultSet getFirstStatistic(){
        return getResultSet("select Year(fromDate) as Year,\n" +
                            "MAX(IF( RIGHT(fromDate, 5)<RIGHT(birthdate, 5), YEAR(fromDate)-YEAR(birthdate)-1,YEAR(fromDate)-YEAR(birthdate))) as max,\n" +
                            "MIN(IF( RIGHT(fromDate, 5) < RIGHT(birthdate, 5), YEAR(fromDate)-YEAR(birthdate)-1,YEAR(fromDate) - YEAR(birthdate))) as min, ROUND(AVG(IF( RIGHT(fromDate, 5) < RIGHT(birthdate, 5), YEAR(fromDate)-YEAR(birthdate)-1,YEAR(fromDate) - YEAR(birthdate))),1) as avg\n" +
                            "from person, guest, person_is_guest, order_room\n" +
                            "where person.id = person_is_guest.person and guest.id = person_is_guest.guest and order_room.guest = guest.id " +
                            "group by Year(fromDate)");
    }
    
    /**
     * liefert Statistik "Number of guests each province and abroad"
     * @return ResultSet
    */
    public static ResultSet getSecondStatistic(){
       return getResultSet("select YEAR(fromDate) as year, fullname as state, count(*) as together\n" +
                            "from state join address on address.state = state.id\n" +
                            "		join guest on guest.address = address.id	\n" +
                            "		join order_room on order_room.guest = guest.id\n" +
                            "group by YEAR(fromDate), fullname ");
    }
    
      /**
     * liefert Statistik "Number of guests per year and days of bookings"
     * @return ResultSet
    */
    public static ResultSet getSixthStatistic(){
       return getResultSet("SELECT YEAR(fromDate) as year, MIN(DATEDIFF(toDate,fromDate)) as min, MAX(DATEDIFF(toDate,fromDate)) as max, ROUND(AVG(DATEDIFF(toDate,fromDate)),1) as avg, count(*) as guest \n" +
                           "FROM order_room join guest on (order_room.guest = guest.id) \n" +
                           "GROUP BY YEAR(fromDate)");
    }
    
      /**
     * liefert Statistik "Overview of all guests"
     * @return ResultSet
    */
    public static ResultSet getSeventhStatistic(){
       return getResultSet( "SELECT guest.id, GROUP_CONCAT(\" \", surname, \" \", name) as pers, fromDate, toDate, DATEDIFF(IF(YEAR(fromDate)<>YEAR(toDate),CURRENT_DATE,toDate),fromDate) as days \n" +
                            "FROM order_room join guest on order_room.guest = guest.id\n" +
                            "join person_is_guest on guest.id = person_is_guest.guest \n" +
                            "join person on person.id = person_is_guest.person\n" +
                            "group by guest.id");
    }
}
