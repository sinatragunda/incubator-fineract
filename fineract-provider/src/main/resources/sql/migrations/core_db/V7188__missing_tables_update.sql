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
-- Created 13/06/2023 ,time 1023
-- Updated By Sinatra Gunda @treyviis@gmail.com



CREATE TABLE IF NOT EXISTS `m_charge_properties` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`commissioned_charge` SMALLINT(5) NULL DEFAULT '0',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Table used for storing extra charge settings .Not ideal in the future to keep adding columns to charge table so best create new '
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
AUTO_INCREMENT=3
;

CREATE TABLE IF NOT EXISTS `m_payment_rule` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`name` VARCHAR(200) NOT NULL DEFAULT '0' COLLATE 'latin1_swedish_ci',
	`office_id` BIGINT(20) NOT NULL DEFAULT '0',
	`active` SMALLINT(5) NULL DEFAULT '0',
	`payment_direction` SMALLINT(5) NULL DEFAULT '0',
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Payment Rule Table'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;

CREATE TABLE IF NOT EXISTS `m_savings_product_properties` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`payment_rule_id` BIGINT(20) NULL DEFAULT '0',
	`savings_product_id` INT(11) NULL DEFAULT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COMMENT='Links to main product table where its reference using a fk '
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
;
