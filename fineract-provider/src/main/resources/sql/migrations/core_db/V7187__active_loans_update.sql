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
-- Created 13/06/2023 ,time 0124
-- Updated By Sinatra Gunda @treyviis@gmail.com


UPDATE `stretchy_report` SET  `report_sql`='select concat(repeat("..",   \r\n   ((LENGTH(ounder.`hierarchy`) - LENGTH(REPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) as "Office/Branch",\r\nifnull(cur.display_symbol, l.currency_code) as Currency,\r\nlo.display_name as "Loan Officer", \r\nc.display_name as "Client", \r\nc.tag as "Employee Number", \r\nc.external_id AS "NRC Number",\r\nl.account_no as "Loan Account No.", pl.`name` as "Product", \r\nf.`name` as Fund,  \r\nl.principal_amount as "Loan Amount", \r\nIFNULL(lrc.principal_amount+lrc.interest_amount ,0) AS "EMI",\r\nl.annual_nominal_interest_rate as " Annual Nominal Interest Rate", \r\ndate(l.disbursedon_date) as "Disbursed Date", \r\ndate(l.expected_maturedon_date) as "Expected Matured On",\r\n\r\nl.principal_repaid_derived as "Principal Repaid",\r\nl.principal_outstanding_derived as "Principal Outstanding",\r\nlaa.principal_overdue_derived as "Principal Overdue",\r\n\r\nl.interest_repaid_derived as "Interest Repaid",\r\nl.interest_outstanding_derived as "Interest Outstanding",\r\nlaa.interest_overdue_derived as "Interest Overdue",\r\n\r\nl.fee_charges_repaid_derived as "Fees Repaid",\r\nl.fee_charges_outstanding_derived  as "Fees Outstanding",\r\nlaa.fee_charges_overdue_derived as "Fees Overdue",\r\n\r\nl.penalty_charges_repaid_derived as "Penalties Repaid",\r\nl.penalty_charges_outstanding_derived as "Penalties Outstanding",\r\npenalty_charges_overdue_derived as "Penalties Overdue"\r\n\r\nfrom m_office o \r\njoin m_office ounder on ounder.hierarchy like concat(o.hierarchy, \'%\')\r\nand ounder.hierarchy like concat(\'${currentUserHierarchy}\', \'%\')\r\njoin m_client c on c.office_id = ounder.id\r\njoin m_loan l on l.client_id = c.id\r\njoin m_product_loan pl on pl.id = l.product_id\r\nleft join m_staff lo on lo.id = l.loan_officer_id\r\nleft join m_currency cur on cur.code = l.currency_code\r\nleft join m_fund f on f.id = l.fund_id\r\nleft join m_loan_arrears_aging laa on laa.loan_id = l.id\r\nleft JOIN m_loan_repayment_schedule lrc ON lrc.loan_id = l.id \r\nwhere o.id = ${officeId}\r\nand (l.currency_code = "${currencyId}" or "-1" = "${currencyId}")\r\nand (l.product_id = "${loanProductId}" or "-1" = "${loanProductId}")\r\nand (ifnull(l.loan_officer_id, -10) = "${loanOfficerId}" or "-1" = "${loanOfficerId}")\r\nand (ifnull(l.fund_id, -10) = ${fundId} or -1 = ${fundId})\r\nand (ifnull(l.loanpurpose_cv_id, -10) = ${loanPurposeId} or -1 = ${loanPurposeId})\r\nand l.loan_status_id = 300\r\ngroup by l.id\r\norder by ounder.hierarchy, l.currency_code, c.account_no, l.account_no' WHERE `report_name`='Active Loans - Details';

