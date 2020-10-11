package com.crawler.service.init.Impl.create_database;


import com.crawler.persistence.mapper.init.create_database.CreateStockDataMapper;
import com.crawler.service.init.Impl.CreateDatabaseService;
import com.crawler.service.init.Impl.InitDatabasesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CreateStockDataImpl implements CreateDatabaseService {

    private final String schema = "stock_data";

    @Autowired
    private CreateStockDataMapper createTable;

    public CreateStockDataImpl(InitDatabasesImpl initDatabases) {
        initDatabases.attachCascadeTable(this);
    }

    @Override
    public void createDatabase(List<String> databases) {
        if (!databases.contains(schema))
            createTable.createDatabase();
    }
}
