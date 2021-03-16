package com.project.myblog.payload.request;

import com.project.myblog.entity.AdvisoryStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter @NoArgsConstructor 
public class AdvisoryIdRequest {
	
	@NotNull
	private long commentId;
	@NotNull
	private long userId;
	@NotNull
	private long advisoryReasonId;
	@NotNull
	private AdvisoryStatus status;
	
}
