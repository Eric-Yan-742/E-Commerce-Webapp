package com.example.ecommercewebsite;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "TestJdbcConnection", value = "/TestJdbcConnection")
public class TestJdbcConnection extends HttpServlet {
    @Resource(name="jdbc/shopping_website")
    private DataSource dataSource;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. Set up the printwriter
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        // 2. get a connection to the database
        Connection myConn = null;
        Statement myStmt = null;
        ResultSet myRs = null;
        try {
            myConn = dataSource.getConnection();
            // 3. Create a SQL statements
            String sql = "select * from items";
            myStmt = myConn.createStatement();
            // 4. Execute SQL query
            myRs = myStmt.executeQuery(sql);
            // 5. Process the result set
            while(myRs.next()) {
                String name = myRs.getString("name");
                out.println(name);
            }
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}