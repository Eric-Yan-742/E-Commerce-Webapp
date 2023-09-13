package com.example.ecommercewebsite;

import javax.sql.DataSource;
import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class CommodityDbUtil {
    private DataSource dataSource;
    public CommodityDbUtil(DataSource theDataSource) {
        dataSource = theDataSource;
    }

    public List<Commodity> getCommodities() throws SQLException{
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;
        List<Commodity> res = new ArrayList<>();

        try {
            // get connection
            myConn = dataSource.getConnection();
            // create sql
            String sql = "select * from items";
            myStmt = myConn.createStatement();
            // execute sql
            myRs = myStmt.executeQuery(sql);

            // create commodities list
            while(myRs.next()) {
                int id = myRs.getInt("id");
                String name = myRs.getString("name");
                int price = myRs.getInt("price");
                int inventory = myRs.getInt("inventory");

                Commodity com = new Commodity(id, name, price, inventory);
                res.add(com);
            }
            return res;
        } finally {
            close(myConn, myStmt, myRs);
        }
    }

    private void close(Connection myConn, Statement myStmt, ResultSet myRs) throws SQLException {
        if(myConn != null) {
            myConn.close();
        }
        if(myStmt != null) {
            myStmt.close();
        }
        if(myRs != null) {
            myRs.close();
        }
    }

    public void deduceInventory(int itemId) throws SQLException {
        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            myConn = dataSource.getConnection();
            String sql = "update items set inventory=inventory-1 where id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, itemId);
            myStmt.executeUpdate();
        } finally {
            close(myConn, myStmt, null);
        }
    }

    public void resetItem() throws SQLException{
        Connection myConn = null;
        Statement myStmt = null;

        try {
            myConn = dataSource.getConnection();
            String deleteSQL = "drop table if exists items";
            String createTable = "CREATE TABLE items (" +
                    "id INT AUTO_INCREMENT KEY PRIMARY KEY ," +
                    "name VARCHAR(20) NOT NULL comment '名称'," +
                    "price INT NOT NULL comment '价格'," +
                    "inventory INT NOT NULL comment '库存');";
            String insertData = "INSERT INTO items (name, price, inventory) " +
                    "values ('苹果', 5, 100), ('电视', 10000, 8), ('沙发', 200, 5), " +
                    "('电脑', 5000, 125), ('铅笔', 1, 2000)";
            myStmt = myConn.createStatement();
            myStmt.executeUpdate(deleteSQL);
            myStmt.executeUpdate(createTable);
            myStmt.executeUpdate(insertData);
        } finally {
            close(myConn, myStmt, null);
        }
    }
    // Return true if inventory >= 0, false if inventory < 0
    public boolean enoughInventory(int itemId) throws SQLException {
        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int remaining = 0;

        try {
            myConn = dataSource.getConnection();
            String sql = "select (inventory-1) remaining from items where id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, itemId);
            myRs = myStmt.executeQuery();

            while(myRs.next()) {
                remaining = myRs.getInt("remaining");
            }

            return remaining >= 0;
        } finally {
            close(myConn, myStmt, myRs);
        }
    }
}
