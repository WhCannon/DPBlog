package com.cannon.dpblog.service;

import com.cannon.dpblog.dao.DiscussPostMapper;
import com.cannon.dpblog.entity.DiscussPost;
import com.cannon.dpblog.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Scanner;

@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostConstruct
    public void initCache(){



    }

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if(userId==0 && orderMode==1){
            List<DiscussPost> list = redisTemplate.opsForList().range("hotPost",offset,offset+limit);
            System.out.println(list.size());
            if(list==null || list.size()==0){
                System.out.println("redis缓存热门帖子");
                list = discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
                redisTemplate.opsForList().rightPushAll("hotPost",list);
            }
            return list;
        }
        return discussPostMapper.selectDiscussPosts(userId, offset, limit,orderMode);
    }

    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    public int addDiscussPost(DiscussPost post) {
        Scanner sc = new Scanner(System.in);
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空!");
        }
        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        return discussPostMapper.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    public int updateType(int id, int type) {

        return discussPostMapper.updateType(id, type);
    }

    public int updateStatus(int id, int status) {

        return discussPostMapper.updateStatus(id, status);
    }

    public int updateScore(int id, double score) {
        return discussPostMapper.updateScore(id, score);
    }

}
