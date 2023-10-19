package com.shopify.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.shopify.model.Role;
import com.shopify.model.User;
import com.shopify.repository.RoleRepository;
import com.shopify.repository.UserRepository;
import com.shopify.service.TwilioOtpService;
import com.shopify.service.UserService;

import io.netty.handler.codec.http.HttpRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserRegistration {

	private final UserService userService;
	
	TwilioController twiliocontroller;
	
	@Autowired
	TwilioOtpService otpService;
	
	@Autowired
	UserRepository userRepository;
	
	
	  @Autowired
	  RoleRepository roleRepository;
	  
	  @Autowired
	    private BCryptPasswordEncoder bCryptPasswordEncoder;
	  
	  String email = "rinsin06@gmail.com";

	
	
//	@GetMapping("/home")
//	public String goToHomePage(Model model) {
//		model.addAttribute("user", new User());
//		return "index";
//	}
	
	@GetMapping("/register")
	public String goToRegisterPage(Model model) {
		model.addAttribute("user", new User());
		
		
		return "index";
	}  

	@PostMapping("/register")
	public String Register(@ModelAttribute("user") User user, HttpServletRequest request, Model model) throws ServletException {
		
		Optional<User> existingUserOptional = userRepository.findByEmail(user.getEmail());
		boolean userExists;

			userExists = false;
			
			 String password = user.getPassword();
		        user.setPassword(bCryptPasswordEncoder.encode(password));
		        List<Role> roles = new ArrayList<>();
//		        roles.add(roleRepository.findById(2).get());
//		        user.set("ROLE_USER");
		        Role role = null;
		        role = roleRepository.findByName("ROLE_USER");
	            if(role == null){
	                role = checkRoleExist("ROLE_USER");
	            }
	            
	        	otpService.sendOtpForVerification(user);
	   		 
	        	user.setActive(true);
	   		    user.setRoles(Arrays.asList(role));
	            userRepository.save(user);
	   		    
	   		    model.addAttribute("user",user);

	            model.addAttribute("phone_number", user.getPhoneNumber());

	            model.addAttribute("email",user.getEmail());
	            return "verifyotp";


		
	}
//	@PostMapping("/createAdmin")
//	public void createAdmin(User user)
//	{
//		user.setFirstName("admin");
//		user.setLastName("admin");
//		user.setPassword(bCryptPasswordEncoder.encode("admin"));
//		Role role = checkRoleExist("ROLE_ADMIN");
//		 user.setActive(true);
//		 user.setRoles(Arrays.asList(role));
//		 user.setOtpVerified(true);
//		 userRepository.save(user);
//		
//		
//	}
	
	
	 private Role checkRoleExist(String roleValue) {
	        Role role = new Role();
	        role.setName(roleValue);
	        return roleRepository.save(role);
	    }
	
	
}
