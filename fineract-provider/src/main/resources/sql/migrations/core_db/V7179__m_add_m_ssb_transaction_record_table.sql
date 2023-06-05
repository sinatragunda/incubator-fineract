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
-- Created 30/05/2023 ,time 1225
-- Created By Sinatra Gunda @treyviis@gmail.com

CREATE TABLE IF NOT EXISTS `m_ssb_transaction_record` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`filename` VARCHAR(200) NOT NULL DEFAULT '0',
	`created_by` BIGINT NOT NULL DEFAULT 0,
	PRIMARY KEY (`id`)
)
COMMENT='Keeps track of the filename and created by user \r\nThis is main record within it a foreign reference is made to ssb_transaction \r\nTo be added creation time in the future \r\n'
COLLATE='latin1_swedish_ci'
;

