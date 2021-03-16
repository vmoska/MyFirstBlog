package com.project.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class PurchaseReportResponse {
	
	private String username;
	private long totalGain;
	private long totalSpent;
	private long balance;

}
