package com.shopify.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.shopify.model.Product;
import com.shopify.service.ProductService;


@Component
public class StockReportGenerator {
	
	@Autowired
	ProductService productService;
	
	  public void generateStockReporte(OutputStream outputStream) throws IOException {
	        try (PDDocument document = new PDDocument()) {
	            PDPage page = new PDPage(PDRectangle.A4);
	            document.addPage(page);

	            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
	            	
	            	contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16); // Larger font size for the title

	                // Set the initial y-coordinate for the title
	                float titleY = page.getMediaBox().getHeight() - 30;

	                // Add the title
	                contentStream.beginText();
	                contentStream.newLineAtOffset(250, titleY);
	                contentStream.showText("Stock Report"); // Add the title here
	                contentStream.endText();
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

	                // Set the initial y-coordinate for the content
	                float y = page.getMediaBox().getHeight() - 50;

	                // Add the order details
	                List<Product> productList = productService.getAllProduct();
	                // Add a table for displaying order items
	                float tableY = y - 50; // Adjust the starting y-coordinate for the table
	                float margin = 50;
	                float tableWidth = page.getMediaBox().getWidth() - 2 * margin;
	                float yPosition = tableY;
	                int rows = productList.size() + 1; // Include header row
	                float rowHeight = 20f;
	                float tableHeight = rowHeight * rows;
	                float colWidth = tableWidth / (float) 5; // 2 columns in the table

	                // Draw table headers
	                float tableXPosition = margin;
	                float textXPosition = tableXPosition + 4;
	                float textYPosition = yPosition - 15;
	                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
	                contentStream.beginText();
	                contentStream.newLineAtOffset(textXPosition, textYPosition);
	                contentStream.showText("Product Name");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Stock Quantity");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Unit Price");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Unit Weight");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.showText("Category");
	                contentStream.newLineAtOffset(colWidth, 0);
	                contentStream.endText();
	                yPosition -= rowHeight;

	                // Draw table content
	                contentStream.setFont(PDType1Font.HELVETICA, 12);
	                for (Product item : productList) {
	                	contentStream.setLineWidth(0.5f); // Set the border line width
                	    contentStream.moveTo(tableXPosition, yPosition);
                	    contentStream.lineTo(tableXPosition + tableWidth, yPosition);
                	    contentStream.stroke();
                	    
                	    
	                    contentStream.beginText();
	                    contentStream.newLineAtOffset(textXPosition, yPosition - rowHeight);
	                    contentStream.showText(item.getName());
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(String.valueOf(item.getStockQuantity()));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(String.valueOf(item.getPrice()));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(String.valueOf(item.getWeight()));
	                    contentStream.newLineAtOffset(colWidth, 0);
	                    contentStream.showText(item.getCategory().getName());
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
	            }

	            document.save(outputStream);
	        }
	  }

}
