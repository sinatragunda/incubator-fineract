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
-- Created by Honest C. on 12/09/2022
--
-- Create parameter
INSERT INTO `stretchy_parameter` (
    `parameter_name`,
    `parameter_variable`,
    `parameter_label`,
    `parameter_displayType`,
    `parameter_FormatType`,
    `parameter_default`,
    `special`,
    `selectOne`,
    `selectAll`,
    `parameter_sql`,
    `parent_id`
  )
VALUES (
    'selectYear',
    'year',
    'Enter Year',
    'text',
    'number',
    2022,
    NULL,
    NULL,
    NULL,
    NULL,
    NULL
  );

-- Create report for loans
INSERT INTO `stretchy_report`
VALUES (
    NULL,
    'LoansActivityReport',
    'Table',
    '',
    'Loan',
    'SELECT  
      id AS "ID",
      ml.account_no AS "Account Number",
      IFNULL(ml.principal_amount, 0) AS "Principal",
      ml.loan_status_id AS "Status",
      ml.submittedon_date AS "Submitted On",
      ml.disbursedon_date AS "Disbursed On",
      ml.closedon_date AS "Closed On",
      ml.maturedon_date AS "Matured On",
      ml.rejectedon_date AS "Rejected On",
      ml.writtenoffon_date AS "Written Off On",
      ml.withdrawnon_date AS "Withdrawn On"
    FROM m_loan ml 
    WHERE YEAR(ml.submittedon_date) = ${year}',
    'Retrieve all loans in a year',
    0,
    1,
    1
  );

-- Insert loan report parameter
INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name)
VALUES (
    (
      SELECT sr.id
      FROM stretchy_report sr
      WHERE sr.report_name = 'LoansActivityReport'
    ),
    (
      SELECT sp.id
      FROM stretchy_parameter sp
      WHERE sp.parameter_variable = 'year'
    ),
    'year'
  );

-- Create report for clients
INSERT INTO `stretchy_report`
VALUES (
    NULL,
    'ClientsActivityReport',
    'Table',
    '',
    'Client',
    'SELECT  
      id AS "ID",
      mc.account_no AS "Account Number",
      mc.display_name AS "Name",
      mc.status_enum AS "Status",
      mc.activation_date AS "Activated On",
      mc.submittedon_date AS "Submitted On"
    FROM m_client mc 
    WHERE YEAR(mc.submittedon_date) = ${year} AND mc.is_staff = 0',
    'Retrieve all new clients in a year',
    0,
    1,
    1
  );

-- Insert client report parameter
INSERT INTO stretchy_report_parameter (report_id, parameter_id, report_parameter_name)
VALUES (
    (
      SELECT sr.id
      FROM stretchy_report sr
      WHERE sr.report_name = 'ClientsActivityReport'
    ),
    (
      SELECT sp.id
      FROM stretchy_parameter sp
      WHERE sp.parameter_variable = 'year'
    ),
    'year'
  );