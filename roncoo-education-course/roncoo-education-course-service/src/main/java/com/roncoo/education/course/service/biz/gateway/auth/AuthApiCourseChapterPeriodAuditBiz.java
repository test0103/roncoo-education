package com.roncoo.education.course.service.biz.gateway.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.roncoo.education.course.common.bean.bo.auth.AuthCourseChapterPeriodAuditBO;
import com.roncoo.education.course.common.bean.bo.auth.AuthCourseChapterPeriodAuditDeleteBO;
import com.roncoo.education.course.common.bean.bo.auth.AuthCourseChapterPeriodAuditSaveBO;
import com.roncoo.education.course.common.bean.bo.auth.AuthCourseChapterPeriodAuditSortBO;
import com.roncoo.education.course.common.bean.bo.auth.AuthCourseChapterPeriodAuditUpdateBO;
import com.roncoo.education.course.common.bean.bo.auth.AuthCourseChapterPeriodAuditViewBO;
import com.roncoo.education.course.common.bean.dto.auth.AuthCourseChapterPeriodAuditDTO;
import com.roncoo.education.course.common.bean.dto.auth.AuthCourseChapterPeriodAuditListDTO;
import com.roncoo.education.course.common.bean.dto.auth.AuthCourseChapterPeriodAuditSaveDTO;
import com.roncoo.education.course.common.bean.dto.auth.AuthCourseChapterPeriodAuditViewDTO;
import com.roncoo.education.course.service.dao.CourseAuditDao;
import com.roncoo.education.course.service.dao.CourseChapterAuditDao;
import com.roncoo.education.course.service.dao.CourseChapterPeriodAuditDao;
import com.roncoo.education.course.service.dao.CourseChapterPeriodDao;
import com.roncoo.education.course.service.dao.impl.mapper.entity.CourseAudit;
import com.roncoo.education.course.service.dao.impl.mapper.entity.CourseChapterAudit;
import com.roncoo.education.course.service.dao.impl.mapper.entity.CourseChapterPeriod;
import com.roncoo.education.course.service.dao.impl.mapper.entity.CourseChapterPeriodAudit;
import com.roncoo.education.util.base.BaseBiz;
import com.roncoo.education.util.base.Result;
import com.roncoo.education.util.enums.AuditStatusEnum;
import com.roncoo.education.util.enums.IsDocEnum;
import com.roncoo.education.util.enums.IsVideoEnum;
import com.roncoo.education.util.enums.ResultEnum;
import com.roncoo.education.util.enums.StatusIdEnum;
import com.roncoo.education.util.tools.ArrayListUtil;
import com.roncoo.education.util.tools.BeanUtil;
import com.roncoo.education.util.tools.Constants;
import com.xiaoleilu.hutool.util.CollectionUtil;
import com.xiaoleilu.hutool.util.ObjectUtil;

/**
 * 课时信息-审核
 *
 * @author wujing
 */
@Component
public class AuthApiCourseChapterPeriodAuditBiz extends BaseBiz {

	@Autowired
	private CourseChapterPeriodDao periodDao;
	@Autowired
	private CourseAuditDao courseAuditDao;
	@Autowired
	private CourseChapterAuditDao chapterAuditDao;
	@Autowired
	private CourseChapterPeriodAuditDao periodAuditDao;

	/**
	 * 课时列出接口
	 * 
	 * @param authCourseChapterPeriodAuditBO
	 * @return
	 * @author wuyun
	 */
	public Result<AuthCourseChapterPeriodAuditListDTO> listByChapterId(AuthCourseChapterPeriodAuditBO authCourseChapterPeriodAuditBO) {
		if (authCourseChapterPeriodAuditBO.getChapterId() == null) {
			return Result.error("章节ID不能为空");
		}

		CourseChapterAudit courseChapter = chapterAuditDao.getById(authCourseChapterPeriodAuditBO.getChapterId());
		if (ObjectUtil.isNull(courseChapter)) {
			return Result.error("找不到章节信息");
		}

		// 根据章节ID查询课时审核信息表
		List<CourseChapterPeriodAudit> periodAuditList = periodAuditDao.listByChapterIdAndStatusId(authCourseChapterPeriodAuditBO.getChapterId(), StatusIdEnum.YES.getCode());
		AuthCourseChapterPeriodAuditListDTO dto = new AuthCourseChapterPeriodAuditListDTO();
		if (CollectionUtil.isNotEmpty(periodAuditList)) {
			List<AuthCourseChapterPeriodAuditDTO> periodAuditDTOList = ArrayListUtil.copy(periodAuditList, AuthCourseChapterPeriodAuditDTO.class);
			dto.setUserCourseChapterPeriodAuditListDTO(periodAuditDTOList);
			for (CourseChapterPeriodAudit courseChapterPeriodAudit : periodAuditList) {
				// 根据课时ID、是否存在视频(1存在，0否)查询课时审核信息
				List<CourseChapterPeriodAudit> isVideoList = periodAuditDao.listByIsVideoAndPeriodId(courseChapterPeriodAudit.getIsVideo(), courseChapterPeriodAudit.getId());
				if (courseChapterPeriodAudit.getIsVideo().equals(IsVideoEnum.YES.getCode())) {
					dto.setVideoNum(isVideoList.size());
				}
				// 根据课时ID、是否存在文档(1存在，0否)查询课时审核信息
				List<CourseChapterPeriodAudit> isDocList = periodAuditDao.listByIsDocAndPeriodId(courseChapterPeriodAudit.getIsDoc(), courseChapterPeriodAudit.getId());
				if (courseChapterPeriodAudit.getIsDoc().equals(IsDocEnum.YES.getCode())) {
					dto.setDocNum(isDocList.size());
				}
			}
		}
		return Result.success(dto);
	}

	/**
	 * 课时查看接口
	 * 
	 * @param authCourseChapterPeriodAuditViewBO
	 * @return
	 * @author wuyun
	 */
	public Result<AuthCourseChapterPeriodAuditViewDTO> view(AuthCourseChapterPeriodAuditViewBO authCourseChapterPeriodAuditViewBO) {

		if (authCourseChapterPeriodAuditViewBO.getId() == null) {
			return Result.error("课时ID不能为空");
		}
		CourseChapterPeriodAudit periodAudit = periodAuditDao.getById(authCourseChapterPeriodAuditViewBO.getId());
		if (ObjectUtil.isNull(periodAudit)) {
			return Result.error("找不到课时信息");
		}
		AuthCourseChapterPeriodAuditViewDTO dto = BeanUtil.copyProperties(periodAudit, AuthCourseChapterPeriodAuditViewDTO.class);
		return Result.success(dto);
	}

	/**
	 * 课时删除接口
	 * 
	 * @param authCourseChapterPeriodAuditDeleteBO
	 * @return
	 * @author wuyun
	 */
	@Transactional
	public Result<Integer> delete(AuthCourseChapterPeriodAuditDeleteBO authCourseChapterPeriodAuditDeleteBO) {
		if (authCourseChapterPeriodAuditDeleteBO.getId() == null) {
			return Result.error("课时ID不能为空");
		}
		if (authCourseChapterPeriodAuditDeleteBO.getUserNo() == null) {
			return Result.error("userNo不能为空");
		}

		CourseChapterPeriodAudit periodAudit = periodAuditDao.getById(authCourseChapterPeriodAuditDeleteBO.getId());
		if (ObjectUtil.isNull(periodAudit)) {
			return Result.error("找不到课时信息");
		}
		CourseAudit course = courseAuditDao.getById(periodAudit.getCourseId());
		if (ObjectUtil.isNull(course)) {
			return Result.error("找不到课程信息");
		}
		if (!authCourseChapterPeriodAuditDeleteBO.getUserNo().equals(course.getLecturerUserNo())) {
			return Result.error("传入的useNo与该课程的讲师useNo不一致");
		}

		// 更新课时接口状态为已冻结，设置为未审核
		periodAudit.setAuditStatus(AuditStatusEnum.WAIT.getCode());
		periodAudit.setStatusId(Constants.FREEZE);
		int result = periodAuditDao.updateById(periodAudit);
		if (result > 0) {
			// 更新章节信息审核表状态为未审核
			CourseChapterAudit chapterAudit = new CourseChapterAudit();
			chapterAudit.setAuditStatus(AuditStatusEnum.WAIT.getCode());
			chapterAuditDao.updateById(chapterAudit);
			// 更新课程信息审核表状态为未审核
			CourseAudit courseAudit = new CourseAudit();
			courseAudit.setAuditStatus(AuditStatusEnum.WAIT.getCode());
			courseAuditDao.updateById(courseAudit);
			return Result.success(result);
		}
		return Result.error(ResultEnum.COURSE_DELETE_FAIL);
	}

	/**
	 * 课时添加接口
	 * 
	 * @param bo
	 * @return
	 * @author wuyun
	 */
	@Transactional
	public Result<AuthCourseChapterPeriodAuditSaveDTO> save(AuthCourseChapterPeriodAuditSaveBO authCourseChapterPeriodAuditSaveBO) {
		if (authCourseChapterPeriodAuditSaveBO.getChapterId() == null) {
			return Result.error("chapterId不能为空");
		}
		if (StringUtils.isEmpty(authCourseChapterPeriodAuditSaveBO.getPeriodName())) {
			return Result.error("课时名称不能为空");
		}
		if (authCourseChapterPeriodAuditSaveBO.getIsFree() == null) {
			return Result.error("isFree不能为空");
		}
		if (authCourseChapterPeriodAuditSaveBO.getUserNo() == null) {
			return Result.error("userNo不能为空");
		}

		CourseChapterAudit chapterAudit = chapterAuditDao.getById(authCourseChapterPeriodAuditSaveBO.getChapterId());
		if (ObjectUtils.isEmpty(chapterAudit)) {
			return Result.error("找不到章节信息");
		}
		CourseAudit course = courseAuditDao.getById(chapterAudit.getCourseId());
		if (ObjectUtil.isNull(course)) {
			return Result.error("找不到课程信息");
		}
		if (!authCourseChapterPeriodAuditSaveBO.getUserNo().equals(course.getLecturerUserNo())) {
			return Result.error("传入的useNo与该课程的讲师useNo不一致");
		}

		CourseChapterPeriodAudit record = BeanUtil.copyProperties(authCourseChapterPeriodAuditSaveBO, CourseChapterPeriodAudit.class);
		// 设置剩余的数据
		record.setCourseId(chapterAudit.getCourseId());
		record.setAuditStatus(AuditStatusEnum.WAIT.getCode());
		// 新增成功
		if (periodAuditDao.save(record) > 0) {
			// 更新课程审核表、章节审核表的审核状态为待审核
			chapterAuditDao.updateAuditStatusByChapterNo(AuditStatusEnum.WAIT.getCode(), record.getChapterId());
			courseAuditDao.updateAuditStatusBycourseId(AuditStatusEnum.WAIT.getCode(), record.getCourseId());
			// 再复制回dto进行返回
			AuthCourseChapterPeriodAuditSaveDTO dto = BeanUtil.copyProperties(record, AuthCourseChapterPeriodAuditSaveDTO.class);
			return Result.success(dto);
		}
		return Result.error(ResultEnum.COURSE_SAVE_FAIL);
	}

	/**
	 * 课时更新接口
	 * 
	 * @param bo
	 * @return
	 * @author wuyun
	 */
	@Transactional
	public Result<Integer> update(AuthCourseChapterPeriodAuditUpdateBO authCourseChapterPeriodAuditUpdateBO) {
		if (authCourseChapterPeriodAuditUpdateBO.getId() == null) {
			return Result.error("课时ID不能为空");
		}
		if (StringUtils.isEmpty(authCourseChapterPeriodAuditUpdateBO.getPeriodName())) {
			return Result.error("课时名称不能为空");
		}
		if (authCourseChapterPeriodAuditUpdateBO.getIsFree() == null) {
			return Result.error("isFree不能为空");
		}
		if (authCourseChapterPeriodAuditUpdateBO.getUserNo() == null) {
			return Result.error("userNo不能为空");
		}

		CourseChapterPeriodAudit periodAudit = periodAuditDao.getById(authCourseChapterPeriodAuditUpdateBO.getId());
		if (ObjectUtil.isNull(periodAudit)) {
			return Result.error("找不到课时信息");
		}
		CourseAudit course = courseAuditDao.getById(periodAudit.getCourseId());
		if (ObjectUtil.isNull(course)) {
			return Result.error("找不到课程信息");
		}
		if (!authCourseChapterPeriodAuditUpdateBO.getUserNo().equals(course.getLecturerUserNo())) {
			return Result.error("传入的useNo与该课程的讲师useNo不一致");
		}

		CourseChapterPeriodAudit courseChapterPeriodAudit = BeanUtil.copyProperties(authCourseChapterPeriodAuditUpdateBO, CourseChapterPeriodAudit.class);
		courseChapterPeriodAudit.setAuditStatus(AuditStatusEnum.WAIT.getCode());
		int result = periodAuditDao.updateById(courseChapterPeriodAudit);
		if (result > 0) {
			courseAuditDao.updateAuditStatusBycourseId(AuditStatusEnum.WAIT.getCode(), periodAudit.getCourseId());
			chapterAuditDao.updateAuditStatusByChapterNo(AuditStatusEnum.WAIT.getCode(), periodAudit.getChapterId());
			return Result.success(result);
		}
		return Result.error(ResultEnum.COURSE_UPDATE_FAIL);
	}

	/**
	 * 更新课时排序接口
	 * 
	 * @param bo
	 * @return
	 * @author wuyun
	 */
	@Transactional
	public Result<Integer> sort(AuthCourseChapterPeriodAuditSortBO authCourseChapterPeriodAuditSortBO) {
		if (CollectionUtil.isEmpty(authCourseChapterPeriodAuditSortBO.getPeriodIdList())) {
			return Result.error("课时信息不能为空");
		}
		if (authCourseChapterPeriodAuditSortBO.getUserNo() == null) {
			return Result.error("userNo不能为空");
		}

		if (CollectionUtil.isNotEmpty(authCourseChapterPeriodAuditSortBO.getPeriodIdList())) {
			int i = 1;
			for (Long periodId : authCourseChapterPeriodAuditSortBO.getPeriodIdList()) {
				// 更新课时审核信息表的排序
				periodAuditDao.updateSortByPeriodId(i++, periodId);
				CourseChapterPeriodAudit periodAudit = periodAuditDao.getById(periodId);
				CourseChapterPeriod period = periodDao.getById(periodId);
				CourseAudit course = courseAuditDao.getById(periodAudit.getCourseId());
				if (ObjectUtil.isNull(course)) {
					return Result.error("找不到课程信息");
				}
				if (!authCourseChapterPeriodAuditSortBO.getUserNo().equals(course.getLecturerUserNo())) {
					return Result.error("传入的useNo与该课程的讲师useNo不一致");
				}
				// 更新课时信息集合
				if (ObjectUtil.isNotNull(period)) {
					CourseChapterPeriod record = new CourseChapterPeriod();
					record.setId(period.getId());
					record.setSort(periodAudit.getSort());
					periodDao.updateById(record);
				}
			}
			return Result.success(i);
		}
		return Result.error("设置排序失败");
	}
}
