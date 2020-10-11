package com.crawler.persistence.mapper.init.create_database;

import com.crawler.persistence.mapper.init.DatabasesMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface CreateCalendarTableMapper extends DatabasesMapper {

    @Override
    void createDatabase();
}
