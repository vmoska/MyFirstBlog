package com.project.myblog.repository;

import java.util.Date;
import java.util.Optional;

import com.project.myblog.entity.Post;
import com.project.myblog.entity.PostCost;
import com.project.myblog.entity.PostCostId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostCostRepository extends JpaRepository<PostCost, PostCostId> {

	Optional<PostCost> findByEndDateEqualsAndPostCostIdPost(Date endDate, Post id);
    boolean existsByPostCostIdPost(Post post);
}
