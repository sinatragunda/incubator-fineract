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
--

ALTER TABLE `m_share_product` DROP COLUMN `property_type`;
ALTER TABLE `m_share_product` DROP COLUMN `monthly_deposit`;

ALTER TABLE `m_share_product` ADD COLUMN `property_type` int(5) DEFAULT 2 ;
ALTER TABLE `m_share_product` ADD COLUMN `monthly_deposit` decimal(20,3) DEFAULT 0 ;
