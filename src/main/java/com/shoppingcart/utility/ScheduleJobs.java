package com.shoppingcart.utility;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.shoppingcart.serviceimpl.AuthServiceImpl;

import lombok.AllArgsConstructor;
@Component
@AllArgsConstructor
public class ScheduleJobs {
	
	private AuthServiceImpl authServiceImpl;
	
	@Scheduled(cron = "0 0 0 * * ?")
	public void autoDeleteNonVerifiedUser() {
		authServiceImpl.removeNonVerifiedUser();
		System.out.println("Deleted...!");
	}
	@Scheduled(cron = "0 0 0 * * ?")
	public void autoDeleteExpiredTokens() {
		authServiceImpl.deleteExpiredTokens();
	}
}
