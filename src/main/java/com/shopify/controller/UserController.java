package com.shopify.controller;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopify.model.Category;
import com.shopify.model.User;
import com.shopify.repository.UserRepository;
import com.shopify.service.CategoryService;
import com.shopify.service.TwilioOtpService;
import com.shopify.service.UserService;

@RequiredArgsConstructor
@Controller
public class UserController {
	
//	@GetMapping("/home")
//	public String home()
//	{
//		return "index";
//	}
	
	@Autowired
	UserService userService;
	
	 @Autowired
	 private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	CategoryService categorieService;
	
	@Autowired
	UserRepository repository;
	
	@Autowired
	TwilioOtpService otpService;

	@GetMapping("/login")
	public String login(Model model) {
		List<Category> categories = categorieService.getAllCategory();
        model.addAttribute("categories", categories);
     model.addAttribute("loginSucess",true);
		model.addAttribute("user", new User());
		 model.addAttribute("pageTitle", "Login Page");
	        return "index";
	}
	
	

	private boolean isAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || AnonymousAuthenticationToken.class.isAssignableFrom(authentication.getClass())) {
			return false;
		}
		return authentication.isAuthenticated();
	}
	
	@GetMapping("/products")
	   public String getProducts(Model model)
	   {
		List<Category> categories = categorieService.getAllCategory();
        model.addAttribute("categories", categories);
     model.addAttribute("loginSucess",true);
		   return"products";
	   }
	
	//password Reset 
	
	@GetMapping("/forgetPassword")
	public String forgetPasswordForm()
	{
		return "foregetPassword";
	}
	
	@PostMapping("/passwordReset-email")
	public String sendOtpForVerification(@RequestParam(value = "email") String email,Model model)
	{
		Optional<User> userOptional = repository.findByEmail(email);
		
		 User user = userOptional.get();
		 
		 otpService.sendOtpForVerification(user);
		 
		 model.addAttribute("user",user);
		 
		 model.addAttribute("email",email);
			
			model.addAttribute("phoneNumber",user.getPhoneNumber());

			return "forgetPasswordVerification";
		
	}
	
	@PostMapping("/verifyPassword-otp")
	public String verifyPasswordotp(@RequestParam(value = "email") String email, @RequestParam(value = "otp") String otp,Model model) {
	
		Optional<User> userOptional = repository.findByEmail(email);
		
		 User user = userOptional.get();
		 
		String phone_number = user.getPhoneNumber();
		
	    String msg = otpService.validateOtp(otp,phone_number);

	    
	    if (msg.equals("Valid OTP")) { // Use .equals() to compare strings
	    	
//	    	user.setVerified(true);
	    	
	    	model.addAttribute("email",email);

	    	model.addAttribute("success", true);
	    	
	    	model.addAttribute("user",user);
	    	
	        return "resetPassword";
	    } else {
	    	model.addAttribute("phone_number",phone_number);
	    	
	    	model.addAttribute("email",email);
	    	
	    	
	    	
	        return "forgetPasswordVerification";
	    }
	}
	
	@PostMapping("/reset-password")
	public String changePassword(@RequestParam(value = "email") String email,
	                             @RequestParam("newPassword") String newPassword,
	                             @RequestParam("confirmPassword") String confirmPassword,
	                             Model model, Principal principal) {
		
		Optional<User> userOptional = repository.findByEmail(email);
		
		 User user = userOptional.get();
		 
	
	    if (!newPassword.equals(confirmPassword)) {
	        // New password and confirmation do not match, display an error message
	        model.addAttribute("passwordError", "New password and confirmation do not match");
	    } else {
	        // Update the user's password
	        userService.updateUserPassword(user, newPassword);
	        model.addAttribute("passwordSuccess", "Password updated successfully");
	    }

	    model.addAttribute("user", user);
	    return "redirect:/login";
	}

}

