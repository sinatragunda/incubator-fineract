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
-- Created by Sinatra Gunda (treyviis@gmail.com)
-- Created 09/12/2022 0231PM

CREATE TABLE `m_local_ref` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL COLLATE 'latin1_swedish_ci',
	`description` VARCHAR(200) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`office_id` BIGINT(20) NOT NULL,
	`ref_table` SMALLINT(5) NOT NULL,
	`ref_value_type` SMALLINT(5) NOT NULL,
	`permission_id` BIGINT(20) NULL DEFAULT '0',
	`submitted_date` DATE NOT NULL DEFAULT '0000-00-00',
	`code_id` BIGINT(20) NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Local References .An implementation of T24 Local Reference functionality'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;