package com.crawler.persistence.mapper.init;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DatabasesMapper {

    List<String> traversalDatabases(String schema);

    void createDatabase();
}
