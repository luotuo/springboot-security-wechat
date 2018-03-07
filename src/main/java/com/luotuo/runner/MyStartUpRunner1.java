package com.luotuo.runner;

import com.luotuo.service.URIResourceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by luotuo on 17-6-29.
 */
@Component
public class MyStartUpRunner1 implements CommandLineRunner {

    @Autowired
    private URIResourceService uriResourceService;

    @Autowired
    ApplicationContext applicationContext;

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>>>>>>>>>>>>>>服务启动执行，执行更新URI操作<<<<<<<<<<<<<");
        UpdateURIResource();
    }

    private void UpdateURIResource() {
        List<String> uris = getAllRequestMappingInfo();
        List<String> allURIs = uriResourceService.getAll();
        for (String uri: uris ) {
            if (!allURIs.contains(uri))
                uriResourceService.save(uri);
        }
    }

    private List<String> getAllRequestMappingInfo() {
        AbstractHandlerMethodMapping<RequestMappingInfo> objHandlerMethodMapping = (AbstractHandlerMethodMapping<RequestMappingInfo>)applicationContext.getBean("requestMappingHandlerMapping");
        Map<RequestMappingInfo, HandlerMethod> mapRet = objHandlerMethodMapping.getHandlerMethods();
        List<String> res = new ArrayList<String>();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : mapRet.entrySet()) {
            String uri = entry.getKey().toString().replace("{", "").replace("[", "").replace("}","").replace("]","");
            String []temp = uri.split(",");
            res.add(temp[0]);
        }
        return res;
    }
}
