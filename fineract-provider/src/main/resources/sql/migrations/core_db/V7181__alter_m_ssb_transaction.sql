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
-- Created 31/05/2023 ,time 1153
-- Created By Sinatra Gunda @treyviis@gmail.com

ALTER TABLE `m_ssb_transaction`
	ADD COLUMN `client_id` BIGINT NULL DEFAULT NULL AFTER `ssb_transaction_record_id`,
	ADD COLUMN `amount` DOUBLE(20,2) NULL DEFAULT NULL AFTER `client_id`;


