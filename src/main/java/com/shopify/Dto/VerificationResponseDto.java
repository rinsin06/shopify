package com.shopify.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class VerificationResponseDto {
	
	private String message;
	
	private OtpStatus status;

}
