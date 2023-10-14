package com.shopify.util;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopify.model.Coupon;
import com.shopify.model.Order;
import com.shopify.model.OrderItem;
import com.shopify.model.User;
import com.shopify.service.UserService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


@Component
public class InvoiceGenerator {
	
	@Autowired
	UserService userService;

    public void generateInvoice(Order order, OutputStream outputStream,User user) throws IOException {
    	
    	
//    	
//    	for (OrderItem item : order.getOrderItems()) {
//    		
//    		int count= 0;
//    		
//    	}
    	List<OrderItem> orderItems = order.getOrderItems();
    	int count = orderItems.size();
    	
    	double productAmount = 0;
    	
    	Coupon appliedCoupon = user.getAppliedCoupon();
    	
         double discountAmount=0;
    	
    	
    	
    	if(count == 1)
    	{
    		productAmount = order.getProduct().getPrice();
    	}
    	
    	else if(count > 1) {
    		
    		
    		
    		for (OrderItem item : order.getOrderItems()) {
    			
    			productAmount += item.getProduct().getPrice();
    		}
    	}
    	
    	if(appliedCoupon != null)
    	{
    		
    		 discountAmount = order.getDiscountAmount();
    	}
    	
    	double taxAmount = productAmount*(5.0/100.0);
    	
 
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

                // Set the initial y-coordinate for the content
                float y = page.getMediaBox().getHeight() - 50;

                // Add the order details
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                contentStream.showText("Invoice");
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("Invoice for Order #" + order.getId());
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("Order Date: " + order.getFormattedOrderDate());
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("Order Time: " + order.getFormattedOrderTime());
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
//                contentStream.showText("Total Amount: " + order.getTotalAmount());
//                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.endText();
                
                
                float rightX = page.getMediaBox().getWidth() - 200;

             // Add company address
             contentStream.beginText();
             contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
             contentStream.newLineAtOffset(rightX, y); // Adjust y-coordinate as needed
             contentStream.showText("Shopify.com");
             contentStream.newLineAtOffset(0, -20);
             contentStream.showText("103,Baker Street");
             contentStream.newLineAtOffset(0, -20);
             contentStream.showText("Banglore, Karnataka");
             contentStream.newLineAtOffset(0, -20);
             contentStream.showText("560003,India");
             contentStream.newLineAtOffset(0, -20);
             contentStream.showText("GST Number: GSTN456789");
             contentStream.endText();




                // You can add more order details here as needed, adjusting the y-coordinate accordingly.

                // Add a table for displaying order items
                float tableY = y - 150; // Adjust the starting y-coordinate for the table
                float margin = 50;
                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
                float yPosition = tableY;
                int rows = order.getOrderItems().size() + 1; // Include header row
                float rowHeight = 20f;
                float tableHeight = rowHeight * rows;
                float colWidth = tableWidth / (float) 3; // 2 columns in the table

                // Draw table headers
                float tableXPosition = margin;
                float textXPosition = tableXPosition + 4;
                float textYPosition = yPosition - 15;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(textXPosition, textYPosition);
                contentStream.showText("Product Name");
                contentStream.newLineAtOffset(colWidth, 0);
                contentStream.showText("Quantity");
                contentStream.newLineAtOffset(colWidth, 0);
                contentStream.showText("Amount");
                contentStream.endText();
                yPosition -= rowHeight;

                // Draw table content
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                for (OrderItem item : order.getOrderItems()) {
                	
                	 contentStream.setLineWidth(0.5f); // Set the border line width
                	    contentStream.moveTo(tableXPosition, yPosition);
                	    contentStream.lineTo(tableXPosition + tableWidth, yPosition);
                	    contentStream.stroke();
                	    
                    contentStream.beginText();
                    contentStream.newLineAtOffset(textXPosition, yPosition - rowHeight);
                    contentStream.showText(item.getProduct().getName());
                    contentStream.newLineAtOffset(colWidth, 1);
                    contentStream.showText(String.valueOf(item.getQuantity()));
                    contentStream.newLineAtOffset(colWidth, 1);
                    contentStream.showText(String.valueOf(item.getProduct().getPrice()));
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
                
                // Adjust the y-coordinate to start after the table
                yPosition = tableY - tableHeight - 40;
                
                contentStream.beginText();
                contentStream.newLineAtOffset(rightX, yPosition);
                // Move down 20 points
                contentStream.showText("Sub Total : " + productAmount);
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("Discount Amount :-"+ discountAmount);
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("Shipping Charges : 40");
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("GST : "+taxAmount );
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.showText("Total Amount: " + order.getTotalAmount());
                contentStream.newLineAtOffset(0, -20); // Move down 20 points
                contentStream.endText();
                
             // Calculate the x-coordinate for the right side of the page
                float footerYposition = tableY - tableHeight - 200;

                // Calculate the y-coordinate for the "Thank You" note
                float thankYouY = 50; // Adjust the y-coordinate as needed

                // Add "Thank You" note
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(180, footerYposition); // Adjust y-coordinate as needed
                contentStream.showText("Thank You for Choosing Shopify!");
                contentStream.endText();

            }

            document.save(outputStream);
        }
    }
}
