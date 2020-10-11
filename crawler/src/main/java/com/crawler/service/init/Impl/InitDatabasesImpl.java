package com.crawler.service.init.Impl;

import com.crawler.persistence.mapper.init.DatabasesMapper;
import com.crawler.service.init.InitDatabases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InitDatabasesImpl implements InitDatabases {

    @Autowired
    private DatabasesMapper databasesMapper;

    @Autowired
    private DataSourceProperties dataSource;

    private List<CreateDatabaseService> baseTables = new ArrayList<>();
    private List<CreateDatabaseService> cascadeTables = new ArrayList<>();

    //初始化数据库表
    @Override
    public void initDatabases(String schema){
        //查询数据库中已有的表
        List<String> databases = databasesMapper.traversalDatabases(schema);
        //调用观察者，判断自己的库表是否创建，如果没有则创建
        // 创建基础数据库
        this.createBaseTable(databases);
        // 创建级联数据库
        this.createCascadeTable(databases);
    }

    public void attachBaseTable(CreateDatabaseService createDatabase) {
        baseTables.add(createDatabase);
    }

    private void createBaseTable(List<String> databases) {
        for (CreateDatabaseService baseTable : baseTables) {
            baseTable.createDatabase(databases);
        }
    }

    public void attachCascadeTable(CreateDatabaseService createDatabase) {
        cascadeTables.add(createDatabase);
    }

    private void createCascadeTable(List<String> databases) {
        for (CreateDatabaseService cascadeTable : cascadeTables) {
            cascadeTable.createDatabase(databases);
        }
    }


}
