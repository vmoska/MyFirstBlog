package com.project.myblog.service;


import com.project.myblog.repository.LogoutTraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Service
public class TokensPurgeTask {

	@Autowired
    private LogoutTraceRepository logoutTraceRepository;

    @Scheduled(cron = "${purge.cron.expression}")
    public void purgeExpired() {
    	logoutTraceRepository.deleteExpiredTokens();
    }
}
