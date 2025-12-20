package com.pblog.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pblog.common.entity.AcRelation;
import com.pblog.common.vo.AcRelationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ACRelationMapper extends BaseMapper<AcRelation> {

    List<AcRelationVO> selectAcRelationVOByArticleIds(@Param("articleIds") List<Integer> articleIds);

    int insertByCategoryIds(@Param("articleId") Integer articleId,
                            @Param("categoryIds") List<Integer> categoryIds);
}
