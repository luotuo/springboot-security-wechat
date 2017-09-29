package com.jwcq.user.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by luotuo on 17-6-29.
 */
@Entity
@Table(name = "department")
@Data
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long pid;
    private String name;
    private int level;
}
