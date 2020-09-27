package org.jinyuanjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.jinyuanjava.litemall.db.dao.LitemallIssueMapper;
import org.jinyuanjava.litemall.db.domain.LitemallIssue;
import org.jinyuanjava.litemall.db.domain.LitemallIssueExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallIssueService {
    @Resource
    private LitemallIssueMapper issueMapper;

    public void deleteById(Integer id) {
        issueMapper.logicalDeleteByPrimaryKey(id);
    }

    public List<LitemallIssue> query() {
        LitemallIssueExample example = new LitemallIssueExample();
        example.or().andDeletedEqualTo(false);
        return issueMapper.selectByExample(example);
    }

    public void add(LitemallIssue issue) {
        issue.setAddTime(LocalDateTime.now());
        issue.setUpdateTime(LocalDateTime.now());
        issueMapper.insertSelective(issue);
    }

    public List<LitemallIssue> querySelective(String question, Integer page, Integer limit, String sort) {
        LitemallIssueExample example = new LitemallIssueExample();
        LitemallIssueExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(question)) {
            criteria.andQuestionLike("%" + question + "%");
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort)) {
            example.setOrderByClause(sort);
        }

        PageHelper.startPage(page, limit);
        return issueMapper.selectByExample(example);
    }

    public int updateById(LitemallIssue issue) {
        issue.setUpdateTime(LocalDateTime.now());
        return issueMapper.updateByPrimaryKeySelective(issue);
    }

    public LitemallIssue findById(Integer id) {
        return issueMapper.selectByPrimaryKey(id);
    }
}
