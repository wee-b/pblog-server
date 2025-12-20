package com.pblog.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.pblog.admin.query.commentPageQueryDTO;
import com.pblog.common.dto.CommentDTO;
import com.pblog.common.entity.Comment;
import com.pblog.common.result.PageResult;
import com.pblog.common.vo.CommentForMeVO;
import com.pblog.common.vo.CommentFromMeVO;
import com.pblog.common.vo.CommentVO;

import java.util.List;


public interface CommentService extends IService<Comment> {

	PageResult pageQuery(commentPageQueryDTO pageQueryDTO);
	
	List<CommentVO> all(Integer articleId);

    List<CommentFromMeVO> fromMe();

    List<CommentForMeVO> forMe();

    CommentVO queryById(Integer id);

    Integer insert(CommentDTO commentdto);

    boolean update(Integer id ,CommentDTO commentdto);

    void setStatus(Integer id,String status);
}

