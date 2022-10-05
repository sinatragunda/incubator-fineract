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
-- Created by Sinatra Gunda  on 05/10/2022

INSERT INTO `stretchy_report` VALUES (null, 'Aging Loans By Product Portfolio', 'Table', NULL, 'Loan', 'SELECT \r\nmpl.short_name AS "ProductName",\r\nifnull(sum(ml.principal_amount),0) AS "Principal",\r\nifnull(sum(mla.interest_overdue_derived),0) AS "InterestAged",\r\nifnull(sum(mla.principal_overdue_derived),0) AS "PrincipalAged"\r\nFROM \r\nm_loan ml \r\nJOIN m_client mc ON mc.id = ml.client_id\r\nJOIN m_product_loan mpl ON mpl.id = ml.product_id\r\nleft JOIN m_loan_arrears_aging mla ON mla.loan_id = ml.id\r\nWHERE YEAR(ml.disbursedon_date) = ${year} AND mc.office_id = ${officeId}\r\nGROUP BY ml.product_id', 'Aging Loans By Product Portfolio ,in chart form', 0, 0, 0, 1);


-- Insert loan report parameter
INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name)
VALUES (
    (
      SELECT sr.id
      FROM stretchy_report sr
      WHERE sr.report_name = 'Aging Loans By Product Portfolio'
    ),
    (
      SELECT sp.id
      FROM stretchy_parameter sp
      WHERE sp.parameter_variable = 'year'
    ),
    'year'
  );


INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='Aging Loans By Product Portfolio'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');
