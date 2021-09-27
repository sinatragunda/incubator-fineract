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
-- Created 26/09/2021 

CREATE TABLE `m_depreciation_product` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL COLLATE 'latin1_swedish_ci',
	`short_name` VARCHAR(200) NOT NULL COLLATE 'latin1_swedish_ci',
	`description` VARCHAR(200) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`currency_code` VARCHAR(50) NOT NULL COLLATE 'latin1_swedish_ci',
	`tag` VARCHAR(50) NOT NULL COLLATE 'latin1_swedish_ci',
	`external_id` VARCHAR(50) NULL DEFAULT NULL COLLATE 'latin1_swedish_ci',
	`salvage_value` DOUBLE NOT NULL DEFAULT '0',
	`asset_class_id` BIGINT(20) NULL DEFAULT NULL,
	`is_appreciating_asset` SMALLINT(5) NOT NULL DEFAULT '0',
	`is_zero_salvaged_asset` SMALLINT(5) NOT NULL DEFAULT '0',
	`rate_of_decay` DOUBLE NULL DEFAULT NULL,
	`min_rate_of_decay` DOUBLE NULL DEFAULT NULL,
	`max_rate_of_decay` DOUBLE NULL DEFAULT NULL,
	`usefull_life` INT(20) NULL DEFAULT NULL,
	`min_usefull_life` INT(20) NULL DEFAULT NULL,
	`max_usefull_life` INT(20) NULL DEFAULT NULL,
	`usefull_life_timer` INT(5) NULL DEFAULT NULL,
	`depreciation_method` INT(5) NULL DEFAULT '0',
	`accounting_rule` INT(5) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`) USING BTREE,
	UNIQUE INDEX `short_name` (`short_name`) USING BTREE,
	UNIQUE INDEX `tag` (`tag`) USING BTREE,
	UNIQUE INDEX `external_id` (`external_id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

