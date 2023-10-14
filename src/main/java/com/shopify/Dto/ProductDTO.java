package com.shopify.Dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private int categoryId;
    private double price;
    private int stockQuantity;
    private double weight;
    private String description;
    private List<String> imageNames;
    private List<MultipartFile> images;
}
