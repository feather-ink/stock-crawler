package com.crawler.service.init.Impl.create_database;

import com.crawler.persistence.mapper.init.create_database.CreateTimeTableMapper;
import com.crawler.service.init.Impl.CreateDatabaseService;
import com.crawler.service.init.Impl.InitDatabasesImpl;
import com.crawler.service.init.InitTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateTimeTableImpl implements CreateDatabaseService {

    private final String schema = "timetable";

    @Autowired
    private CreateTimeTableMapper createTable;

    @Autowired
    private InitTable initTable;

    public CreateTimeTableImpl(InitDatabasesImpl initDatabases) {
        initDatabases.attachBaseTable(this);
    }

    @Override
    public void createDatabase(List<String> databases) {
        if (!databases.contains(schema)){
            createTable.createDatabase();
            try {
                initTable.initTable();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
