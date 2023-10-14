package com.shopify.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.shopify.model.Order;
import com.shopify.model.Product;
import com.shopify.orderServiceImpl.OrderServiceImpl;
import com.shopify.service.ProductService;
import com.shopify.util.OrderStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("admin/sales")
public class SalesController {
	
	
	@Autowired
	OrderServiceImpl orderServiceImpl;
	
	@Autowired
	ProductService productService;
	

    @GetMapping("/download")
    @ResponseBody
    public void downloadSalesReport(@RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
    	    @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate ,HttpServletResponse response) throws IOException {
        // Generate sales data (replace this with your actual data retrieval logic)
    	// Add the order details
        OrderStatus cancelledStatus = OrderStatus.CANCELED;
        OrderStatus returnedStatus = OrderStatus.RETURN;
        List<Order> notcancelledNotList = orderServiceImpl.findOrdersByDateRange(startDate, endDate);
        
        

        // Create a CSV file with the sales data
        try (FileWriter writer = new FileWriter("sales_report.csv")) {
        	
        	writer.append("Order ID");
            writer.append(",");
            writer.append("Order Date");
            writer.append(",");
            writer.append("Number of Items");
            writer.append(",");
            writer.append("User ID");
            writer.append(",");
            writer.append("Payment Method");
            writer.append(",");
            writer.append("Status");
            writer.append(",");
            writer.append("Order Amount");
            writer.append(",");
            writer.append("\n");
            
            // Add data for not cancelled orders
            for (Order item : notcancelledNotList) {
                writer.append(String.valueOf(item.getId()));
                writer.append(",");
                writer.append(String.valueOf(item.getFormattedOrderDate()));
                writer.append(",");
                writer.append(String.valueOf(item.getOrderItems().size()));
                writer.append(",");
                writer.append(item.getUser().getEmail());
                writer.append(",");
                writer.append(item.getPaymentMethod());
                writer.append(",");
                writer.append(String.valueOf(item.getStatus()));
                writer.append(",");
                writer.append(String.valueOf(item.getTotalAmount()));
                writer.append(",");
                writer.append("\n");
            }

           
        } catch (IOException e) {
            // Handle file creation error
            e.printStackTrace();
        }

        // Configure the response to allow file download
        // Set appropriate headers
        // You may need to adjust the content type and headers based on your file type (e.g., Excel)
        response.setHeader("Content-Disposition", "attachment; filename=sales_report.csv");
        response.setContentType("text/csv");

        // Copy the file content to the response output stream
        try (FileInputStream fileInputStream = new FileInputStream("sales_report.csv");
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // Handle file download error
            e.printStackTrace();
        }
    }
    
    @GetMapping("/stock")
    @ResponseBody
    public void downloadStockReport(HttpServletResponse response) throws IOException {
        // Generate sales data (replace this with your actual data retrieval logic)
    	// Add the order details
    	 List<Product> productList = productService.getAllProduct();
        

        // Create a CSV file with the sales data
        try (FileWriter writer = new FileWriter("stock_report.csv")) {
        	
        	writer.append("Product Name");
            writer.append(",");
            writer.append("Stock Quantity");
            writer.append(",");
            writer.append("Unit Price");
            writer.append(",");
            writer.append("Unit Weight");
            writer.append(",");
            writer.append("Category");
            writer.append(",");
            writer.append("\n");
            
            // Add data for not cancelled orders
            for (Product item : productList) {
                writer.append(item.getName());
                writer.append(",");
                writer.append(String.valueOf(item.getStockQuantity()));
                writer.append(",");
                writer.append(String.valueOf(item.getPrice()));
                writer.append(",");
                writer.append(String.valueOf(item.getWeight()));
                writer.append(",");
                writer.append(item.getCategory().getName());
                writer.append(",");
                writer.append("\n");
            }

           
        } catch (IOException e) {
            // Handle file creation error
            e.printStackTrace();
        }

        // Configure the response to allow file download
        // Set appropriate headers
        // You may need to adjust the content type and headers based on your file type (e.g., Excel)
        response.setHeader("Content-Disposition", "attachment; filename=stock_report.csv");
        response.setContentType("text/csv");

        // Copy the file content to the response output stream
        try (FileInputStream fileInputStream = new FileInputStream("stock_report.csv");
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // Handle file download error
            e.printStackTrace();
        }
    }
    
    @GetMapping("/cancellationReport")
    @ResponseBody
    public void downloadCancellationReport(@RequestParam(name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
    	    @RequestParam(name = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date endDate ,HttpServletResponse response) throws IOException {
        // Generate sales data (replace this with your actual data retrieval logic)
    	// Add the order details
    	OrderStatus cancelledStatus = OrderStatus.CANCELED;
        List<Order> cancelledList = orderServiceImpl.findCanceledOrdersByDateRange(startDate, endDate);
        int count = cancelledList.size();
        // Create a CSV file with the sales data
        try (FileWriter writer = new FileWriter("cancellation_report.csv")) {
        	
        	writer.append("Order ID");
            writer.append(",");
            writer.append("Order Date");
            writer.append(",");
            writer.append("Number of Items");
            writer.append(",");
            writer.append("Order Amount");
            writer.append(",");
            writer.append("User ID");
            writer.append(",");
            writer.append("Payment Method");
            writer.append(",");
            writer.append("Status");
            writer.append(",");
            writer.append("Refund Status");
            writer.append(",");
            writer.append("\n");
            
            // Add data for not cancelled orders
            for (Order item : cancelledList) {
                writer.append(String.valueOf(item.getId()));
                writer.append(",");
                writer.append(String.valueOf(item.getFormattedOrderDate()));
                writer.append(",");
                writer.append(String.valueOf(count));
                writer.append(",");
                writer.append(String.valueOf(item.getTotalAmount()));
                writer.append(",");
                writer.append(item.getUser().getEmail());
                writer.append(",");
                writer.append(item.getPaymentMethod());
                writer.append(",");
                writer.append(String.valueOf(item.getStatus()));
                writer.append(",");
                writer.append("COMPLETED");
                writer.append(",");
                writer.append("\n");
            }

           
        } catch (IOException e) {
            // Handle file creation error
            e.printStackTrace();
        }

        // Configure the response to allow file download
        // Set appropriate headers
        // You may need to adjust the content type and headers based on your file type (e.g., Excel)
        response.setHeader("Content-Disposition", "attachment; filename=cancellation_report.csv");
        response.setContentType("text/csv");

        // Copy the file content to the response output stream
        try (FileInputStream fileInputStream = new FileInputStream("cancellation_report.csv");
             OutputStream outputStream = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            // Handle file download error
            e.printStackTrace();
        }
    }
    
}

