/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.dataqueries.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.apache.fineract.infrastructure.core.domain.JdbcSupport;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.dataqueries.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class HybridEntityReadPlatformServiceImpl implements HybridEntityReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final HybridTableMapper hybridTableMapper = new HybridTableMapper();

    @Autowired
    public HybridEntityReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(this.dataSource);

    }

    @Override
    public Collection<HybridEntityTableData> retrieveOneByRefId(final Long id) {

        final String sql = String.format("%s where h.ref_id = ? " ,hybridTableMapper.schema());

        final Collection<HybridEntityTableData> hybridEntityDataCollection = this.jdbcTemplate.query(sql ,hybridTableMapper ,new Object[]{id});

        return hybridEntityDataCollection;
    }

    private static final class HybridTableMapper implements RowMapper<HybridEntityTableData> {

        public String schema() {
            return " select h.id as id, h.ref_id as refId ,h.application_record_id as applicationRecordId from m_hybrid_entity_table h";
        }

        @Override
        public HybridEntityTableData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final Long id = JdbcSupport.getLong(rs, "id");
            final Long refId = JdbcSupport.getLong(rs, "refId");
            final Long applicationRecordId = JdbcSupport.getLong(rs ,"applicationRecordId");

            return new HybridEntityTableData(id ,refId ,applicationRecordId);
        }
    }


}