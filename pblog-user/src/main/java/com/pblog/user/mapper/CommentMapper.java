package com.pblog.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.Comment;
import com.pblog.common.vo.CommentForMeVO;
import com.pblog.common.vo.CommentFromMeVO;
import com.pblog.common.vo.CommentVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface CommentMapper extends BaseMapper<Comment> {


    List<CommentVO> selectAllByArticleId(Integer articleId);

    List<CommentFromMeVO> selectCommentsFromMe(String username);

    List<CommentForMeVO> selectCommentsForMe(String username);
}
