package com.hniu.service.imp;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hniu.entity.Readers;
import com.hniu.entity.vo.PageVo;
import com.hniu.entity.vo.ReaderVo;
import com.hniu.mapper.ReadersMapper;
import com.hniu.service.ReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class ReaderServiceImpl implements ReaderService {


    @Autowired
    private ReadersMapper readersMapper;

    @Override
    public int deleteByPrimaryKey(Integer rId) {

        return readersMapper.deleteByPrimaryKey(rId);
    }

    @Override
    public synchronized int insert(Readers record) {
		if(StringUtils.isEmpty(record.getReaderName())) {
			return 0;
		}
		if(StringUtils.isEmpty(record.getWechat())) {
			return 0;
		}
		if(StringUtils.isEmpty(record.getReaderTypeId())) {
			return 0;
		}

		record.setOverdueNumber(Byte.parseByte("0"));
		Calendar calendar = Calendar.getInstance();
		Date date= calendar.getTime();
		record.setCreateTime(date);

		String yearStr = String.valueOf(calendar.getWeekYear()).substring(2);
		String rcode = readersMapper.selectRcode();
		if(StringUtils.isEmpty(rcode)){
			rcode = "00000000";
		}
		rcode= rcode.substring(2);
		Pattern pattern= Pattern.compile("[1-9]\\d*$");
		Matcher matcher = pattern.matcher(rcode);
		if(matcher.find()){
			int teg=Integer.parseInt(matcher.group())+1;
			String flag = String.valueOf(teg);
			while(flag.length()<8) {
				flag = "0"+flag;
			}
			record.setReaderCode(yearStr + flag);
		}else {
			record.setReaderCode(yearStr + "00000001");
		}

		calendar.set(calendar.YEAR, calendar.getWeekYear()+1);
		date = calendar.getTime();
		record.setExpirationTime(date);


		return readersMapper.insert(record);
    }

    @Override
    public ReaderVo selectByPrimaryKey(Integer rId) {
        return readersMapper.selectByPrimaryKey(rId);
    }

    @Override
    public void selectAll(PageVo<ReaderVo> pageVo) {
        PageHelper.startPage(pageVo.getPage(),pageVo.getRows());
        List<ReaderVo> list = readersMapper.selectAll();
        PageInfo<ReaderVo> pageInfo = new PageInfo<>(list);
        pageVo.setTotal(pageInfo.getTotal());
        pageVo.setDateList(list);
    }

    @Override
    public int updateByPrimaryKey(Readers record) {

        return readersMapper.updateByPrimaryKey(record);
    }

    @Override
    public void selectByShearch(PageVo<ReaderVo> pageVo) {
        PageHelper.startPage(pageVo.getPage(),pageVo.getRows());
        List<ReaderVo> list = readersMapper.selectByShearch(pageVo);
        PageInfo<ReaderVo> pageInfo = new PageInfo<>(list);
        pageVo.setTotal(pageInfo.getTotal());
        pageVo.setDateList(list);
    }


    @Override
    public List<Map<String,Integer>> selectSexCount() {
        return readersMapper.selectSexCount();
    }


    @Override
    public ReaderVo selectByRCode(String readerCode) {
        return readersMapper.selectByRCode(readerCode);
    }

    @Override
    public int updateOverdue(Integer rId) {
        return readersMapper.updateOverdue(rId);
    }

    @Override
    public List<ReaderVo> selectByOther(Readers reader) {
        return readersMapper.selectByOther(reader);
    }


}