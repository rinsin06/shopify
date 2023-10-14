package com.shopify;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.shopify.twilioConfig.TwilioConfig;
import com.twilio.Twilio;

@SpringBootApplication
public class DemoApplication {
	
	@Autowired
	private TwilioConfig twilioConfig;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	
	@PostConstruct
	public void initTwilio()
	{
		Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAutToken());
	}

}
