package com.project.myblog.service;

import com.project.myblog.entity.User;
import com.project.myblog.repository.CommentRepository;
import com.project.myblog.repository.PurchasedPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service
public class CreditService {

	@Autowired
    CommentRepository commentRepository;
	@Autowired
    PurchasedPostRepository purchasedPostRepository;
    
    public Long getCommentsWrote(User user) {
    	return commentRepository.countByCommentAuthor(user);
    }
    
    public Long getCommentsBanned(User user) {
    	return commentRepository.getCountBannedComments(user);
    }
    
    public Long getCreditsSpent(User user) {
    	return purchasedPostRepository.getTotalSpentCredit(user);
    }
    
    public Long getUserBalance(User user){
        return commentRepository.countByCommentAuthor(user) - commentRepository.getCountBannedComments(user) - purchasedPostRepository.getTotalSpentCredit(user);
    }

    public Long getTotalGain(User user){
        return commentRepository.countByCommentAuthor(user) - commentRepository.getCountBannedComments(user);
    }

}
