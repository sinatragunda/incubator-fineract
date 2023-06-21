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
-- Created 27/04/2023 0654

CREATE TABLE IF NOT EXISTS `m_wsscript` (
	`id` BIGINT NOT NULL AUTO_INCREMENT,
	`class_name` VARCHAR(200) NOT NULL,
	`method_name` VARCHAR(200) NOT NULL,
	`script_type` TINYINT(5) NOT NULL,
	`return_type` TINYINT(5) NOT NULL,
	`ws_script_container_id` BIGINT NOT NULL,
	PRIMARY KEY (`id`)
)
COMMENT='WsScript table contains the actual java function contained within the jar file'
COLLATE='latin1_swedish_ci'
;
