package com.shopify.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopify.model.Category;
import com.shopify.model.User;
import com.shopify.repository.CategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    public List<Category> getAllCategory(){
        return categoryRepository.findAll();
    }
    
    public Page<Category> findPaginated(int pageNo, int pageSize) {
  		Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
  		return this.categoryRepository.findAll(pageable);
  	}

    public void addCategory(Category category){
        categoryRepository.save(category);
    }

    public void removeCategoryById(int id){
        categoryRepository.deleteById(id);
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepository.findById(id);
    }

    public boolean isCategoryNameExists(String categoryName){
        return categoryRepository.existsByName(categoryName);
    }

    public int getTotalCategories() {
        return (int) categoryRepository.count();
    }
}
