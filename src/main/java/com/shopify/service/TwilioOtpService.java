package com.shopify.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.MonoToListenableFutureAdapter;

import com.shopify.Dto.OtpStatus;
import com.shopify.Dto.VerificationRequestDto;
import com.shopify.Dto.VerificationResponseDto;
import com.shopify.model.User;
import com.shopify.twilioConfig.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import reactor.core.publisher.Mono;

@Service
public class TwilioOtpService {
	
	
	@Autowired
	private TwilioConfig twilioConfig;
	
	Map<String,String> otpMap = new HashMap<>();
	
	ArrayList<String> list = new ArrayList<String>();
	
	String otp;
	
	public String sendOtpForVerification(User user )
	{
		
		VerificationResponseDto verificationResponseDto = null;
		
		
		try {
			
			 PhoneNumber to = new PhoneNumber(user.getPhoneNumber());
			 
			 PhoneNumber from = new PhoneNumber(twilioConfig.getVerificatonNumber());
			 
			otp = generateOTP();
			
			String otpMessage = "Dear Customer, Your OTP is ##" + otp + "##. Use this passcode to complete your transaction. Thank you";

		
		Message message = Message.creator(
			      to,
			      from,
			      otpMessage)
			    .create();
		
		otpMap.put(user.getPhoneNumber(), otp);
		
		list.add(otp);
		
		verificationResponseDto = new VerificationResponseDto(otpMessage,OtpStatus.DELIVERED);
		}
		catch(Exception ex)
		{
			verificationResponseDto = new VerificationResponseDto(ex.getMessage(),OtpStatus.FAILED);

		}
		
		return "";
	}
	
	//6 digit otp
	
	private String generateOTP()
	{
		return new DecimalFormat("000000")
				.format(new Random().nextInt(999999));
	}
	
	
	public String getOtp()
	{
		return otp;
	}
	public String validateOtp(String userInputOtp,String phone_number)
	{
		
		if(userInputOtp.equals(otpMap.get(phone_number)) == true)
		{
			return "Valid OTP";
		}else
		{
			return "Invalid OTP";
		}
	}
	
	

}
