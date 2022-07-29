package com.github.memos.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.github.memos.dao.*;
import com.github.memos.dto.response.*
import com.github.memos.pojo.CnArea;
import com.github.memos.utils.ListUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CnAreaService
 * @Author:
 * @Date: 2020/7/8 10:43 上午
 * @Version: 1.0
 **/
@Transactional
@Service
public class CnAreaService extends ServiceImpl<CnAreaDao, CnArea> {

    private static Map<Long, Integer> map;
    private CnAreaDao areaDao;

    @Autowired
    public CnAreaService(CnAreaDao areaDao) {
        this.areaDao = areaDao;
        if (map == null) {
            map = Maps.newHashMap();
        }
    }

    /**
     * @param code:
     * @param mod:
     * @MethodName: getArea
     * @Author:
     * @Date: 2020/7/8 3:48 下午
     * @Version: 1.0
     * @return: com.github.memos.dto.response.RespCnArea
     **/
    public RespCnArea getArea(long code, int mod) {
        //查数据库
        Wrapper<CnArea> wrapper = new QueryWrapper<CnArea>().eq("id", code);
        CnArea area = areaDao.selectOne(wrapper);
        RespCnArea two = null;
        if (area != null) {
            two = this.getRespArea(area);
            this.fillChildren(two, mod, 1);
        }
        return two;
    }

    /**
     * @param code:
     * @param mod:
     * @MethodName: getAreaByCode
     * @Author:
     * @Date: 2020/7/8 3:48 下午
     * @Version: 1.0
     * @return: com.github.memos.dto.response.RespCnArea
     **/
    public RespCnAria getAreaByCode(long code, int mod) {
        //查数据库
        Wrapper<CnArea> wrapper = new QueryWrapper<CnArea>().eq("id", code);
        CnArea area = areaDao.selectOne(wrapper);
        RespCnAria two = null;
        if (area != null) {
            two = this.getRespAria(area);
            this.fillUpper(two, mod, 1);
        }
        return two;
    }

    private void fillChildren(RespCnArea one, int mod, int i) {
        //查数据库
        Wrapper<CnArea> wrapper = new QueryWrapper<CnArea>().eq("parent", one.getId());
        List<CnArea> tmo = areaDao.selectList(wrapper);
        int j = i;
        if (ListUtil.isLstNotEmpty(tmo)) {
            List<RespCnArea> two = new ArrayList<>();
            tmo.forEach(area -> {
                two.add(this.getRespArea(area));
            });

            one.setChildren(two);

            //查根结点
            if (one.getId() == 100000L) {
                if (map.get(100000L) == null) {
                    var num = Long.valueOf(0);
                    wrapper = new QueryWrapper<>();
                    wrapper.ne("id", 100000L);
                    num = areaDao.selectCount(wrapper);
                    map.put(100000L, num.intValue());
                }
                one.setNum(map.get(100000L));
            }

            if (j++ < mod) {
                for (RespCnArea three : two) {
                    this.fillChildren(three, mod, j);
                }
            }
        }
    }

    private void fillUpper(RespCnAria one, int mod, int i) {
        int j = i;
        if (one.getParentId() != -1) {
            //查数据库
            CnArea tmo = areaDao.selectById(one.getParentId());
            RespCnAria two = this.getRespAria(tmo);

            one.setUpper(two);
            if (j++ < mod) {
                this.fillUpper(two, mod, j);
            }
        }
    }

    //converter
    private RespCnArea getRespArea(CnArea one) {
        RespCnArea area = new RespCnArea();
        BeanUtils.copyProperties(one, area);

        //不查叶子结点
        if (!"6".equals(area.getLevel()) && area.getId() != 100000L) {
            if (map.get(area.getId()) == null) {
                //查非根、叶子结点
                var num = Long.valueOf(0);
                QueryWrapper<CnArea> wrapper = new QueryWrapper<>();
                wrapper.likeRight("remark", area.getRemark() + "，");
                num = areaDao.selectCount(wrapper);
                map.put(area.getId(), num.intValue());
                area.setNum(num.intValue());
            }
            area.setNum(map.get(area.getId()));
        }

        return area;
    }

    //converter
    private RespCnAria getRespAria(CnArea one) {
        RespCnAria area = new RespCnAria();
        BeanUtils.copyProperties(one, area);
        if (one.getParent() != -1) {
            area.setParentId(one.getParent());
        }

        return area;
    }

}
