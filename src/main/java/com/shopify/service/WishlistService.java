package com.shopify.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopify.model.Product;
import com.shopify.model.User;
import com.shopify.model.WishlistItem;
import com.shopify.repository.WishlistRepository;

import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    public List<WishlistItem> getUserWishlist(User user) {
        return wishlistRepository.findByUser(user);
    }

    public void addToWishlist(User user, Product product) {
        WishlistItem wishlistItem = new WishlistItem();
        wishlistItem.setUser(user);
        wishlistItem.setProduct(product);
        wishlistRepository.save(wishlistItem);
    }

    public void removeFromWishlist(Long wishlistItemId) {
        wishlistRepository.deleteById(wishlistItemId);
    }
}

