package com.github.memos.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.github.memos.config.VersionConfig;
import com.github.memos.dto.ResultVO;
import com.github.memos.dto.request.*;
import com.github.memos.dto.response.*
import com.github.memos.service.impl.*;
import com.github.memos.utils.DateUtils;
import com.github.memos.utils.MessagesUtils;
import com.github.memos.utils.StringHelper;
import com.github.memos.utils.SysPlatLog;

import java.util.List;

/**
 * @ClassName: MemosAdController
 * @Author:
 * @Date: 2020/5/14 8:59 上午
 * @Version: 1.0
 **/
@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/t")
public class MemosAdController {

    @Autowired
    private CnAreaService areaService;

    @Resource
    HttpServletResponse response;

    @Autowired
    private MessagesUtils messagesUtils;

    @SysPlatLog(operateName = "查询中国行政区划信息")
    @RequestMapping(value = "/area", method = {RequestMethod.GET})
    public @ResponseBody
    Object getCnArea(@RequestParam(required = false) Long code, Integer mod) {
        //限制层级
        if (mod == null || mod < 1 || mod > 2) {
            mod = 2;
        }

        if (code == null) {
            code = 100000L;
        } else if (code < 100000 || code > 999999999999L) {
            return ResultVO.error(HttpStatus.NOT_FOUND.value(), "code err", String.valueOf(code));
        }

        RespCnArea area = areaService.getArea(code, mod);
        //没查出来
        if (area == null) {
            return ResultVO.success(code);
        }
        return area;
    }

    @SysPlatLog(operateName = "查询中国行政区划信息", logNote = "根据code查询")
    @RequestMapping(value = "/area/{code:\\w+}", method = {RequestMethod.GET})
    public @ResponseBody
    Object getCnAreaByCode(@PathVariable Long code, Integer mod) {
        if (mod == null || mod < 1) {
            mod = 4;
        } else if (mod > 5) {
            mod = 5;
        }
        if (code < 100000 || code > 999999999999L) {
            return ResultVO.error(HttpStatus.NOT_FOUND.value(), "code err", String.valueOf(code));
        }
        RespCnAria area = areaService.getAreaByCode(code, mod);
        //没查出来
        if (area == null) {
            return ResultVO.success(code);
        }
        return area;
    }

    @ModelAttribute
    public void addResponse() {
        response.addHeader("Server", "undertow/" + VersionConfig.UNDERTOW_V);
    }
}
