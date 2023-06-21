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
-- Created 05/04/2022 0249 by Sinatra Gunda (treyviis@gmail.com)

ALTER TABLE `m_charge_tier`
	CHANGE COLUMN `upper_limit` `min_tier` DOUBLE(20,2) NOT NULL DEFAULT 0 AFTER `amount`,
	ADD COLUMN `max_tier` DOUBLE(20,2) NOT NULL DEFAULT 0 AFTER `min_tier`,
	ADD COLUMN `overlapping` SMALLINT(5) NOT NULL DEFAULT 0 AFTER `max_tier`,
	ADD COLUMN `tier_type` SMALLINT(5) NOT NULL DEFAULT 0 AFTER `overlapping`;