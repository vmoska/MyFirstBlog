package com.project.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ReportAuthor {

	private long id;
	private String username;
	private long nrWrittenPosts;
	private long nrViews;
	private double avgWrittenPosts;
}
