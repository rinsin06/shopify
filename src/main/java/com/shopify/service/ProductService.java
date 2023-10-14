package com.shopify.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.shopify.model.Category;
import com.shopify.model.Order;
import com.shopify.model.Product;
import com.shopify.model.ProductImage;
import com.shopify.repository.CategoryRepository;
import com.shopify.repository.OrderRepository;
import com.shopify.repository.ProductImageRepository;
import com.shopify.repository.ProductRepository;

import javax.persistence.EntityNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    ProductImageRepository productImageRepository;

    @Autowired
    CategoryRepository categoryRepository;
    
    @Autowired
    OrderRepository orderRepository;

    public List<Product> getAllProduct(){
        return productRepository.findAll();
    }
    
    
	public Page<Product> findPaginated(int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
		return this.productRepository.findAll(pageable);
	}
    
    public List<Product> getAllNonDeletedProduct(){
        return productRepository.findByIsDeleted();
    }

    public void addProduct(Product product) {
        for (ProductImage image : product.getImages()) {
            image.setProduct(product);
        }
        productRepository.save(product);
    }

    public void removeProductById(Long id){
    	 Iterable<Long> singleId = Collections.singleton(id);
         
    	disassociateProductFromOrders(singleId);
        productRepository.deleteById(id);
    }
    
    private void disassociateProductFromOrders(Iterable<Long> productId) {
        List<Order> ordersWithProduct = orderRepository.findAllById(productId);
        for (Order order : ordersWithProduct) {
            // Set the product reference to null or remove it, depending on your mapping
            order.setProduct(null); // Example: Set product reference to null
            orderRepository.save(order); // Save the updated order
        }
    }

    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }
    
    public Product getProductById1(Long id){
        return productRepository.getReferenceById(id);
    }

    public List<Product> getAllProductsByCategoryId(int id){
        return productRepository.findAllByCategory_Id(id);
    }

    public Long saveImageAndGetId(ProductImage image) {
        ProductImage savedImage = productImageRepository.save(image);
        return savedImage.getId();
    }

    public Long saveImageAndGetId(String imageName) {
        ProductImage image = new ProductImage();
        image.setImageName(imageName);
        ProductImage savedImage = productImageRepository.save(image);

        return savedImage.getId();
    }

    public List<Product> searchProducts(String query) {
    	
    	if(query != null)
    	{
    		 return productRepository.search(query);
    	}
       
        return productRepository.findAll();
        
    }

    public void saveProduct(Product product){
        productRepository.save(product);
    }

    @Transactional
    public void restockProduct(Long productId, int quantity) {
        Product product = getProductById(productId).orElseThrow(() -> new EntityNotFoundException("Product not found"));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        saveProduct(product);
    }

    public boolean isProductNameExists(String productName){
        return productRepository.existsByName(productName);
    }

    public void addCategory(Category category) {
        categoryRepository.save(category);
    }

    public List<Product> getProductsLowStock() {
        return productRepository.findLowStockProducts();
    }

    public List<Product> getProductsByIds(List<Long> productIds) {
        return productRepository.findAllById(productIds);
    }

    public int getTotalProducts() {
        return (int) productRepository.count();
    }



}
