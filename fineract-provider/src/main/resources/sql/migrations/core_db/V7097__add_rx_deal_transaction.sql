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
-- Created 05/11/2022 0742 PM

CREATE TABLE IF NOT EXISTS `m_rx_deal_transaction` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`rx_deal_id` BIGINT(20) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;