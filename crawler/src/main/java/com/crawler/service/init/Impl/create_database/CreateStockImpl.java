package com.crawler.service.init.Impl.create_database;

import com.crawler.persistence.mapper.init.create_database.CreateStockMapper;
import com.crawler.service.init.Impl.CreateDatabaseService;
import com.crawler.service.init.Impl.InitDatabasesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateStockImpl implements CreateDatabaseService {

    private final String schema = "stock";

    @Autowired
    private CreateStockMapper createTable;

    public CreateStockImpl(InitDatabasesImpl initDatabases) {
        initDatabases.attachBaseTable(this);
    }

    @Override
    public void createDatabase(List<String> databases) {
        if (!databases.contains(schema))
            createTable.createDatabase();
    }
}
