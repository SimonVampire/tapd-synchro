package com.ext.tapd.tapd.controller;

import com.ext.tapd.tapd.common.status.SPriorityEnum;
import com.ext.tapd.tapd.dao.*;
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
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/storie")
public class StorieController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private StoryRepository storyRepository;
    @Autowired
    private IterationRepository iterationRepository;
    @Autowired
    private StoryCategoriesRepository storyCategoriesRepository;
    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private StatusMapRepository statusMapRepository;

    @Value("${workspace.ids}")
    private String ids;

    //初始化task
    @RequestMapping(value = "/initStorie", method = RequestMethod.GET)
    public String initStorie() {

        String[] idsStr = ids.split(",");
        for (String workspaceId : idsStr) {
            String url = "https://api.tapd.cn/stories?workspace_id=" + workspaceId;
            //在请求头信息中携带Basic认证信息(这里才是实际Basic认证传递用户名密码的方式)
            HttpHeaders headers = new HttpHeaders();
            headers.set("authorization", "Basic " + Base64.getEncoder().encodeToString("XFzFJy1k:1BF133BB-0B17-E7C1-A04A-067C761B353C".getBytes()));
            int count = getCount(workspaceId);
            int totalPage = 0;
            if (count > 200) {
                totalPage = (count / 200) + 1;
                for (int i = 1; i <= totalPage; i++) {
                    url = url + "&limit=200&page=" + i;
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
                            String gsonStr = g.toJson(map.get("Story"));
                            System.out.println(gsonStr);
                            Story story = g.fromJson(gsonStr, Story.class);
//                            story.setStatus(StatusEnum.getValue(story.getStatus()));
                            story.setPriority(SPriorityEnum.getValue(story.getPriority()));
                            Optional<Iteration> iteration = iterationRepository.findById(story.getIteration_id());
                            if (iteration.isPresent()) {
                                story.setIteration_name(iteration.get().getName());
                            }
                            Optional<StoryCategories> categories = storyCategoriesRepository.findById(story.getCategory_id());
                            if (categories.isPresent()) {
                                story.setCategory_name(categories.get().getName());
                            }
                            Optional<Workspace> workspace = workspaceRepository.findById(story.getWorkspace_id());
                            if (workspace.isPresent()) {
                                story.setWorkspace_name(workspace.get().getName());
                            }
                            StatusMap statusMap = statusMapRepository.findByCodeAndSystemAndWorkspaceId(story.getStatus(), "story", story.getWorkspace_id());
                            if (Objects.nonNull(statusMap)) {
                                story.setStatus(statusMap.getName());
                            }
                            storyRepository.save(story);
                        }
                    }
                }
            } else {
                url = url + "&limit=200";
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
                        String gsonStr = g.toJson(map.get("Story"));
                        System.out.println(gsonStr);
                        Story story = g.fromJson(gsonStr, Story.class);
//                        story.setStatus(StatusEnum.getValue(story.getStatus()));
                        story.setPriority(SPriorityEnum.getValue(story.getPriority()));
                        Optional<Iteration> iteration = iterationRepository.findById(story.getIteration_id());
                        if (iteration.isPresent()) {
                            story.setIteration_name(iteration.get().getName());
                        }
                        Optional<StoryCategories> categories = storyCategoriesRepository.findById(story.getCategory_id());
                        if (categories.isPresent()) {
                            story.setCategory_name(categories.get().getName());
                        }
                        Optional<Workspace> workspace = workspaceRepository.findById(story.getWorkspace_id());
                        if (workspace.isPresent()) {
                            story.setWorkspace_name(workspace.get().getName());
                        }
                        StatusMap statusMap = statusMapRepository.findByCodeAndSystemAndWorkspaceId(story.getStatus(), "story", story.getWorkspace_id());
                        if (Objects.nonNull(statusMap)) {
                            story.setStatus(statusMap.getName());
                        }
                        storyRepository.save(story);
                    }
                }
            }
        }
        return "执行成功";
    }

    private int getCount(final String workspaceId) {
        String url = "https://api.tapd.cn/stories/count?workspace_id=" + workspaceId;
        //在请求头信息中携带Basic认证信息(这里才是实际Basic认证传递用户名密码的方式)
        HttpHeaders headers = new HttpHeaders();
        headers.set("authorization", "Basic " + Base64.getEncoder().encodeToString("XFzFJy1k:1BF133BB-0B17-E7C1-A04A-067C761B353C".getBytes()));
        //发送请求
        HttpEntity<String> ans = restTemplate.exchange(url, HttpMethod.GET,   //GET请求
                new HttpEntity<>(null, headers),   //加入headers
                String.class);  //body响应数据接收类型
        System.out.println(ans);
        String gson = ans.getBody();
        System.out.println(gson);
        Gson g = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss")
                .create();
        ResultCountEntity vo = g.fromJson(gson, ResultCountEntity.class);
        Map map = vo.getData();
        int count = new Double((Double) map.get("count")).intValue();
        return count;
    }
}
