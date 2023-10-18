package com.alibaba.csp.sentinel.dashboard.controller.v2;

import com.alibaba.csp.sentinel.dashboard.discovery.AppInfo;
import com.alibaba.csp.sentinel.dashboard.discovery.AppManagement;
import com.alibaba.csp.sentinel.dashboard.discovery.MachineInfo;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.domain.vo.MachineInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
@RestController
@RequestMapping(value = "/sentinel-service/app")
public class AppControllerV2 {
    @Autowired
    private AppManagement appManagement;

    @GetMapping("/briefinfos")
    public Result<List<AppInfo>> queryAppInfos(HttpServletRequest request) {
        List<AppInfo> list = new ArrayList<>(appManagement.getBriefApps());
        Collections.sort(list, Comparator.comparing(AppInfo::getApp));
        return Result.ofSuccess(list);
    }
    @GetMapping(value = "/{app}/machines")
    public Result<List<MachineInfoVo>> getMachinesByApp(@PathVariable("app") String app) {
        AppInfo appInfo = appManagement.getDetailApp(app);
        if (appInfo == null) {
            return Result.ofSuccess(null);
        }
        List<MachineInfo> list = new ArrayList<>(appInfo.getMachines());
        Collections.sort(list, Comparator.comparing(MachineInfo::getApp).thenComparing(MachineInfo::getIp).thenComparingInt(MachineInfo::getPort));
        return Result.ofSuccess(MachineInfoVo.fromMachineInfoList(list));
    }
}
