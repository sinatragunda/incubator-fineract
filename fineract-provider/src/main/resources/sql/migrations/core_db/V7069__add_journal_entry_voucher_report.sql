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

-- Added 14/08/2022 at 1626

INSERT INTO `stretchy_parameter` VALUES (null, 'userId', 'userId', 'User Id', 'text', 'number', 'n/a', NULL, NULL, 'Y', null, NULL);


INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Journal Entry Voucher', 'Pentaho', NULL, 'Loans', NULL,"A copy of payment voucher report but only for journal entries not loans as well", 0, 1);

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Journal Entry Voucher'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='transactionId'), 
  'transactionId');
  

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Journal Entry Voucher'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_name='userId'), 
  'userId');