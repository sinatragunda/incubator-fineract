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
-- Added by Sinatra Gunda
-- Created 13/11/2022 2018

INSERT INTO `stretchy_report` VALUES (null ,'Withdrawals Today', 'Table', '', 'Savings', 'SELECT
mo.name AS Branch,
mst.transaction_date AS Date, 
mst.id AS Id, 
mc.display_name AS CLIENT,
mst.amount AS Amount
FROM 
m_savings_account_transaction mst 
LEFT JOIN m_savings_account ms ON ms.id = mst.savings_account_id
LEFT JOIN m_client mc ON mc.id = ms.client_id
LEFT JOIN m_office mo ON mo.id = mst.office_id
WHERE mst.transaction_date = CURDATE()
AND mst.transaction_type_enum = 2
AND mst.office_id =${officeId}
','Total deposits today', 0, 1 ,1 ,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='Withdrawals Today'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');