package com.jwcq.service;

import com.jwcq.user.repository.URIResourceRepository;
import com.jwcq.user.entity.URIResource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by luotuo on 17-6-29.
 */

@Service
@Transactional("secondTransactionManager")
public class URIResourceService {

    @Autowired
    private URIResourceRepository uriResourceRepository;

    public URIResource save(String uriStr) {
        URIResource uri = new URIResource();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(0, 1, sort);
        Iterable<URIResource> uriResource = uriResourceRepository.findAll(pageRequest);
        for (URIResource u: uriResource) {
            uri.setId(u.getId() + 1);
        }
        uri.setUri(uriStr);
        return uriResourceRepository.save(uri);
    }

    public List<String> getAll() {
        List<URIResource> uriResources = uriResourceRepository.findAll();
        List<String> uriStrs = new ArrayList<String>();
        for (URIResource uri: uriResources) {
            uriStrs.add(uri.getUri());
        }
        return uriStrs;
    }

    public List<String> getAllForWeb() {
        List<URIResource> uriResources = uriResourceRepository.findAll();
        List<String> uriStrs = new ArrayList<String>();
        for (URIResource uri: uriResources) {
            if (uri.getUri().contains("/error"))
                continue;
            uriStrs.add(uri.getUri());
        }
        uriStrs.sort(new Comparator<String>() {//排序
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return uriStrs;
    }
}
