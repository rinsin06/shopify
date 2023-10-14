package com.shopify.twilioConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties("twilio")
@Data
public class TwilioConfig {
	
	@Value("${twilio.account_sid}")
	private String accountSid;
	
	@Value("${twilio.auth_token}")
	private String autToken;
	
	@Value("${twilio.verification_number}")
	private String verificatonNumber;
	

}
