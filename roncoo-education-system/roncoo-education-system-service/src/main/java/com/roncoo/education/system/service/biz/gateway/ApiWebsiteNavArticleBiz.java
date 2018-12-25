package com.roncoo.education.system.service.biz.gateway;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.roncoo.education.system.common.bean.bo.WebsiteNavArticleBO;
import com.roncoo.education.system.common.bean.dto.WebsiteNavArticleDTO;
import com.roncoo.education.system.service.dao.WebsiteNavArticleDao;
import com.roncoo.education.system.service.dao.impl.mapper.entity.WebsiteNavArticle;
import com.roncoo.education.util.base.Result;
import com.roncoo.education.util.enums.StatusIdEnum;
import com.roncoo.education.util.tools.BeanUtil;
import com.xiaoleilu.hutool.util.ObjectUtil;

/**
 * 站点导航文章
 *
 * @author wuyun
 */
@Component
public class ApiWebsiteNavArticleBiz {

	@Autowired
	private WebsiteNavArticleDao dao;

	public Result<WebsiteNavArticleDTO> get(WebsiteNavArticleBO bo) {
		if (StringUtils.isEmpty(bo.getNavId())) {
			return Result.error("navId不能为空");
		}
		WebsiteNavArticle websiteNavArticle = dao.getByNavIdAndStatusId(bo.getNavId(), StatusIdEnum.YES.getCode());
		if (ObjectUtil.isNull(websiteNavArticle)) {
			return Result.error("没有找到站点导航文章信息");
		}
		return Result.success(BeanUtil.copyProperties(websiteNavArticle, WebsiteNavArticleDTO.class));
	}
}
