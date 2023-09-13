package com.example.ecommercewebsite;

import javax.sql.DataSource;
import java.sql.*;

public class CustomerDbUtil {
    private DataSource dataSource;

    public CustomerDbUtil(DataSource theDataSource) {
        dataSource = theDataSource;
    }

    public Customer getCustomer(int cusId) throws Exception{
        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        Customer cu = null;

        try {
            myConn = dataSource.getConnection();
            String sql = "select * from customer where id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, cusId);
            myRs = myStmt.executeQuery();

            while(myRs.next()) {
                int id = myRs.getInt("id");
                String name = myRs.getString("name");
                int balance = myRs.getInt("balance");
                cu = new Customer(id, name, balance);
            }

            return cu;
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

    public void deduceBalance(int cusId, int itemId) throws SQLException{
        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            myConn = dataSource.getConnection();
            String sql = "update customer set balance = balance - (select price from items where id=?) where id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, itemId);
            myStmt.setInt(2, cusId);
            myStmt.executeUpdate();
        } finally {
            close(myConn, myStmt, null);
        }
    }

    public void resetCustomer(int id) throws SQLException{
        Connection myConn = null;
        PreparedStatement myStmt = null;

        try {
            myConn = dataSource.getConnection();
            String update = "update customer set balance=20000 where id=?";
            myStmt = myConn.prepareStatement(update);
            myStmt.setInt(1, id);
            myStmt.executeUpdate();
        } finally {
            close(myConn, myStmt, null);
        }
    }
    // If user's balance is non-negative, return true. If it's negative, return false.
    public boolean enoughBalance(int cusId, int itemId) throws SQLException {
        Connection myConn = null;
        PreparedStatement myStmt = null;
        ResultSet myRs = null;
        int difference = 0;

        try {
            myConn = dataSource.getConnection();
            String sql = "select balance - (select price from items where id=?) difference " +
                    "from customer where id=?";
            myStmt = myConn.prepareStatement(sql);
            myStmt.setInt(1, itemId);
            myStmt.setInt(2, cusId);
            myRs = myStmt.executeQuery();

            while(myRs.next()) {
                difference = myRs.getInt("difference");
            }

            return difference >= 0;
        } finally {
             close(myConn, myStmt, myRs);
        }
    }
}
