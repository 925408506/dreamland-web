package wang.dreamland.www.dao;

import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import wang.dreamland.www.entity.Comment;

import java.util.List;

/**
 * Created by Administrator on 2018/2/27.
 */
public interface CommentMapper extends Mapper<Comment> {
    /**
     * /根据文章id查询所有评论
     * @param cid
     * @return
     */
    List<Comment> selectAll(@Param("cid")long cid);

    /**
     * 根据文章id查询所有一级评论
     * @param cid
     * @return
     */
    List<Comment> findAllFirstComment(@Param("cid")long cid);

    /**
     * //根据文章id和二级评论ids查询出所有二级评论
     * @param cid
     * @param children
     * @return
     */
    List<Comment> findAllChildrenComment(@Param("cid")long cid,@Param("children")String children);

    /**
     * 插入评论并返回主键id 返回值是影响行数  id在Comment对象中
     * @param comment
     * @return
     */
    int insertComment(Comment comment);

    /**
     * 根据cid删除评论
     * @param cid
     * @return
     */
    int deleteByContentId(Long cid);

}
