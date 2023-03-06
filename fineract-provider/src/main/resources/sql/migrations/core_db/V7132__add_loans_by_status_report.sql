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
-- Created 23/02/2023 ,time 1411


INSERT INTO `stretchy_parameter` (`id`, `parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) 
VALUES (null, 'loanStatus', 'loanStatusId', 'Loan', 'select', 'number', '-1', NULL, NULL, 'Y', 'SELECT -1 id,\r\n"All" STATUS\r\nUNION\r\nSELECT 100 id ,\r\n"Submitted and Pending Loans" status\r\nUNION \r\nSELECT 200 id,\r\n"Approved Loans" status \r\nUNION \r\nSELECT 300 id,\r\n"Active" status\r\nUNION \r\nSELECT 400 id,\r\n"Withdrawn Loans" STATUS\r\nUNION \r\nSELECT 500 id,\r\n"Rejected" STATUS\r\nUNION \r\nSELECT 600 id,\r\n"Closed with Full Repayment" STATUS\r\nUNION \r\nSELECT 601 id,\r\n"Closed Written Off" STATUS\r\nUNION \r\nSELECT 602 id,\r\n"Closed With Outstanding Amount" STATUS\r\nUNION \r\nSELECT 700 id,\r\n"OverPaid" status', 5);




INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) 
VALUES ('Loans By Status', 'Pentaho', NULL, 'Loan', NULL, 'Loans By Status Report', 0, 1);


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='loanProductId'), 
  'loanProductId');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='loanOfficerId'), 
  'Loan Officer');
  

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
  'Branch');
  
  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='currencyId'), 
  'CurrencyId');
  
 
  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='startDate'), 
  'fromDate');
  

  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='endDate'), 
  'toDate');


  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans By Status'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='loanStatusId'), 
  'loanStatusId');
