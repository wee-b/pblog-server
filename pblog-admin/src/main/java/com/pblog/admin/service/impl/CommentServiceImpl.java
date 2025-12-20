package com.pblog.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pblog.admin.query.commentPageQueryDTO;
import com.pblog.admin.vo.CommentDetailVO;
import com.pblog.common.Expection.BusinessException;
import com.pblog.common.constant.DefaultConstants;
import com.pblog.common.dto.CommentDTO;
import com.pblog.common.dto.PageQueryDTO;
import com.pblog.common.entity.Comment;
import com.pblog.common.result.PageResult;
import com.pblog.common.utils.SecurityContextUtil;
import com.pblog.common.vo.CommentForMeVO;
import com.pblog.common.vo.CommentFromMeVO;
import com.pblog.common.vo.CommentVO;
import com.pblog.admin.mapper.CommentMapper;
import com.pblog.admin.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

	@Autowired
    private CommentMapper commentMapper;

    @Override
    public PageResult pageQuery(commentPageQueryDTO pageQueryDTO) {
        Page<CommentDetailVO> page = new Page<>(pageQueryDTO.getPageNum(), pageQueryDTO.getPageSize());
        IPage<CommentDetailVO> pageRes = commentMapper.selectCommentPage(page, pageQueryDTO);


        return new PageResult(
                pageRes.getTotal(),
                pageRes.getPages(),
                pageRes.getCurrent(),
                pageRes.getSize(),
                pageRes.getRecords()
        );
    }
    
    @Override
    public List<CommentVO> all(Integer articleId) {
        List<CommentVO> lis = commentMapper.selectAllByArticleId(articleId);
        return lis;
    }

    @Override
    public List<CommentFromMeVO> fromMe() {
        String username = SecurityContextUtil.getUsername();
        List<CommentFromMeVO> lis = commentMapper.selectCommentsFromMe(username);
        return lis;
    }

    @Override
    public List<CommentForMeVO> forMe() {
        String username = SecurityContextUtil.getUsername();
        List<CommentForMeVO> lis = commentMapper.selectCommentsForMe(username);
        return lis;
    }

    @Override
    public CommentVO queryById(Integer id) {
        Comment one = commentMapper.selectById(id);
        CommentVO vo = new CommentVO();
        BeanUtils.copyProperties(one, vo);
        return vo;
    }

    @Override
    public Integer insert(CommentDTO commentdto) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(commentdto, comment);
        // 设置一些字段

        // 文章Id为0代表该评论为留言板评论
        if (commentdto.getArticleId() == null || commentdto.getArticleId() == 0){
            comment.setArticleId(DefaultConstants.ZERO);
            comment.setUsername("0000");
        }else{
            comment.setUsername(SecurityContextUtil.getUsername());
        }
        // 没传代表根评论Id为0
        if (commentdto.getRootId() == null){
            comment.setRootId(DefaultConstants.ZERO);
        }


        comment.setStatus(DefaultConstants.toInspect);
        comment.setLikeCount(DefaultConstants.ZERO);
        comment.setDelFlag(DefaultConstants.DEFAULT_DELFLAG);

        int inserted = commentMapper.insert(comment);
        if (inserted == 0) {
            throw new RuntimeException("新增失败");
        }
        return comment.getId();
    }

    @Override
    public boolean update(Integer id,CommentDTO commentdto) {
        Comment old = commentMapper.selectById(id);
        BeanUtils.copyProperties(commentdto, old);
        int updated = commentMapper.updateById(old);
        if (updated == 0) {
            throw new RuntimeException("更新失败");
        }
        return true;
    }

    @Override
    public void setStatus(Integer id,String status) {

        Comment one = commentMapper.selectById(id);

        LambdaUpdateWrapper<Comment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Comment::getId, id);
        updateWrapper.set(Comment::getStatus,status);

        int rows = commentMapper.update(one, updateWrapper);
        if (rows == 0) {
            throw new BusinessException("更新失败");
        }
    }

}

