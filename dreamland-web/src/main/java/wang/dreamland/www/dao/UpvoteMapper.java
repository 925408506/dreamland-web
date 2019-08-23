package wang.dreamland.www.dao;

import tk.mybatis.mapper.common.Mapper;
import wang.dreamland.www.entity.Upvote;

public interface UpvoteMapper extends Mapper<Upvote>{
    /**
     * 根据cid删除评论
     * @param cid
     * @return
     */
    int deleteByContentId(Long cid);
}