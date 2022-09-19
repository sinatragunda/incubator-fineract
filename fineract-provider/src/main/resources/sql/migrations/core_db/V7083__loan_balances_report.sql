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
-- Created by Honest C. on 14/09/2022
--
-- Create report for loan balances
INSERT INTO `stretchy_report`
VALUES (
    NULL,
    'LoanBalancesReport',
    'Table',
    '',
    'Loan',
    'SELECT
      mo.name AS "Office",
      COUNT(ml.id) AS "Total Loans",
      COUNT(DISTINCT mc.id) AS "Total Clients",
      SUM(ml.principal_disbursed_derived) AS "Principal Disbursed",
      SUM(ml.principal_repaid_derived) AS "Principal Repaid",
      SUM(ml.principal_outstanding_derived) AS "Principal Outstanding",
      SUM(ml.principal_writtenoff_derived) AS "Principal Written Off",
      SUM(ml.interest_charged_derived) AS "Interest Charged",
      SUM(ml.interest_repaid_derived) AS "Interest Repaid",
      SUM(ml.interest_outstanding_derived) AS "Interest Outstanding",
      SUM(ml.interest_waived_derived) AS "Interest Waived",
      SUM(ml.interest_writtenoff_derived) AS "Interest Written Off",
      SUM(ml.fee_charges_charged_derived) AS "Fees Charged",
      SUM(ml.fee_charges_repaid_derived) AS "Fees Repaid",
      SUM(ml.fee_charges_outstanding_derived) AS "Fees Outstanding",
      SUM(ml.fee_charges_waived_derived) AS "Fees Waived",
      SUM(ml.fee_charges_writtenoff_derived) AS "Fees Written Off",
      SUM(ml.penalty_charges_charged_derived) AS "Penalty Charged",
      SUM(ml.penalty_charges_repaid_derived) AS "Penalty Repaid",
      SUM(ml.penalty_charges_outstanding_derived) AS "Penalty Outstanding",
      SUM(ml.penalty_charges_waived_derived) AS "Penalty Waived",
      SUM(ml.penalty_charges_writtenoff_derived) AS "Penalty Written Off"
    FROM m_loan ml
    INNER JOIN m_client mc
    ON mc.id = ml.client_id
    INNER JOIN m_office mo
    ON mo.id = mc.office_id
    GROUP BY mo.id',
    'Retrieve all loan balances for all offices',
    0,
    1,
    1
  );