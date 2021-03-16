package com.project.myblog.payload.request;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter @NoArgsConstructor
public class AdvisoryRequest {
	
	@NotNull
	private long commentId;
	
	@NotNull
	private long advisoryReasonId;
	
	private String description;

}
