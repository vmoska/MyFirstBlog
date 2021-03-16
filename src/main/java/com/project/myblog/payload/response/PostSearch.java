package com.project.myblog.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class PostSearch {
	
	private long id;
	private String title;
	private String content;
	private String author;
	
	

}
