package com.project.myblog.payload.response;

import java.util.Set;

import com.project.myblog.entity.AuthorityName;
import com.project.myblog.entity.DbFile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResponse {
	
	private String username;
	private String email;
	private Set<AuthorityName> authorities;
	private DbFile avatar;

}
