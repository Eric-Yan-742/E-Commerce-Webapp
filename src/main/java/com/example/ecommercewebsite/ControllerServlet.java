package com.example.ecommercewebsite;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ControllerServlet", value = "/ControllerServlet")
public class ControllerServlet extends HttpServlet {
    private CommodityDbUtil commodityDbUtil;
    private CustomerDbUtil customerDbUtil;
    @Resource(name="jdbc/shopping_website")
    private DataSource dataSource;
    @Override
    public void init() throws ServletException {
        commodityDbUtil = new CommodityDbUtil(dataSource);
        customerDbUtil = new CustomerDbUtil(dataSource);
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // read the "command" parameter
            String theCommand = request.getParameter("command");
            if(theCommand == null) {
                theCommand = "LIST";
            }
            // route to the appropriate method
            switch(theCommand) {
                case "LIST":
                    listAllCommodities(request, response);
                    break;
                case "SEARCH":
                    searchItem(request, response);
                    break;
                case "BUY":
                    buyItem(request, response);
                    break;
                case "RESET":
                    resetContent(request, response);
                    break;
                default:
                    listAllCommodities(request, response);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            throw new ServletException(ex);
        }
    }

    private void resetContent(HttpServletRequest request, HttpServletResponse response)
        throws Exception{
        commodityDbUtil.resetItem();
        customerDbUtil.resetCustomer(1);
        listAllCommodities(request, response);
    }

    private void buyItem(HttpServletRequest request, HttpServletResponse response)
        throws Exception{
        // read id
        int itemId = Integer.parseInt(request.getParameter("itemID"));

        // check balance and inventory
        if(!customerDbUtil.enoughBalance(1, itemId)) {
            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/balance_error.jsp");
            dispatcher.forward(request, response);
        } else if(!commodityDbUtil.enoughInventory(itemId)) {
            RequestDispatcher dispatcher =
                    request.getRequestDispatcher("/inventory_error.jsp");
            dispatcher.forward(request, response);
        } else {
            // deduce balance
            customerDbUtil.deduceBalance(1, itemId);

            // deduce inventory
            commodityDbUtil.deduceInventory(itemId);

            listAllCommodities(request, response);
        }
    }

    private void searchItem(HttpServletRequest request, HttpServletResponse response) throws Exception{
        // read text
        String text = request.getParameter("search_field");

        // get all commodities
        List<Commodity> commodityList = commodityDbUtil.getCommodities();
        List<Commodity> resultList = new ArrayList<>();

        // loop through all commodities, find the ones that contain the term
        for(int id = 1; id <= commodityList.size(); id++) {
            String name = commodityList.get(id - 1).getName();
            if(name.contains(text)) {
                resultList.add(commodityList.get(id - 1));
            }
        }

        // display result
        displayList(resultList, request, response);

    }

    private void listAllCommodities(HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        // get commodities from db
        List<Commodity> commodityList = commodityDbUtil.getCommodities();
        displayList(commodityList, request, response);
    }

    private void displayList(List<Commodity> commodityList, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        // add commodities to the request
        request.setAttribute("COMMODITY_LIST", commodityList);

        // get customer from db
        Customer cu = customerDbUtil.getCustomer(1);

        // add customer to the request
        request.setAttribute("CUSTOMER", cu);

        // send to JSP page
        RequestDispatcher dispatcher =
                request.getRequestDispatcher("/list_commodity.jsp");
        dispatcher.forward(request, response);
    }
}