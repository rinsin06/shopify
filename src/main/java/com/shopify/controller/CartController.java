package com.shopify.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.shopify.model.Address;
import com.shopify.model.Coupon;
import com.shopify.model.CouponUsage;
import com.shopify.model.Product;
import com.shopify.model.User;
import com.shopify.model.UserCart;
import com.shopify.repository.CountryRepository;
import com.shopify.repository.CouponUsageRepository;
import com.shopify.service.CouponService;
import com.shopify.service.CouponUsageService;
import com.shopify.service.ProductService;
import com.shopify.service.UserService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CartController {

    @Autowired
    ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CouponUsageRepository couponUsageRepository;
    
    @Autowired
    private CouponUsageService couponUsageService;

    @GetMapping("/addToCart/{id}")
    public String addToCart(@PathVariable long id, Principal principal) {
        Product product = productService.getProductById(id).orElse(null);
        if (product != null) {
            User user = userService.getUserByEmail(principal.getName());
            userService.addToUserCart(user, product);
        }
        return "redirect:/shop";
    }

    @GetMapping("/cart")
    public String getCart(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        UserCart userCart = userService.getUserCart(user);

        double totalCartAmount = userCart.getTotalCartAmount();

        model.addAttribute("cartCount", userCart.getProducts().size());
        model.addAttribute("total", totalCartAmount);
        model.addAttribute("cart", userCart.getProducts());

        return "cart";
    }

    @GetMapping("/cart/removeItem/{id}")
    public String cartItemRemove(@PathVariable long id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        userService.removeFromUserCart(user, id);
        return "redirect:/cart";
    }

    @PostMapping("/applyCoupon")
    public String applyCoupon(@RequestParam("couponCode") String couponCode, Principal principal,Model model) {
        User user = userService.getUserByEmail(principal.getName());
        UserCart userCart = userService.getUserCart(user);

        Coupon coupon = couponService.getCouponByCode(couponCode);

        if (coupon != null && !user.hasUsedCoupon(coupon)) {
            // Apply the coupon discount to the cart total
            double totalCartAmount = userCart.getTotalCartAmount();
            double discountAmount = totalCartAmount * (coupon.getDiscountPercentage() / 100);
            totalCartAmount -= discountAmount;

            // Update the cart with the discounted amount
            userCart.setTotalCartAmount(totalCartAmount);
            userService.saveUserCart(userCart);

            // Mark the coupon as used by creating a CouponUsage entity
            CouponUsage couponUsage = new CouponUsage();
            couponUsage.setUser(user);
            couponUsage.setCoupon(coupon);
            couponUsage.setUsedAt(LocalDateTime.now());
            couponUsageRepository.save(couponUsage);
            
            
            model.addAttribute("discountAmount", discountAmount);
            
           

            return "redirect:/checkout";
        }

        return "redirect:/checkout?couponError=true"; // Invalid coupon or already used
    }
    
    @PostMapping("/applySingleCoupon")
    public String applySingleCoupon(@RequestParam("couponCode") String couponCode, Principal principal,@RequestParam("productId") Long productId,Model model) {
        User user = userService.getUserByEmail(principal.getName());
        
        Product product = productService.getProductById1(productId);

        Coupon coupon = couponService.getCouponByCode(couponCode);

        if (coupon != null && !user.hasUsedCoupon(coupon)) {
            // Apply the coupon discount to the cart total
            double Amount = product.getPrice();
            double discountAmount = Amount * (coupon.getDiscountPercentage() / 100);
            Amount -= discountAmount;

            // Update the cart with the discounted amount
             
            

            // Mark the coupon as used by creating a CouponUsage entity
            CouponUsage couponUsage = new CouponUsage();
            couponUsage.setUser(user);
            couponUsage.setCoupon(coupon);
            couponUsage.setUsedAt(LocalDateTime.now());
            couponUsageRepository.save(couponUsage);
            
          
            model.addAttribute("discountAmount", discountAmount);
            model.addAttribute("productId",productId);
            
            return "redirect:/checkoutBuy/"+productId;
        }

        return "redirect:/checkout?couponError=true"; // Invalid coupon or already used
    }
    
    



    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        UserCart userCart = userService.getUserCart(user);

        List<Address> addresses = user.getAddresses();
        model.addAttribute("addresses", addresses);

        List<Product> cartProducts = userCart.getProducts();

        double totalCartAmount = userCart.getTotalCartAmount();
        double totalProductAmount = userCart.getTotalCartAmount();
        Coupon appliedCoupon = user.getAppliedCoupon();

        double shippingCharge = 40.0;


        double taxPercentage = 0.05; // 5%
        double taxAmount = userCart.getTotalCartAmount() * taxPercentage;

        double totalAmount;

        if (appliedCoupon != null) {
            double discountAmount = totalCartAmount * (appliedCoupon.getDiscountPercentage() / 100);
            totalCartAmount -= discountAmount;
            model.addAttribute("discountAmount", discountAmount);
           
            // Calculate the total amount
            totalAmount = totalCartAmount + shippingCharge + taxAmount;
        }
        else {
            // Calculate the total amount
            totalAmount = userCart.getTotalCartAmount() + shippingCharge + taxAmount;
        }
        
        		
      	
        model.addAttribute("appliedCoupon", appliedCoupon);
        model.addAttribute("cartProducts", cartProducts);
        model.addAttribute("cartAmount", totalCartAmount);
        model.addAttribute("totalProductAmount",totalProductAmount);
        model.addAttribute("shippingCharge", shippingCharge);
        model.addAttribute("taxAmount", taxAmount);
        model.addAttribute("total", totalAmount);

        return "checkout";
    }
    
    @GetMapping("/removeCoupon")
    public String removeCoupon(Model model,Principal principal)
    {
    	 User user = userService.getUserByEmail(principal.getName());
         UserCart userCart = userService.getUserCart(user);

         List<Address> addresses = user.getAddresses();
         model.addAttribute("addresses", addresses);

         List<Product> cartProducts = userCart.getProducts();

         double totalCartAmount = userCart.getTotalCartAmount();
         
         Coupon appliedCoupon = user.getAppliedCoupon();
         
         couponUsageService.deleteCouponUsage(user, appliedCoupon);
         
  
         return "redirect:/checkout";

//         double shippingCharge = 40.0;
//
//
//         double taxPercentage = 0.05; // 5%
//         double taxAmount = userCart.getTotalCartAmount() * taxPercentage;
//
//         double totalAmount;
//         
//         totalAmount = userCart.getTotalCartAmount() + shippingCharge + taxAmount;
//         
//         
//        
//         model.addAttribute("cartProducts", cartProducts);
//         model.addAttribute("cartAmount", totalCartAmount);
//         model.addAttribute("shippingCharge", shippingCharge);
//         model.addAttribute("taxAmount", taxAmount);
//         model.addAttribute("total", totalAmount);
//
//         return "checkout";
         
    }
    
    @PostMapping("/removeSingleCoupon")
    public String removeSingleCoupon(Model model,Principal principal,@RequestParam("productId") Long productId)
    {
    	 User user = userService.getUserByEmail(principal.getName());
         UserCart userCart = userService.getUserCart(user);
         
      

         List<Address> addresses = user.getAddresses();
         model.addAttribute("addresses", addresses);

       
         Coupon appliedCoupon = user.getAppliedCoupon();
         
         couponUsageService.deleteCouponUsage(user, appliedCoupon);

         return "redirect:/checkoutBuy/"+productId;
         
    }

    @GetMapping("/checkoutBuy/{productId}")
    public String checkoutBuy(@PathVariable Long productId, Model model, Principal principal) {

    	
            User user = userService.getUserByEmail(principal.getName());

            List<Address> addresses = user.getAddresses();
            model.addAttribute("addresses", addresses);
            
            Coupon appliedCoupon = user.getAppliedCoupon();

            Optional<Product> product = productService.getProductById(productId);
            model.addAttribute("product", product);

            double productAmount = product.get().getPrice();
            
            double totalProductAmount = product.get().getPrice();

           

            double shippingCharge = 40.0;


            double taxPercentage = 0.05; // 5%
            double taxAmount = product.get().getPrice() * taxPercentage;

            double totalAmount;

            if (appliedCoupon != null) {
                double discountAmount = productAmount * (appliedCoupon.getDiscountPercentage() / 100);
                productAmount -= discountAmount;
                
                model.addAttribute("discountAmount", discountAmount);

                // Calculate the total amount
                totalAmount = productAmount + shippingCharge + taxAmount;
            }
            else {
                // Calculate the total amount
                totalAmount = product.get().getPrice() + shippingCharge + taxAmount;
            }
            
            model.addAttribute("product",product);
            model.addAttribute("totalProductAmount", totalProductAmount);
            model.addAttribute("appliedCoupon", appliedCoupon);
            model.addAttribute("productAmount", productAmount);
            model.addAttribute("shippingCharge", shippingCharge);
            model.addAttribute("taxAmount", taxAmount);
            model.addAttribute("total", totalAmount);

            return "checkoutBuy";
    }


}
