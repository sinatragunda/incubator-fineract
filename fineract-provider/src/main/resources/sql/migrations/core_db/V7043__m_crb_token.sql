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

-- Created 15/03/2022 04:12am

CREATE TABLE `m_crb_token` (
	`expires_after` VARCHAR(50) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`token` VARCHAR(250) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`status` VARCHAR(50) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
COMMENT="This is a dummy table used for testing crb project .Will be deleted straight after";
