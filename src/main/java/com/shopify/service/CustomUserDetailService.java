package com.shopify.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shopify.model.CustomUserDetail;
import com.shopify.model.User;
import com.shopify.repository.UserRepository;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        Optional<User> user = userRepository.findUserByEmail(email);
//        user.orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
//        return user.map(CustomUserDetail:: new).get();
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    	 Optional<User> userOptional = userRepository.findByEmail(email);
         if (userOptional.isEmpty()) {
             throw new UsernameNotFoundException("User not found");
         }

         User user = userOptional.get();

         if (user.isBlocked()) {
             throw new DisabledException("User is blocked");
         }

         // Check if user is OTP verified
         boolean isOtpVerified = user.getOtp() == null || user.isOtpVerified();

         CustomUserDetail customUserDetail = new CustomUserDetail(user);
         customUserDetail.setOtpVerified(isOtpVerified);

         return customUserDetail;
    }
    public boolean isUserOtpVerified(String email) {
    	Optional<User> userOptional = userRepository.findByEmail(email);
        return userOptional.map(user -> user.getOtp() != null && user.isOtpVerified()).orElse(false);
    }
    
}
