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
-- Created 13/03/2023 1018PM


CREATE TABLE `m_screen_object` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL DEFAULT '0',
	`office_id` BIGINT NOT NULL DEFAULT 0,
	`ref_table` SMALLINT(5) NOT NULL DEFAULT 0,
	`active` SMALLINT(5) NOT NULL DEFAULT 0,
	`parent_screen_object_id` BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`)
)
COMMENT='Screen Object ,main handler called from screen ui .Contains Screen Elements .'
COLLATE='latin1_swedish_ci'
;