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
-- Created 06/01/2021 12:33am

CREATE TABLE IF NOT EXISTS `m_attached_commission_charges` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`is_deposited` SMALLINT(5) DEFAULT '1',
	`loan_from_agent_id` BIGINT(20) NOT NULL,
	`loan_commission_charge_id` BIGINT(5) NOT NULL,
	`amount` DECIMAL(20,3) NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB
COMMENT="This contains charges that have been attached to loans ,links loan to charge";