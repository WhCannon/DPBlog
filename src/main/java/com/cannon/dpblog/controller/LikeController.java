package com.cannon.dpblog.controller;

import com.cannon.dpblog.entity.User;
import com.cannon.dpblog.event.Event;
import com.cannon.dpblog.event.EventProducer;
import com.cannon.dpblog.service.LikeService;
import com.cannon.dpblog.util.CommunityConstant;
import com.cannon.dpblog.util.CommunityUtil;
import com.cannon.dpblog.util.HostHolder;
import com.cannon.dpblog.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private EventProducer eventProducer;

    @Autowired
    private RedisTemplate redisTemplate;

    /*
            点赞功能
     */
    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId,int postId) {
        User user = hostHolder.getUser();

        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 对该实体点赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        // 触发点赞事件
        if (likeStatus == 1) {
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId", postId);
            eventProducer.fireEvent(event);
        }

         if(entityType == ENTITY_TYPE_POST){
             // 计算帖子分数
             String redisKey = RedisKeyUtil.getPostScoreKey();
             redisTemplate.opsForSet().add(redisKey, postId);
         }

        return CommunityUtil.getJSONString(0, null, map);
    }

}
