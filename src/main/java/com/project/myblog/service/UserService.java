package com.project.myblog.service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.myblog.entity.Authority;
import com.project.myblog.entity.AuthorityName;
import com.project.myblog.entity.User;
import com.project.myblog.payload.response.ResponseEntityHandler;
import com.project.myblog.repository.AuthorityRepository;
import com.project.myblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;
	@Autowired
	AuthorityRepository authorityRepository;

public User getAuthenticatedUser() {
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		List<String> auts = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

		if(!auts.contains("ROLE_ANONYMOUS")) {
			return userRepository.findByUsername(authentication.getName()).get();
		}else {
			return null;
		}
		
	}

	public void setAuthority(long countUsers, User user) {
		Optional<Authority> userAuthority = Optional.empty();

		if (countUsers > 0) {
			userAuthority = authorityRepository.findByName(AuthorityName.ROLE_READER);
		} else {
			userAuthority = authorityRepository.findByName(AuthorityName.ROLE_ADMIN);
		}

		user.setAuthorities(Collections.singleton(userAuthority.get()));
	}

	public byte[] getSHA(String input) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(input.getBytes(StandardCharsets.UTF_8));
	}

	public String toHexString(byte[] hash) {

		BigInteger number = new BigInteger(1, hash);
		StringBuilder hexString = new StringBuilder(number.toString(16));

		while (hexString.length() < 32) {
			hexString.insert(0, '0');
		}

		return hexString.toString().toUpperCase();

	}

	public Date adjustDate(Date date) {
		LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		return Date.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
	}

	public boolean compareTwoUser(User user1, User user2, ResponseEntityHandler response) {

		if (user1.getId().equals(user2.getId())) {
			response.setMsg("Identical user");
			response.setStatus(HttpStatus.FORBIDDEN);
			return true;
		}
		return false;

	}

}
