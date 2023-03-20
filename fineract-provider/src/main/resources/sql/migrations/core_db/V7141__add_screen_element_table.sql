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
-- Created 13/03/2023 1027PM

CREATE TABLE `m_screen_element` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL DEFAULT '0',
	`model_name` VARCHAR(100) NOT NULL DEFAULT '0',
	`display_name` VARCHAR(100) NOT NULL DEFAULT '0',
	`comparison_type` SMALLINT(5) NOT NULL DEFAULT 0,
	`gate` SMALLINT(5) NOT NULL DEFAULT 0,
	`show_on_ui` SMALLINT NOT NULL DEFAULT 0,
	`mandatory` SMALLINT NOT NULL DEFAULT 0,
	`value` VARCHAR(200) NOT NULL DEFAULT '0',
	`element_type` SMALLINT NOT NULL DEFAULT 0,
	`screen_object_id` BIGINT NOT NULL DEFAULT 0,
	`screen_element_parent_id` BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`)
)
COMMENT='Screen Element ,the element itself .Contains reference to its subvalues in the form of screen element parent id column'
COLLATE='latin1_swedish_ci'
;
