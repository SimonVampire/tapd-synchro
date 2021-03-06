package com.ext.tapd.tapd.controller;

import com.ext.tapd.tapd.common.status.PriorityEnum;
import com.ext.tapd.tapd.common.status.ResolutionEnum;
import com.ext.tapd.tapd.common.status.SeverityEnum;
import com.ext.tapd.tapd.dao.BugRepository;
import com.ext.tapd.tapd.dao.IterationRepository;
import com.ext.tapd.tapd.dao.WorkspaceRepository;
import com.ext.tapd.tapd.pojo.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/workspace")
public class WorkSpaceController {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Value("${company.id}")
    private String companyId;


    //初始化task
    @RequestMapping(value = "/initWorkspace", method = RequestMethod.GET)
    public String initWorkspace() {
        String url = "https://api.tapd.cn/workspaces/projects?company_id=" + companyId;
        //在请求头信息中携带Basic认证信息(这里才是实际Basic认证传递用户名密码的方式)
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Basic " + Base64.getEncoder().encodeToString("XFzFJy1k:1BF133BB-0B17-E7C1-A04A-067C761B353C".getBytes()));
        //发送请求
        HttpEntity<String> ans = restTemplate.exchange(url, HttpMethod.GET,   //GET请求
                new HttpEntity<>(null, headers),   //加入headers
                String.class);  //body响应数据接收类型
        String gson = ans.getBody();
        Gson g = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        ResultEntity vo = g.fromJson(gson, ResultEntity.class);
        if (vo.getData().size() > 0) {
            for (LinkedTreeMap map : vo.getData()) {
                String gsonStr = g.toJson(map.get("Workspace"));
                System.out.println(gsonStr);
                Workspace bug = g.fromJson(gsonStr, Workspace.class);
                workspaceRepository.save(bug);
            }

        }
        return "执行成功";
    }
}
