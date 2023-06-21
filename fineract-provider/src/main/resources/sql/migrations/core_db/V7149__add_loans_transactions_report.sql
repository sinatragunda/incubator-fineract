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
-- Created 29/03/2023 ,time 0617

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) 
VALUES ('Loans Transactions Dated', 'Pentaho', NULL, 'Loan', NULL, 'Loans Transactions Dated Report', 0, 1);


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans Transactions Dated'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='loanProductId'), 
  'loanProductId');


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans Transactions Dated'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='tag'), 
  'tag');
  

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans Transactions Dated'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
  'Branch');
  
  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans Transactions Dated'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='currencyId'), 
  'CurrencyId');
  
 
  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans Transactions Dated'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='startDate'), 
  'fromDate');
  

  INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES
 ((select sr.id from stretchy_report sr where sr.report_name='Loans Transactions Dated'), 
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='endDate'), 
  'toDate');