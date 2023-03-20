--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
-- Created 08/03/2023 ,time 0849


CREATE TABLE `m_hybrid_entity_table` (
	`id` BIGINT NOT NULL DEFAULT 0,
	`ref_table` INT NOT NULL DEFAULT 0,
	`x_registered_table_id` BIGINT NOT NULL DEFAULT 0,
	`application_record_id` BIGINT NOT NULL DEFAULT 0,
	`ref_id` BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`)
)
COMMENT='This tables links application record data to tables like loan ,savings ,client etc .A ref id is the fk linking to those tables \r\n'
COLLATE='latin1_swedish_ci'
;