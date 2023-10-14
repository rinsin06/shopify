package com.shopify.controller;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.shopify.model.User;
import com.shopify.repository.UserRepository;
import com.shopify.service.TwilioOtpService;
import com.shopify.service.UserService;

import io.netty.handler.codec.http.HttpRequest;

@Controller

 
public class TwilioController {
	
	
	
	@Autowired
	TwilioOtpService otpService;
	
	@Autowired
	UserRepository repository;

	@PostMapping("/sendotp")
	
	public String sendOtp(@ModelAttribute("user") User user,Model model)
	{
	
		otpService.sendOtpForVerification(user);
		
		model.addAttribute("user",user);
		
		model.addAttribute("phoneNumber",user.getPhoneNumber());

		return "verifyotp";
	}
	
	@PostMapping("/verify-otp")
	public String verifyotp(@RequestParam(value = "email") String email, @RequestParam(value = "otp") String otp,Model model) {
	
		Optional<User> userOptional = repository.findByEmail(email);
		
		 User user = userOptional.get();
		 
		String phone_number = user.getPhoneNumber();
		
	    String msg = otpService.validateOtp(otp,phone_number);

	    
	    if (msg.equals("Valid OTP")) { // Use .equals() to compare strings
	    	
//	    	user.setVerified(true);
	
	    	 
	    	 user.setOtpVerified(true);
             repository.save(user);
	    	
	    	model.addAttribute("success", true);
	    	
	    	model.addAttribute("user",user);
	    	
	        return "redirect:/";
	    } else {
	    	model.addAttribute("phone_number",phone_number);
	    	
	    	model.addAttribute("email",email);
	    	
	    	
	    	
	        return "verifyotp";
	    }
	}

}


