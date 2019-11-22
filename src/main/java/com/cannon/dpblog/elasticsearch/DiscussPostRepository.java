package com.cannon.dpblog.elasticsearch;

import com.cannon.dpblog.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository <DiscussPost, Integer> {

}
