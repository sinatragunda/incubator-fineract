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
-- Created 04/01/2021 12:33am

CREATE TABLE `m_loan_commission_charge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL ,
	`is_active` SMALLINT(5) DEFAULT '1',
	`charge_calculation_type` INT(5) NOT NULL,
	`charge_time_type` INT(5) NOT NULL,
	`charge_applies_to` INT(5) NOT NULL,
	`amount` DECIMAL NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
COMMENT="This should just contain the charges only.This is tracking since when updated if no deposit been made it will refer to new value"
;
