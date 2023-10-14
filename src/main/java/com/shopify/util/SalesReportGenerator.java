package com.shopify.util;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopify.model.Order;
import com.shopify.model.OrderItem;
import com.shopify.orderServiceImpl.OrderServiceImpl;

@Component
public class SalesReportGenerator {
	
	@Autowired
	OrderServiceImpl orderServiceImpl;
	
	 public void generateSalesReport(OutputStream outputStream,Date startDate, Date endDate) throws IOException {
	        try (PDDocument document = new PDDocument()) {
	            PDPage page = new PDPage(PDRectangle.A2);
	            document.addPage(page);

	            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	            	
	            	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20); // Larger font size for the title

	                // Set the initial y-coordinate for the title
	                float titleY = page.getMediaBox().getHeight() - 30;

	                // Add the title
	                contentStream.beginText();
	                contentStream.newLineAtOffset(480, titleY);
	                contentStream.showText("Sales Report"); // Add the title here
	                contentStream.endText();
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

	                // Set the initial y-coordinate for the content
	                float y = page.getMediaBox().getHeight() - 50;

	                // Add the order details
	                OrderStatus cancelledStatus = OrderStatus.CANCELED;
	                OrderStatus returnedStatus = OrderStatus.RETURN;
	                List<Order> notcancelledNotList = orderServiceImpl.findOrdersByDateRange(startDate, endDate);
	                // Add a table for displaying order items
	                float tableY = y - 50; // Adjust the starting y-coordinate for the table
	                float margin = 50;
	                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
	                float yPosition = tableY;
	                int rows = notcancelledNotList.size() + 1; // Include header row
	                float rowHeight = 20f;
	                float tableHeight = rowHeight * rows;
	                float colWidth = tableWidth / (float) 7; // 8 columns in the table

	                // Draw table headers
	                float tableXPosition = margin;
	                float textXPosition = tableXPosition + 4;
	                float textYPosition = yPosition - 15;
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.beginText();
	                contentStream.newLineAtOffset(textXPosition, textYPosition);
	                contentStream.showText("Order ID");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Order Date");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Number of Items");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("User ID");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Payment Method");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Status");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Order Amount");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.endText();
	                yPosition -= rowHeight;

	                // Draw table content
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                for (Order item : notcancelledNotList) {
	                	contentStream.setLineWidth(0.5f); // Set the border line width
             	    contentStream.moveTo(tableXPosition, yPosition);
             	    contentStream.lineTo(tableXPosition + tableWidth, yPosition);
             	    contentStream.stroke();
             	    
             	   List<OrderItem> itemList = item.getOrderItems(); // Get the list of items

             	// Create a StringBuilder to concatenate the item names in the same column
             	StringBuilder itemText = new StringBuilder();
             	
             	int count = itemList.size();

             	// Iterate through the list of items and concatenate them
             	//for (OrderItem items : itemList) {
             	    //itemText.append(items.getProduct().getName());
             	    
             	   //itemText.append("\\u2028");// Add the item name to the StringBuilder
             	    // Add a newline character to separate items
             		
             		
             	//}
             	PDType1Font font = PDType1Font.COURIER;
             	
	                    contentStream.beginText();
	                    contentStream.newLineAtOffset(textXPosition, yPosition - rowHeight);
	                    contentStream.showText(String.valueOf(item.getId()));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(String.valueOf(item.getFormattedOrderDate()));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(String.valueOf(count));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(item.getUser().getEmail());
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(item.getPaymentMethod());
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(String.valueOf(item.getStatus()));
	                    contentStream.newLineAtOffset(colWidth, 0);	         
	                    contentStream.showText(String.valueOf(item.getTotalAmount()));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.endText();
	                    yPosition -= rowHeight;
	                }

	                // Draw table borders
	                contentStream.setLineWidth(1f);
	                contentStream.moveTo(tableXPosition, tableY);
	                contentStream.lineTo(tableXPosition, tableY - tableHeight);
	                contentStream.lineTo(tableXPosition + tableWidth, tableY - tableHeight);
	                contentStream.lineTo(tableXPosition + tableWidth, tableY);
	                contentStream.closePath();
	                contentStream.stroke();
	                
	            
	                
	             // Adjust the y-coordinate to start after the table-2
	                yPosition = tableY - tableHeight - 40;
	                
	                
	                float table2Y = yPosition; // Adjust the starting y-coordinate for the table
	                float margin2 = 50;
	                float tableWidth2 = page.getMediaBox().getWidth() - 2 * margin;
	                float yPosition2 = table2Y;
	                int rows2 = notcancelledNotList.size() + 1; // Include header row
	                float rowHeight2 = 20f;
	                float tableHeight2 = rowHeight2 *2;
	                float colWidth2 = tableWidth / (float) 3; // 8 columns in the table

	                // Draw table headers
	                float tableXPosition2 = margin2;
	                float textXPosition2 = tableXPosition + 4;
	                float textYPosition2 = yPosition2 - 15;
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.beginText();
	                contentStream.newLineAtOffset(tableXPosition2, textYPosition2);
	                contentStream.showText("Total Number of Sales");
	                contentStream.newLineAtOffset(colWidth2, 0);
	                contentStream.showText("Gross Sales Amount");
	                contentStream.newLineAtOffset(colWidth2, 0);
	                contentStream.showText("Net Sales Amount");
	                contentStream.newLineAtOffset(colWidth2, 0);
	                contentStream.endText();
	                yPosition2 -= rowHeight2;
	                
	                //Table content
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                
	                contentStream.setLineWidth(0.5f); // Set the border line width
             	    contentStream.moveTo(tableXPosition2, yPosition2);
             	    contentStream.lineTo(tableXPosition2 + tableWidth2, yPosition2);
             	    contentStream.stroke();
             	    
	                int totalSale =  notcancelledNotList.size();
	                
	                int gross = 0;

	                for(Order item : notcancelledNotList)
	                {
	                	gross +=item.getTotalAmount();
	                }
	                
	                int grossSaleAmount = gross;
	                
	               double tax=0;
	                
	                for(Order item : notcancelledNotList)
	                {
	                	
	                	List<OrderItem> products = item.getOrderItems();
	                	
	                	for(OrderItem product : products )
	                	{
	                		tax += product.getProduct().getPrice() * (5.0/100.0);
	                	}
	                }
	  
	                int netSaleAmount = (int) (grossSaleAmount - tax);
	                
	                contentStream.beginText();
                    contentStream.newLineAtOffset(textXPosition2 + 20, yPosition2 - rowHeight2);
                    contentStream.showText(String.valueOf(totalSale));
                    contentStream.newLineAtOffset(colWidth2, 0);
                    contentStream.showText(String.valueOf(grossSaleAmount));
                    contentStream.newLineAtOffset(colWidth2, 0);
                    contentStream.showText(String.valueOf(netSaleAmount));
                    contentStream.newLineAtOffset(colWidth2, 0);
                    contentStream.endText();
	            
	                
	             // Draw table borders
	                contentStream.setLineWidth(1f);
	                contentStream.moveTo(tableXPosition2, table2Y);
	                contentStream.lineTo(tableXPosition2, table2Y - tableHeight2);
	                contentStream.lineTo(tableXPosition2 + tableWidth2, table2Y - tableHeight2);
	                contentStream.lineTo(tableXPosition2 + tableWidth2, table2Y);
	                contentStream.closePath();
	                contentStream.stroke();
	                
	               
	            }

	            document.save(outputStream);
	        }
	 }


}

