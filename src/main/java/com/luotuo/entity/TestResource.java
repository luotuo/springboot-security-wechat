package com.luotuo.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "test_resource")
public class TestResource extends BaseResource {
}
