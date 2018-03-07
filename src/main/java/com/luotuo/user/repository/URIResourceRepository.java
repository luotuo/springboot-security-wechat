package com.luotuo.user.repository;

import com.luotuo.user.entity.URIResource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Table;

/**
 * Created by luotuo on 17-6-29.
 */
@Repository
@Table(name = "uri_resource")
@Qualifier("uriResourceRepository")
public interface URIResourceRepository extends JpaRepository<URIResource, Long> {
}
