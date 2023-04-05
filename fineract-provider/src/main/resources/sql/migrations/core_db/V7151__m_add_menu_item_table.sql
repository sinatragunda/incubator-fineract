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

-- Created 30/03/2023 1902 by Sinatra Gunda

CREATE TABLE `m_menu_item` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(50) NOT NULL,
	`ref_table` SMALLINT NOT NULL,
	`application_action` SMALLINT NOT NULL,
	`shortcut` VARCHAR(50) NULL DEFAULT '',
	PRIMARY KEY (`id`),
	UNIQUE INDEX `shortcut` (`shortcut`)
)
COMMENT='Represents a single menu item ,that will fit into part of a menu .To be referenced in menuTable'
COLLATE='latin1_swedish_ci'
;

