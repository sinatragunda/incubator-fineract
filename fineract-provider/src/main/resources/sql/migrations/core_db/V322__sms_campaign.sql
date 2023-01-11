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

CREATE TABLE `sms_campaign` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`campaign_name` VARCHAR(100) NOT NULL,
	`campaign_type` INT(11) NOT NULL,
	`campaign_trigger_type` INT(11) NOT NULL,
	`report_id` INT(11) NOT NULL,
	`provider_id` BIGINT(20) NOT NULL,
	`param_value` TEXT NULL,
	`status_enum` INT(11) NOT NULL,
	`message` TEXT NOT NULL,
	`submittedon_date` DATE NULL DEFAULT NULL,
	`submittedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`approvedon_date` DATE NULL DEFAULT NULL,
	`approvedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`closedon_date` DATE NULL DEFAULT NULL,
	`closedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`recurrence` VARCHAR(100) NULL DEFAULT NULL,
	`next_trigger_date` DATETIME NULL DEFAULT NULL,
	`last_trigger_date` DATETIME NULL DEFAULT NULL,
	`recurrence_start_date` DATETIME NULL DEFAULT NULL,
	`is_visible` TINYINT(1) NULL DEFAULT '1',
	PRIMARY KEY (`id`),
	INDEX `report_id` (`report_id`),
	CONSTRAINT `sms_campaign_ibfk_1` FOREIGN KEY (`report_id`) REFERENCES `stretchy_report` (`id`)
);


ALTER TABLE `sms_messages_outbound`
	ADD COLUMN `campaign_id` BIGINT(20) NOT NULL,
	ADD COLUMN `external_id` VARCHAR(100) NULL DEFAULT NULL,
	ADD COLUMN `submittedon_date` DATE NULL DEFAULT NULL,
	ADD COLUMN `delivered_on_date` DATETIME NULL DEFAULT NULL,
	ADD INDEX `FKCAMPAIGN00000001` (`campaign_id`),
	ADD CONSTRAINT `FKCAMPAIGN00000001` FOREIGN KEY (`campaign_id`) REFERENCES `sms_campaign` (`id`);


INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES 
('Update SMS Outbound with Campaign Message', 'Update SMS Outbound with Campaign Message', '0 0 5 1/1 * ? *', NOW(), 3, NULL, NULL, NULL, 'Update SMS Outbound with Campaign Message1 _ DEFAULT', NULL, 1, 0, 1, 4, 0), 
('Send Messages to SMS Gateway', 'Send Messages to SMS Gateway', '0 0 5 1/1 * ? *', NOW(), 2, NULL, NULL, NULL, 'Send Messages to SMS Gateway1 _ DEFAULT', NULL, 1, 0, 1, 4, 0), 
('Get Delivery Reports from SMS Gateway', 'Get Delivery Reports from SMS Gateway', '0 0 5 1/1 * ? *', NOW(), 1, NULL, NULL, NULL, 'Get Delivery Reports from SMS Gateway1 _ DEFAULT', NULL, 1, 0, 1, 4, 0);

INSERT INTO  `m_permission` 
(`grouping` ,`code` ,`entity_name`, `action_name`, `can_maker_checker`) VALUES 
('organisation', 'READ_SMSCAMPAIGN', 'SMSCAMPAIGN', 'READ', '0'),
('organisation', 'CREATE_SMSCAMPAIGN', 'SMSCAMPAIGN', 'CREATE', '0'),
('organisation', 'CREATE_SMSCAMPAIGN_CHECKER', 'SMSCAMPAIGN', 'CREATE', '0'),
('organisation', 'UPDATE_SMSCAMPAIGN', 'SMSCAMPAIGN',  'UPDATE', '0'),
('organisation', 'UPDATE_SMSCAMPAIGN_CHECKER', 'SMSCAMPAIGN', 'UPDATE', '0'),
('organisation', 'DELETE_SMSCAMPAIGN', 'SMSCAMPAIGN', 'DELETE', '0'),
('organisation', 'DELETE_SMSCAMPAIGN_CHECKER', 'SMSCAMPAIGN',  'DELETE', '0'),
('organisation', 'ACTIVATE_SMSCAMPAIGN', 'SMSCAMPAIGN', 'ACTIVATE', '0'),
('organisation', 'CLOSE_SMSCAMPAIGN', 'SMSCAMPAIGN', 'CLOSE', '0'),
('organisation', 'REACTIVATE_SMSCAMPAIGN', 'SMSCAMPAIGN', 'REACTIVATE', '0');




INSERT INTO `m_code_value` (`code_id`, `code_value`, `code_description`, `order_position`, `code_score`, `is_active`, `is_mandatory`) VALUES ((SELECT `id` FROM `m_code` mc WHERE mc.code_name = "GROUPROLE" AND is_system_defined), 'Leader', 'Group Leader Role', 1, NULL, 1, 0);




INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('cycleXSelect', 'cycleX', 'Cycle X Number', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL), 
('cycleYSelect', 'cycleY', 'Cycle Y Number', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL), 
('fromXSelect', 'fromX', 'From X Number', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL), 
('toYSelect', 'toY', 'To Y Number', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL), 
('overdueXSelect', 'overdueX', 'Overdue X Number', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL), 
('overdueYSelect', 'overdueY', 'Overdue Y Number', 'text', 'number', 'n/a', NULL, NULL, NULL, NULL, NULL);



INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Active Clients', 'SMS', 'NonTriggered', 'Clients', 'SELECT c.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\no.id AS "officeNumber"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nWHERE o.id = ${officeId} AND c.status_enum = 300 AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId})\r\nGROUP BY c.id\r\nORDER BY ounder.hierarchy, c.account_no', 'All clients with the status ‘Active’', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Prospective Clients', 'SMS', 'NonTriggered', 'Clients', 'SELECT c.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\no.id AS "officeNumber"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nLEFT JOIN m_loan l ON l.client_id = c.id\r\nWHERE o.id = ${officeId} AND c.status_enum = 300 AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND l.client_id IS NULL\r\nGROUP BY c.id\r\nORDER BY ounder.hierarchy, c.account_no', 'All clients with the status ‘Active’ who have never had a loan before', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Active Loan Clients', 'SMS', 'NonTriggered', 'Clients', 'SELECT \r\nc.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", \r\nl.principal_amount AS "loanAmount", \r\n(IFNULL(l.principal_outstanding_derived, 0) + IFNULL(l.interest_outstanding_derived, 0) + IFNULL(l.fee_charges_outstanding_derived, 0) + IFNULL(l.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nl.principal_disbursed_derived AS "loanDisbursed",\r\nounder.id AS "officeNumber", \r\nl.account_no AS "loanAccountId", \r\ngua.lastname AS "guarantorLastName", COUNT(gua.id) AS "numberOfGuarantors",\r\ng.display_name AS "groupName"\r\n\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_loan l ON l.client_id = c.id\r\nJOIN m_product_loan pl ON pl.id = l.product_id\r\nLEFT JOIN m_group_client gc ON gc.client_id = c.id\r\nLEFT JOIN m_group g ON g.id = gc.group_id\r\nLEFT JOIN m_staff lo ON lo.id = l.loan_officer_id\r\nLEFT JOIN m_currency cur ON cur.code = l.currency_code\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = l.id\r\nWHERE o.id = ${officeId} AND (IFNULL(l.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND l.loan_status_id = 300 AND (DATEDIFF(CURDATE(), l.disbursedon_date) BETWEEN ${cycleX} AND ${cycleY})\r\nGROUP BY l.id\r\nORDER BY ounder.hierarchy, l.currency_code, c.account_no, l.account_no', 'All clients with an outstanding loan between cycleX and cycleY days', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan in arrears', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\nmc.id AS "id", \r\nmc.firstname AS "firstName",\r\nmc.middlename AS "middleName",\r\nmc.lastname AS "lastName",\r\nmc.display_name AS "fullName",\r\nmc.mobile_no AS "mobileNo", \r\nml.principal_amount AS "loanAmount", \r\n(IFNULL(ml.principal_outstanding_derived, 0) + IFNULL(ml.interest_outstanding_derived, 0) + IFNULL(ml.fee_charges_outstanding_derived, 0) + IFNULL(ml.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nml.principal_disbursed_derived AS "loanDisbursed",\r\nlaa.overdue_since_date_derived AS "paymentDueDate",\r\nIFNULL(laa.total_overdue_derived, 0) AS "totalDue",\r\nounder.id AS "officeNumber", \r\nml.account_no AS "loanAccountId", \r\ngua.lastname AS "guarantorLastName", \r\nCOUNT(gua.id) AS "numberOfGuarantors",\r\ng.display_name AS "groupName"\r\n\r\nFROM m_office mo\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')\r\nINNER JOIN m_client mc ON mc.office_id=ounder.id\r\nINNER JOIN m_loan ml ON ml.client_id = mc.id\r\nINNER JOIN r_enum_value rev ON rev.enum_id=ml.loan_status_id AND rev.enum_name = \'loan_status_id\'\r\nINNER JOIN m_loan_arrears_aging laa ON laa.loan_id=ml.id\r\nLEFT JOIN m_currency cur ON cur.code = ml.currency_code\r\nLEFT JOIN m_group_client gc ON gc.client_id = mc.id\r\nLEFT JOIN m_group g ON g.id = gc.group_id\r\nLEFT JOIN m_staff lo ON lo.id = ml.loan_officer_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = ml.id\r\nWHERE ml.loan_status_id=300 AND mo.id=${officeId} AND (IFNULL(ml.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND (DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN ${fromX} AND ${toY})\r\nGROUP BY ml.id\r\nORDER BY ounder.hierarchy, ml.currency_code, mc.account_no, ml.account_no', 'All clients with an outstanding loan in arrears between fromX and toY days', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan payments due', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\ncl.id AS "id", \r\ncl.firstname AS "firstName",\r\ncl.middlename AS "middleName",\r\ncl.lastname AS "lastName",\r\ncl.display_name AS "fullName",\r\ncl.mobile_no AS "mobileNo", \r\nl.principal_amount AS "loanAmount",\r\nof.id AS "officeNumber",\r\n(IFNULL(l.principal_outstanding_derived, 0) + IFNULL(l.interest_outstanding_derived, 0) + IFNULL(l.fee_charges_outstanding_derived, 0) + IFNULL(l.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nl.principal_disbursed_derived AS "loanDisbursed",\r\nls.duedate AS "paymentDueDate",\r\n(IFNULL(SUM(ls.principal_amount),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0)\r\n + IFNULL(SUM(ls.interest_amount),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.interest_waived_derived),0)\r\n + IFNULL(SUM(ls.fee_charges_amount),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.fee_charges_waived_derived),0)\r\n + IFNULL(SUM(ls.penalty_charges_amount),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.penalty_charges_waived_derived),0)\r\n) AS "totalDue",\r\nlaa.total_overdue_derived AS "totalOverdue",\r\nl.account_no AS "loanAccountId",\r\ngua.lastname AS "guarantorLastName",\r\nCOUNT(gua.id) AS "numberOfGuarantors",\r\ngp.display_name AS "groupName"\r\n\r\nFROM m_office of\r\nLEFT JOIN m_client cl ON of.id = cl.office_id\r\nLEFT JOIN m_loan l ON cl.id = l.client_id\r\nLEFT JOIN m_group_client gc ON gc.client_id = cl.id\r\nLEFT JOIN m_group gp ON gp.id = l.group_id\r\nLEFT JOIN m_loan_repayment_schedule ls ON l.id = ls.loan_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = l.id\r\nINNER JOIN m_loan_arrears_aging laa ON laa.loan_id=l.id\r\nWHERE of.id = ${officeId} AND (IFNULL(l.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND (DATEDIFF(CURDATE(), ls.duedate) BETWEEN ${fromX} AND ${toY}) \r\nAND (of.hierarchy LIKE CONCAT((\r\nSELECT ino.hierarchy\r\nFROM m_office ino\r\nWHERE ino.id = ${officeId}),"%"))\r\nGROUP BY l.id\r\nORDER BY of.hierarchy, l.currency_code, cl.account_no, l.account_no', 'All clients with an unpaid installment due on their loan between fromX and toY days', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Dormant Prospects', 'SMS', 'NonTriggered', 'Clients', 'SELECT c.id AS "id", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo",  \r\no.id AS "officeNumber", \r\nTIMESTAMPDIFF(MONTH, c.activation_date, CURDATE()) AS "dormant"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nLEFT JOIN m_loan l ON l.client_id = c.id\r\nWHERE o.id = ${officeId} AND c.status_enum = 300 AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND l.client_id IS NULL AND (TIMESTAMPDIFF(MONTH, c.activation_date, CURDATE()) > 3)\r\nGROUP BY c.id\r\nORDER BY ounder.hierarchy, c.account_no', 'All individuals who have not yet received a loan but were also entered into the system more than 3 months', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Active group leaders', 'SMS', 'NonTriggered', 'Clients', 'SELECT c.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\no.id AS "officeNumber"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_group g ON g.office_id = ounder.id\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN m_group_client gc ON gc.group_id = g.id AND gc.client_id = c.id\r\nLEFT JOIN m_group_roles gr ON gr.group_id = g.id AND gr.client_id = c.id\r\nLEFT JOIN m_staff ms ON ms.id = c.staff_id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nLEFT JOIN m_code_value cv ON cv.id = gr.role_cv_id\r\nLEFT JOIN m_code code ON code.id = cv.code_id\r\nWHERE o.id = ${officeId} AND g.status_enum = 300 AND c.status_enum = 300  AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND code.code_name = \'GROUPROLE\' AND cv.code_value = \'Leader\'\r\nGROUP BY c.id\r\nORDER BY ounder.hierarchy, c.account_no', 'All active group chairmen', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan payments due (Overdue Loans)', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\nmc.id AS "id", \r\nmc.firstname AS "firstName",\r\nmc.middlename AS "middleName",\r\nmc.lastname AS "lastName",\r\nmc.display_name AS "fullName",\r\nmc.mobile_no AS "mobileNo", \r\nml.principal_amount AS "loanAmount", \r\n(IFNULL(ml.principal_outstanding_derived, 0) + IFNULL(ml.interest_outstanding_derived, 0) + IFNULL(ml.fee_charges_outstanding_derived, 0) + IFNULL(ml.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nml.principal_disbursed_derived AS "loanDisbursed",\r\nlaa.overdue_since_date_derived AS "paymentDueDate",\r\n(IFNULL(SUM(ls.principal_amount),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0)\r\n + IFNULL(SUM(ls.interest_amount),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.interest_waived_derived),0)\r\n + IFNULL(SUM(ls.fee_charges_amount),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.fee_charges_waived_derived),0)\r\n + IFNULL(SUM(ls.penalty_charges_amount),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.penalty_charges_waived_derived),0)\r\n) AS "totalDue",\r\nlaa.total_overdue_derived AS "totalOverdue",\r\nounder.id AS "officeNumber", \r\nml.account_no AS "loanAccountId", \r\ngua.lastname AS "guarantorLastName", \r\nCOUNT(gua.id) AS "numberOfGuarantors",\r\ng.display_name AS "groupName"\r\n\r\nFROM m_office mo\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')\r\nINNER JOIN m_client mc ON mc.office_id=ounder.id\r\nINNER JOIN m_loan ml ON ml.client_id = mc.id\r\nINNER JOIN r_enum_value rev ON rev.enum_id=ml.loan_status_id AND rev.enum_name = \'loan_status_id\'\r\nINNER JOIN m_loan_arrears_aging laa ON laa.loan_id=ml.id\r\nLEFT JOIN m_loan_repayment_schedule ls ON ls.loan_id = ml.id\r\nLEFT JOIN m_currency cur ON cur.code = ml.currency_code\r\nLEFT JOIN m_group_client gc ON gc.client_id = mc.id\r\nLEFT JOIN m_group g ON g.id = gc.group_id\r\nLEFT JOIN m_staff lo ON lo.id = ml.loan_officer_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = ml.id\r\nWHERE ml.loan_status_id=300 AND mo.id=${officeId} AND (IFNULL(ml.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) \r\nAND (DATEDIFF(CURDATE(), ls.duedate) BETWEEN ${fromX} AND ${toY})\r\nAND (DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN ${overdueX} AND ${overdueY})\r\nGROUP BY ml.id\r\nORDER BY ounder.hierarchy, ml.currency_code, mc.account_no, ml.account_no', 'Loan Payments Due between fromX to toY days for clients in arrears between overdueX and overdueY days', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan payments received (Active Loans)', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\nmc.id AS "id", \r\nmc.firstname AS "firstName",\r\nmc.middlename AS "middleName",\r\nmc.lastname AS "lastName",\r\nmc.display_name AS "fullName",\r\nmc.mobile_no AS "mobileNo", \r\nml.principal_amount AS "loanAmount", \r\n(IFNULL(ml.principal_outstanding_derived, 0) + IFNULL(ml.interest_outstanding_derived, 0) + IFNULL(ml.fee_charges_outstanding_derived, 0) + IFNULL(ml.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nounder.id AS "officeNumber", \r\nml.account_no AS "loanAccountNumber",\r\nSUM(lt.amount) AS "repaymentAmount"\r\nFROM m_office mo\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')\r\nINNER JOIN m_client mc ON mc.office_id=ounder.id\r\nINNER JOIN m_loan ml ON ml.client_id = mc.id\r\nINNER JOIN r_enum_value rev ON rev.enum_id=ml.loan_status_id AND rev.enum_name = \'loan_status_id\'\r\nINNER JOIN m_loan_transaction lt ON lt.loan_id = ml.id\r\nINNER JOIN m_appuser au ON au.id = lt.appuser_id\r\nLEFT JOIN m_loan_arrears_aging laa ON laa.loan_id=ml.id\r\nLEFT JOIN m_payment_detail mpd ON mpd.id=lt.payment_detail_id\r\nLEFT JOIN m_currency cur ON cur.code = ml.currency_code\r\nLEFT JOIN m_group_client gc ON gc.client_id = mc.id\r\nLEFT JOIN m_group g ON g.id = gc.group_id\r\nLEFT JOIN m_staff lo ON lo.id = ml.loan_officer_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = ml.id\r\nWHERE ml.loan_status_id=300 AND mo.id=${officeId} AND (IFNULL(ml.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND (DATEDIFF(CURDATE(), lt.transaction_date) BETWEEN ${fromX} AND ${toY}) AND lt.is_reversed=0 AND lt.transaction_type_enum=2 AND laa.loan_id IS NULL\r\nGROUP BY ml.id\r\nORDER BY ounder.hierarchy, ml.currency_code, mc.account_no, ml.account_no', 'Payments received in the last fromX to toY days for any loan with the status Active (on-time)', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan payments received (Overdue Loans)', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\nml.id AS "loanId", \r\nmc.id AS "id", \r\nmc.firstname AS "firstName",\r\nmc.middlename AS "middleName",\r\nmc.lastname AS "lastName",\r\nmc.display_name AS "fullName",\r\nmc.mobile_no AS "mobileNo", \r\nml.principal_amount AS "loanAmount", \r\n(IFNULL(ml.principal_outstanding_derived, 0) + IFNULL(ml.interest_outstanding_derived, 0) + IFNULL(ml.fee_charges_outstanding_derived, 0) + IFNULL(ml.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nounder.id AS "officeNumber", \r\nml.account_no AS "loanAccountNumber",\r\nSUM(lt.amount) AS "repaymentAmount"\r\nFROM m_office mo\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')\r\nINNER JOIN m_client mc ON mc.office_id=ounder.id\r\nINNER JOIN m_loan ml ON ml.client_id = mc.id\r\nINNER JOIN r_enum_value rev ON rev.enum_id=ml.loan_status_id AND rev.enum_name = \'loan_status_id\'\r\nINNER JOIN m_loan_arrears_aging laa ON laa.loan_id=ml.id\r\nINNER JOIN m_loan_transaction lt ON lt.loan_id = ml.id\r\nINNER JOIN m_appuser au ON au.id = lt.appuser_id\r\nLEFT JOIN m_payment_detail mpd ON mpd.id=lt.payment_detail_id\r\nLEFT JOIN m_currency cur ON cur.code = ml.currency_code\r\nLEFT JOIN m_group_client gc ON gc.client_id = mc.id\r\nLEFT JOIN m_group g ON g.id = gc.group_id\r\nLEFT JOIN m_staff lo ON lo.id = ml.loan_officer_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = ml.id\r\nWHERE ml.loan_status_id=300 AND mo.id=${officeId} AND (IFNULL(ml.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND (DATEDIFF(CURDATE(), lt.transaction_date) BETWEEN ${fromX} AND ${toY}) AND (DATEDIFF(CURDATE(), laa.overdue_since_date_derived) BETWEEN ${overdueX} AND ${overdueY}) AND lt.is_reversed=0 AND lt.transaction_type_enum=2\r\nGROUP BY ml.id\r\nORDER BY ounder.hierarchy, ml.currency_code, mc.account_no, ml.account_no', 'Payments received in the last fromX to toY days for any loan with the status Overdue (arrears) between overdueX and overdueY days', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Happy Birthday', 'SMS', 'NonTriggered', 'Clients', 'SELECT \r\nc.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\no.id AS "officeNumber", \r\nc.date_of_birth AS "dateOfBirth",\r\nIF(c.date_of_birth IS NULL, 0, CEIL(DATEDIFF (NOW(), c.date_of_birth)/365)) AS "age"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nLEFT JOIN m_staff ms ON ms.id = c.staff_id\r\nWHERE o.id = ${officeId} AND c.status_enum = 300 AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND c.date_of_birth IS NOT NULL AND (DAY(c.date_of_birth)=DAY(NOW())) AND (MONTH(c.date_of_birth)=MONTH(NOW()))\r\nORDER BY ounder.hierarchy, c.account_no', 'This sends a message to all clients with the status Active on their Birthday', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan fully repaid', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\nc.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", \r\nl.principal_amount AS "loanAmount",\r\n(IFNULL(l.principal_outstanding_derived, 0) + IFNULL(l.interest_outstanding_derived, 0) + IFNULL(l.fee_charges_outstanding_derived, 0) + IFNULL(l.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nl.principal_disbursed_derived AS "loanDisbursed",\r\no.id AS "officeNumber",\r\nl.account_no AS "loanAccountId",\r\ngua.lastname AS "guarantorLastName", COUNT(gua.id) AS "numberOfGuarantors",\r\nls.duedate AS "dueDate",\r\nlaa.total_overdue_derived AS "totalDue",\r\ngp.display_name AS "groupName",\r\nl.total_repayment_derived AS "totalFullyPaid"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_loan l ON l.client_id = c.id\r\nLEFT JOIN m_staff lo ON lo.id = l.loan_officer_id\r\nLEFT JOIN m_currency cur ON cur.code = l.currency_code\r\nLEFT JOIN m_group_client gc ON gc.client_id = c.id\r\nLEFT JOIN m_group gp ON gp.id = l.group_id\r\nLEFT JOIN m_loan_repayment_schedule ls ON l.id = ls.loan_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = l.id\r\nLEFT JOIN m_loan_arrears_aging laa ON laa.loan_id=l.id\r\nWHERE o.id = ${officeId} AND (IFNULL(l.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND \r\n(DATEDIFF(CURDATE(), l.closedon_date) BETWEEN ${fromX} AND ${toY})\r\n AND (l.loan_status_id IN (600, 700))\r\nGROUP BY l.id\r\nORDER BY ounder.hierarchy, l.currency_code, c.account_no, l.account_no', 'All loans that have been fully repaid (Closed or Overpaid) in the last fromX to toY days', 0, 1);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Loan outstanding after final instalment date', 'SMS', 'NonTriggered', 'Loan', 'SELECT \r\nc.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", \r\nl.principal_amount AS "loanAmount",\r\no.id AS "officeNumber",\r\n(IFNULL(l.principal_outstanding_derived, 0) + IFNULL(l.interest_outstanding_derived, 0) + IFNULL(l.fee_charges_outstanding_derived, 0) + IFNULL(l.penalty_charges_outstanding_derived, 0)) AS "loanOutstanding",\r\nl.principal_disbursed_derived AS "loanDisbursed",\r\nls.duedate AS "paymentDueDate",\r\n(IFNULL(SUM(ls.principal_amount),0) - IFNULL(SUM(ls.principal_writtenoff_derived),0)\r\n + IFNULL(SUM(ls.interest_amount),0) - IFNULL(SUM(ls.interest_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.interest_waived_derived),0)\r\n + IFNULL(SUM(ls.fee_charges_amount),0) - IFNULL(SUM(ls.fee_charges_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.fee_charges_waived_derived),0)\r\n + IFNULL(SUM(ls.penalty_charges_amount),0) - IFNULL(SUM(ls.penalty_charges_writtenoff_derived),0) \r\n - IFNULL(SUM(ls.penalty_charges_waived_derived),0)\r\n) AS "totalDue",\r\nlaa.total_overdue_derived AS "totalOverdue",\r\nl.account_no AS "loanAccountId",\r\ngua.lastname AS "guarantorLastName",\r\nCOUNT(gua.id) AS "numberOfGuarantors",\r\ngp.display_name AS "groupName"\r\n\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_loan l ON l.client_id = c.id\r\nLEFT JOIN m_staff lo ON lo.id = l.loan_officer_id\r\nLEFT JOIN m_currency cur ON cur.code = l.currency_code\r\nLEFT JOIN m_loan_arrears_aging laa ON laa.loan_id = l.id\r\nLEFT JOIN m_group_client gc ON gc.client_id = c.id\r\nLEFT JOIN m_group gp ON gp.id = l.group_id\r\nLEFT JOIN m_loan_repayment_schedule ls ON l.id = ls.loan_id\r\nLEFT JOIN m_guarantor gua ON gua.loan_id = l.id\r\nWHERE o.id = ${officeId} AND (IFNULL(l.loan_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND l.loan_status_id = 300 AND l.expected_maturedon_date < CURDATE() \r\nAND (DATEDIFF(CURDATE(), l.expected_maturedon_date) BETWEEN ${fromX} AND ${toY})\r\nGROUP BY l.id\r\nORDER BY ounder.hierarchy, l.currency_code, c.account_no, l.account_no', 'All active loans (with an outstanding balance) between fromX to toY days after the final instalment date on their loan schedule', 0, 1);


INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES 
((SELECT id FROM stretchy_report WHERE report_name='Active Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'), 
((SELECT id FROM stretchy_report WHERE report_name='Active Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),

((SELECT id FROM stretchy_report WHERE report_name='Prospective Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Prospective Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),

((SELECT id FROM stretchy_report WHERE report_name='Active Loan Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Active Loan Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Active Loan Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='cycleXSelect' AND parameter_variable = 'cycleX'), 'cycleX'),
((SELECT id FROM stretchy_report WHERE report_name='Active Loan Clients' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='cycleYSelect' AND parameter_variable = 'cycleY'), 'cycleY'),

((SELECT id FROM stretchy_report WHERE report_name='Loan in arrears' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan in arrears' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan in arrears' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan in arrears' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY'),

((SELECT id FROM stretchy_report WHERE report_name='Loan payments due' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY'),

((SELECT id FROM stretchy_report WHERE report_name='Dormant Prospects' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Dormant Prospects' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),

((SELECT id FROM stretchy_report WHERE report_name='Active group leaders' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Active group leaders' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),

((SELECT id FROM stretchy_report WHERE report_name='Loan payments due (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='overdueXSelect' AND parameter_variable = 'overdueX'), 'overdueX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments due (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='overdueYSelect' AND parameter_variable = 'overdueY'), 'overdueY'),

((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Active Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Active Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Active Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Active Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY'),

((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='overdueXSelect' AND parameter_variable = 'overdueX'), 'overdueX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan payments received (Overdue Loans)' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='overdueYSelect' AND parameter_variable = 'overdueY'), 'overdueY'),

((SELECT id FROM stretchy_report WHERE report_name='Happy Birthday' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Happy Birthday' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),

((SELECT id FROM stretchy_report WHERE report_name='Loan fully repaid' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan fully repaid' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan fully repaid' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan fully repaid' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY'),

((SELECT id FROM stretchy_report WHERE report_name='Loan outstanding after final instalment date' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='OfficeIdSelectOne' AND parameter_variable = 'officeId'), 'officeId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan outstanding after final instalment date' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='loanOfficerIdSelectAll' AND parameter_variable = 'loanOfficerId'), 'loanOfficerId'),
((SELECT id FROM stretchy_report WHERE report_name='Loan outstanding after final instalment date' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='fromXSelect' AND parameter_variable = 'fromX'), 'fromX'),
((SELECT id FROM stretchy_report WHERE report_name='Loan outstanding after final instalment date' AND report_type = 'SMS'), (SELECT id FROM stretchy_parameter WHERE parameter_name='toYSelect' AND parameter_variable = 'toY'), 'toY');



INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES 
('Loan Repayment', 'SMS', 'Triggered', NULL, 'select ml.id as loanId,mc.id, mc.firstname, ifnull(mc.middlename,\'\') as middlename, mc.lastname, mc.display_name as FullName, mobile_no as mobileNo, mc.group_name as GroupName, round(ml.principal_amount, ml.currency_digits) as LoanAmount, round(ml.`total_outstanding_derived`, ml.currency_digits) as LoanOutstanding,\nml.`account_no` as LoanAccountId, round(mlt.amountPaid, ml.currency_digits) as repaymentAmount\nFROM m_office mo\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')\nAND ounder.hierarchy like CONCAT(\'.\', \'%\')\nLEFT JOIN (\n select \n ml.id as loanId, \n ifnull(mc.id,mc2.id) as id, \n ifnull(mc.firstname,mc2.firstname) as firstname, \n ifnull(mc.middlename,ifnull(mc2.middlename,(\'\'))) as middlename, \n ifnull(mc.lastname,mc2.lastname) as lastname, \n ifnull(mc.display_name,mc2.display_name) as display_name, \n ifnull(mc.status_enum,mc2.status_enum) as status_enum,\n ifnull(mc.mobile_no,mc2.mobile_no) as mobile_no,\n ifnull(mg.office_id,mc2.office_id) as office_id,\n ifnull(mg.staff_id,mc2.staff_id) as staff_id,\n mg.id as group_id, \nmg.display_name as group_name\n from\n m_loan ml\n left join m_group mg on mg.id = ml.group_id\n left join m_group_client mgc on mgc.group_id = mg.id\n left join m_client mc on mc.id = mgc.client_id\n left join m_client mc2 on mc2.id = ml.client_id\n order by loanId\n ) mc on mc.office_id = ounder.id\nright join m_loan as ml on mc.loanId = ml.id\nright join(\nselect mlt.amount as amountPaid,mlt.id,mlt.loan_id\nfrom m_loan_transaction mlt\nwhere mlt.is_reversed = 0 \ngroup by mlt.loan_id\n) as mlt on mlt.loan_id = ml.id\nright join m_loan_repayment_schedule as mls1 on ml.id = mls1.loan_id and mls1.`completed_derived` = 0\nand mls1.installment = (SELECT MIN(installment) from m_loan_repayment_schedule where loan_id = ml.id and duedate <= CURDATE() and completed_derived=0)\nwhere mc.status_enum = 300 and mobile_no is not null and ml.`loan_status_id` = 300\nand (mo.id = ${officeId} or ${officeId} = -1)\nand (mc.staff_id = ${loanOfficerId} or ${loanOfficerId} = -1)\nand (ml.loan_type_enum = ${loanType} or ${loanType} = -1)\nand ml.id in (select mla.loan_id from m_loan_arrears_aging mla)\ngroup by ml.id', 'Loan Repayment', 0, 0);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES 
('Loan Approved', 'SMS', 'Triggered', NULL, 'SELECT mc.id, mc.firstname, mc.middlename as middlename, mc.lastname, mc.display_name as FullName, mc.mobile_no as mobileNo, mc.group_name as GroupName, mo.name as officename, ml.id as loanId, ml.account_no as accountnumber, ml.principal_amount_proposed as loanamount, ml.annual_nominal_interest_rate as annualinterestrate FROM m_office mo JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\') AND ounder.hierarchy like CONCAT(\'.\', \'%\') LEFT JOIN ( select  ml.id as loanId,  ifnull(mc.id,mc2.id) as id,  ifnull(mc.firstname,mc2.firstname) as firstname,  ifnull(mc.middlename,ifnull(mc2.middlename,(\'\'))) as middlename,  ifnull(mc.lastname,mc2.lastname) as lastname,  ifnull(mc.display_name,mc2.display_name) as display_name,  ifnull(mc.status_enum,mc2.status_enum) as status_enum, ifnull(mc.mobile_no,mc2.mobile_no) as mobile_no, ifnull(mg.office_id,mc2.office_id) as office_id, ifnull(mg.staff_id,mc2.staff_id) as staff_id, mg.id as group_id, mg.display_name as group_name from m_loan ml left join m_group mg on mg.id = ml.group_id left join m_group_client mgc on mgc.group_id = mg.id left join m_client mc on mc.id = mgc.client_id left join m_client mc2 on mc2.id = ml.client_id order by loanId ) mc on mc.office_id = ounder.id  left join m_loan ml on ml.id = mc.loanId WHERE mc.status_enum = 300 and mc.mobile_no is not null and (mo.id = ${officeId} or ${officeId} = -1) and (mc.staff_id = ${loanOfficerId} or ${loanOfficerId} = -1)and (ml.id = ${loanId} or ${loanId} = -1)and (mc.id = ${clientId} or ${clientId} = -1)and (mc.group_id = ${groupId} or ${groupId} = -1)and (ml.loan_type_enum = ${loanType} or ${loanType} = -1)', 'Loan and client data of approved loan', 0, 0);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES 
('Loan Rejected', 'SMS', 'Triggered', NULL, 'SELECT mc.id, mc.firstname, mc.middlename as middlename, mc.lastname, mc.display_name as FullName, mc.mobile_no as mobileNo, mc.group_name as GroupName,  mo.name as officename, ml.id as loanId, ml.account_no as accountnumber, ml.principal_amount_proposed as loanamount, ml.annual_nominal_interest_rate as annualinterestrate  FROM  m_office mo  JOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\')  AND ounder.hierarchy like CONCAT(\'.\', \'%\')  LEFT JOIN (  select   ml.id as loanId,   ifnull(mc.id,mc2.id) as id,   ifnull(mc.firstname,mc2.firstname) as firstname,   ifnull(mc.middlename,ifnull(mc2.middlename,(\'\'))) as middlename,   ifnull(mc.lastname,mc2.lastname) as lastname,   ifnull(mc.display_name,mc2.display_name) as display_name,   ifnull(mc.status_enum,mc2.status_enum) as status_enum,  ifnull(mc.mobile_no,mc2.mobile_no) as mobile_no,  ifnull(mg.office_id,mc2.office_id) as office_id,  ifnull(mg.staff_id,mc2.staff_id) as staff_id,  mg.id as group_id,  mg.display_name as group_name  from  m_loan ml  left join m_group mg on mg.id = ml.group_id  left join m_group_client mgc on mgc.group_id = mg.id  left join m_client mc on mc.id = mgc.client_id  left join m_client mc2 on mc2.id = ml.client_id  order by loanId  ) mc on mc.office_id = ounder.id  left join m_loan ml on ml.id = mc.loanId  WHERE mc.status_enum = 300 and mc.mobile_no is not null  and (mo.id = ${officeId} or ${officeId} = -1)  and (mc.staff_id = ${loanOfficerId} or ${loanOfficerId} = -1) and (ml.id = ${loanId} or ${loanId} = -1) and (mc.id = ${clientId} or ${clientId} = -1) and (mc.group_id = ${groupId} or ${groupId} = -1)  and (ml.loan_type_enum = ${loanType} or ${loanType} = -1)', 'Loan and client data of rejected loan', 0, 0);
INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `use_report`) VALUES 
('Client Rejected', 'SMS', 'Triggered', 'Clients', 'SELECT c.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\no.id AS "officeNumber"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nWHERE o.id = ${officeId} AND c.id = ${clientId} AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId})', 'Client Rejection', '1'), 
('Client Activated', 'SMS', 'Triggered', 'Clients', 'SELECT c.id AS "id", \r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo", CONCAT(REPEAT("..", ((LENGTH(ounder.`hierarchy`) - LENGTH(\r\nREPLACE(ounder.`hierarchy`, \'.\', \'\')) - 1))), ounder.`name`) AS "officeName", \r\no.id AS "officeNumber"\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nLEFT JOIN r_enum_value r ON r.enum_name = \'status_enum\' AND r.enum_id = c.status_enum\r\nWHERE o.id = ${officeId} AND c.id = ${clientId} AND (IFNULL(c.staff_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId})', 'Client Activation', '1'), 
('Savings Rejected', 'SMS', 'Triggered', 'Savings', 'SELECT \r\nc.id AS "id",\r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo",\r\ns.account_no AS "savingsAccountNo",\r\nounder.id AS "officeNumber",\r\nounder.name AS "officeName"\r\n\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_savings_account s ON s.client_id = c.id\r\nJOIN m_savings_product sp ON sp.id = s.product_id\r\nLEFT JOIN m_staff st ON st.id = s.field_officer_id\r\nLEFT JOIN m_currency cur ON cur.code = s.currency_code\r\nWHERE o.id = ${officeId} AND (IFNULL(s.field_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND s.id = ${savingsId}', 'Savings Rejected', '1'), 
('Savings Activated', 'SMS', 'Triggered', 'Savings', 'SELECT \r\nc.id AS "id",\r\nc.firstname AS "firstName",\r\nc.middlename AS "middleName",\r\nc.lastname AS "lastName",\r\nc.display_name AS "fullName",\r\nc.mobile_no AS "mobileNo",\r\ns.account_no AS "savingsAccountNo",\r\nounder.id AS "officeNumber",\r\nounder.name AS "officeName"\r\n\r\nFROM m_office o\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(o.hierarchy, \'%\')\r\nJOIN m_client c ON c.office_id = ounder.id\r\nJOIN m_savings_account s ON s.client_id = c.id\r\nJOIN m_savings_product sp ON sp.id = s.product_id\r\nLEFT JOIN m_staff st ON st.id = s.field_officer_id\r\nLEFT JOIN m_currency cur ON cur.code = s.currency_code\r\nWHERE o.id = ${officeId} AND (IFNULL(s.field_officer_id, -10) = ${loanOfficerId} OR "-1" = ${loanOfficerId}) AND s.id = ${savingsId}', 'Savings Activation', '1');

INSERT INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`, `enum_type`)
VALUES ('loan_type_enum', '1', 'Individual Loan', 'Individual Loan', '0');
INSERT INTO `r_enum_value` (`enum_name`, `enum_id`, `enum_message_property`, `enum_value`, `enum_type`)
VALUES ('loan_type_enum', '2', 'Group Loan', 'Group Loan', '0');


INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `selectAll`, `parameter_sql`, `parent_id`)
VALUES ('DefaultLoan', 'loanId', 'Loan', 'none', 'number', '-1', 'Y', 'select ml.id \nfrom m_loan ml \nleft join m_client mc on mc.id = ml.client_id \nleft join m_office mo on mo.id = mc.office_id \nwhere mo.id = ${officeId} or ${officeId} = -1', '5');
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `selectAll`, `parameter_sql`, `parent_id`)
VALUES ('DefaultClient', 'clientId', 'Client', 'none', 'number', '-1', 'Y', 'select mc.id \nfrom m_client mc\n left join m_office on mc.office_id = mo.id\n where mo.id = ${officeId} or ${officeId} = -1', '5');
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `selectAll`, `parameter_sql`, `parent_id`)
VALUES ('DefaultGroup', 'groupId', 'Group', 'none', 'number', '-1', 'Y', 'select mg.id \nfrom m_group mg\nleft join m_office mo on mg.office_id = mo.id\nwhere mo.id = ${officeId} or ${officeId} = -1', '5');
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `selectAll`, `parameter_sql`, `parent_id`)
VALUES ('SelectLoanType', 'loanType', 'Loan Type', 'select', 'number', '-1', 'Y', "select\nenum_id as id,\nenum_value as value\nfrom r_enum_value\nwhere enum_name = 'loan_type_enum'", NULL);
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `special`, `selectOne`, `selectAll`, `parameter_sql`, `parent_id`) VALUES 
('DefaultSavings', 'savingsId', 'Savings', 'none', 'number', '-1', NULL, NULL, 'Y', NULL, 5);
INSERT INTO `stretchy_parameter` (`parameter_name`, `parameter_variable`, `parameter_label`, `parameter_displayType`, `parameter_FormatType`, `parameter_default`, `selectAll`, `parameter_sql`, `parent_id`) VALUES ('DefaultSavingsTransactionId', 'savingsTransactionId', 'Savings Transaction', 'none', 'number', '-1', 'Y', NULL, '5');

SET @LRej = (select id from `stretchy_report` where `report_name`='Loan Rejected');
SET @LApp = (select id from `stretchy_report` where `report_name`='Loan Approved');
SET @LRep = (select id from `stretchy_report` where `report_name`='Loan Repayment');
SET @Office = (select id from `stretchy_parameter` where `parameter_name`='OfficeIdSelectOne');
SET @loanOfficer = (select id from `stretchy_parameter` where `parameter_name`='loanOfficerIdSelectAll');
SET @DLoan = (select id from `stretchy_parameter` where `parameter_name`='DefaultLoan');
SET @DClient = (select id from `stretchy_parameter` where `parameter_name`='DefaultClient');
SET @DGroup = (select id from `stretchy_parameter` where `parameter_name`='DefaultGroup');
SET @LoanType = (select id from `stretchy_parameter` where `parameter_name`='SelectLoanType');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`)
VALUES (@LRej, @Office, 'officeId'),
(@LApp, @Office, 'officeId'),
(@LRep, @Office, 'officeId'),
(@LRej, @loanOfficer, 'loanOfficerId'),
(@LApp, @loanOfficer, 'loanOfficerId'),
(@LRep, @loanOfficer, 'loanOfficerId'),
(@LRej, @DLoan, 'loanId'),
(@LApp, @DLoan, 'loanId'),
(@LRej, @DClient, 'clientId'),
(@LApp, @DClient, 'clientId'),
(@LRej, @DGroup, 'groupId'),
(@LApp, @DGroup, 'groupId'),
(@LRej, @LoanType, 'loanType'),
(@LApp, @LoanType, 'loanType'),
(@LRep, @LoanType, 'loanType');

SET @CRej = (select id from `stretchy_report` where `report_name`='Client Rejected');
SET @CAct = (select id from `stretchy_report` where `report_name`='Client Activated');
SET @SRej = (select id from `stretchy_report` where `report_name`='Savings Rejected');
SET @SAct = (select id from `stretchy_report` where `report_name`='Savings Activated');

SET @Office = (select id from `stretchy_parameter` where `parameter_name`='OfficeIdSelectOne');
SET @fieldOfficer = (select id from `stretchy_parameter` where `parameter_name`='loanOfficerIdSelectAll');
SET @DClient = (select id from `stretchy_parameter` where `parameter_name`='DefaultClient');
SET @DSavings = (select id from `stretchy_parameter` where `parameter_name`='DefaultSavings');

INSERT INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES 
(@CRej, @Office, 'officeId'),
(@CAct, @Office, 'officeId'),
(@CRej, @fieldOfficer, 'loanOfficerId'),
(@CAct, @fieldOfficer, 'loanOfficerId'),
(@CRej, @DClient, 'clientId'),
(@CAct, @DClient, 'clientId'),

(@SRej, @Office, 'officeId'),
(@SRej, @fieldOfficer, 'loanOfficerId'),
(@SRej, @DSavings, 'savingsId'),
(@SAct, @Office, 'officeId'),
(@SAct, @fieldOfficer, 'loanOfficerId'),
(@SAct, @DSavings, 'savingsId');

INSERT INTO `stretchy_report` (`report_name`, `report_type`, `report_subtype`, `report_category`, `report_sql`, `description`, `core_report`, `use_report`) VALUES ('Savings Deposit', 'SMS', 'Triggered', NULL, 'SELECT sc.savingsId AS savingsId, sc.id AS clientId, sc.firstname, IFNULL(sc.middlename,\'\') AS middlename, sc.lastname, sc.display_name AS FullName, sc.mobile_no AS mobileNo,\r\nms.`account_no` AS savingsAccountNo, ROUND(mst.amountPaid, ms.currency_digits) AS depositAmount, ms.account_balance_derived AS balance, \r\nmst.transactionDate AS transactionDate\r\nFROM m_office mo\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\') AND ounder.hierarchy LIKE CONCAT(\'.\', \'%\')\r\nLEFT JOIN (\r\nSELECT \r\n sa.id AS savingsId, mc.id AS id, mc.firstname AS firstname, mc.middlename AS middlename, mc.lastname AS lastname, \r\n mc.display_name AS display_name, mc.status_enum AS status_enum, \r\n mc.mobile_no AS mobile_no, mc.office_id AS office_id, \r\n mc.staff_id AS staff_id\r\nFROM\r\nm_savings_account sa\r\nLEFT JOIN m_client mc ON mc.id = sa.client_id\r\nORDER BY savingsId) sc ON sc.office_id = ounder.id\r\nRIGHT JOIN m_savings_account AS ms ON sc.savingsId = ms.id\r\nRIGHT JOIN(\r\nSELECT st.amount AS amountPaid, st.id, st.savings_account_id, st.id AS savingsTransactionId, st.transaction_date AS transactionDate\r\nFROM m_savings_account_transaction st\r\nWHERE st.is_reversed = 0\r\nGROUP BY st.savings_account_id\r\n) AS mst ON mst.savings_account_id = ms.id\r\nWHERE sc.mobile_no IS NOT NULL AND (mo.id = ${officeId} OR ${officeId} = -1) AND (sc.staff_id = ${loanOfficerId} OR ${loanOfficerId} = -1) AND mst.savingsTransactionId = ${savingsTransactionId}', 'Savings Deposit', 0, 1), 
('Savings Withdrawal', 'SMS', 'Triggered', NULL, 'SELECT sc.savingsId AS savingsId, sc.id AS clientId, sc.firstname, IFNULL(sc.middlename,\'\') AS middlename, sc.lastname, sc.display_name AS FullName, sc.mobile_no AS mobileNo,\r\nms.`account_no` AS savingsAccountNo, ROUND(mst.amountPaid, ms.currency_digits) AS withdrawAmount, ms.account_balance_derived AS balance, \r\nmst.transactionDate AS transactionDate\r\nFROM m_office mo\r\nJOIN m_office ounder ON ounder.hierarchy LIKE CONCAT(mo.hierarchy, \'%\') AND ounder.hierarchy LIKE CONCAT(\'.\', \'%\')\r\nLEFT JOIN (\r\nSELECT \r\n sa.id AS savingsId, mc.id AS id, mc.firstname AS firstname, mc.middlename AS middlename, mc.lastname AS lastname, \r\n mc.display_name AS display_name, mc.status_enum AS status_enum, \r\n mc.mobile_no AS mobile_no, mc.office_id AS office_id, \r\n mc.staff_id AS staff_id\r\nFROM\r\nm_savings_account sa\r\nLEFT JOIN m_client mc ON mc.id = sa.client_id\r\nORDER BY savingsId) sc ON sc.office_id = ounder.id\r\nRIGHT JOIN m_savings_account AS ms ON sc.savingsId = ms.id\r\nRIGHT JOIN(\r\nSELECT st.amount AS amountPaid, st.id, st.savings_account_id, st.id AS savingsTransactionId, st.transaction_date AS transactionDate\r\nFROM m_savings_account_transaction st\r\nWHERE st.is_reversed = 0\r\nGROUP BY st.savings_account_id\r\n) AS mst ON mst.savings_account_id = ms.id\r\nWHERE sc.mobile_no IS NOT NULL AND (mo.id = ${officeId} OR ${officeId} = -1) AND (sc.staff_id = ${loanOfficerId} OR ${loanOfficerId} = -1) AND mst.savingsTransactionId = ${savingsTransactionId}', 'Savings Withdrawal', 0, 1);


SET @SDep = (select id from `stretchy_report` where `report_name`='Savings Deposit');
SET @SWith = (select id from `stretchy_report` where `report_name`='Savings Withdrawal');

SET @savingsTransaction = (select id from `stretchy_parameter` where `parameter_name`='DefaultSavingsTransactionId');

INSERT IGNORE INTO `stretchy_report_parameter` (`report_id`, `parameter_id`, `report_parameter_name`) VALUES 
(@SDep, @Office, 'officeId'),
(@SDep, @loanOfficer, 'loanOfficerId'),
(@SDep, @savingsTransaction, 'savingsTransactionId'),
(@SWith, @Office, 'officeId'),
(@SWith, @loanOfficer, 'loanOfficerId'),
(@SWith, @savingsTransaction, 'savingsTransactionId');

INSERT INTO `c_external_service` (`name`) VALUES ('MESSAGE_GATEWAY');

SET @EId = (SELECT `id` FROM `c_external_service` WHERE name = 'MESSAGE_GATEWAY');

INSERT INTO `c_external_service_properties` (`name`, `value`, `external_service_id`) VALUES 
('host_name', 'localhost', @EId), 
('port_number', '9191', @EId), 
('end_point', '/', @EId), 
('tenant_app_key', NULL, @EId);


CREATE TABLE `scheduled_email_campaign` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`campaign_name` VARCHAR(100) NOT NULL,
	`campaign_type` INT(11) NOT NULL,
	`businessRule_id` INT(11) NOT NULL,
	`param_value` TEXT NULL,
	`status_enum` INT(11) NOT NULL,
	`email_subject` VARCHAR(100) NOT NULL, 
	`email_message` TEXT NOT NULL,
	`email_attachment_file_format` VARCHAR(10) NOT NULL,
	`stretchy_report_id` INT(11) NOT NULL,
	`stretchy_report_param_map` TEXT NULL DEFAULT NULL,
	`closedon_date` DATE NULL DEFAULT NULL,
	`closedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`submittedon_date` DATE NULL DEFAULT NULL,
	`submittedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`approvedon_date` DATE NULL DEFAULT NULL,
	`approvedon_userid` BIGINT(20) NULL DEFAULT NULL,
	`recurrence` VARCHAR(100) NULL DEFAULT NULL,
	`next_trigger_date` DATETIME NULL DEFAULT NULL,
	`last_trigger_date` DATETIME NULL DEFAULT NULL,
	`recurrence_start_date` DATETIME NULL DEFAULT NULL,
	`is_visible` TINYINT(1) NULL DEFAULT '1',
	`previous_run_error_log` TEXT NULL DEFAULT NULL,
	`previous_run_error_message` TEXT NULL DEFAULT NULL,
	PRIMARY KEY (`id`),
	CONSTRAINT `scheduled_email_campaign_ibfk_1` FOREIGN KEY (`stretchy_report_id`) REFERENCES `stretchy_report` (`id`)	
);

CREATE TABLE IF NOT EXISTS scheduled_email_messages_outbound (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `group_id` bigint(20) DEFAULT NULL,
  `client_id` bigint(20) DEFAULT NULL,
  `staff_id` bigint(20) DEFAULT NULL,
  `email_campaign_id` bigint(20) DEFAULT NULL,
  `status_enum` int(5) NOT NULL DEFAULT '100',
  `email_address` varchar(50) NOT NULL,
  `email_subject` varchar(50) NOT NULL,
  `message` text NOT NULL,
  `campaign_name` varchar(200) DEFAULT NULL,
  `submittedon_date` date,
  `error_message` text,
  PRIMARY KEY (`id`),
  KEY `SEFKGROUP000000001` (`group_id`),
  KEY `SEFKCLIENT00000001` (`client_id`),
  key `SEFKSTAFF000000001` (`staff_id`),
  CONSTRAINT `SEFKGROUP000000001` FOREIGN KEY (`group_id`) REFERENCES `m_group` (`id`),
  CONSTRAINT `SEFKCLIENT00000001` FOREIGN KEY (`client_id`) REFERENCES `m_client` (`id`),
  CONSTRAINT `SEFKSTAFF000000001` FOREIGN KEY (`staff_id`) REFERENCES `m_staff` (`id`),
  CONSTRAINT `fk_schedule_email_campign1` FOREIGN KEY (`email_campaign_id`) REFERENCES `scheduled_email_campaign` (`id`)
);

create table if not exists scheduled_email_configuration (
id int primary key auto_increment,
name varchar(50) not null,
`value` varchar(200) null,
constraint unique_name unique (name)
);

DELETE FROM `m_permission` WHERE `code`='READ_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'READ_EMAIL', 'EMAIL', 'READ', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL', 'EMAIL', 'CREATE', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL_CHECKER', 'EMAIL', 'CREATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL', 'EMAIL', 'UPDATE', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL_CHECKER', 'EMAIL', 'UPDATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL', 'EMAIL', 'DELETE', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL_CHECKER', 'EMAIL', 'DELETE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='READ_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'READ_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'READ', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'CREATE', 0);

DELETE FROM `m_permission` WHERE `code`='CREATE_EMAIL_CAMPAIGN_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CREATE_EMAIL_CAMPAIGN_CHECKER', 'EMAIL_CAMPAIGN', 'CREATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'UPDATE', 0);

DELETE FROM `m_permission` WHERE `code`='UPDATE_EMAIL_CAMPAIGN_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'UPDATE_EMAIL_CAMPAIGN_CHECKER', 'EMAIL_CAMPAIGN', 'UPDATE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'DELETE', 0);

DELETE FROM `m_permission` WHERE `code`='DELETE_EMAIL_CAMPAIGN_CHECKER';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'DELETE_EMAIL_CAMPAIGN_CHECKER', 'EMAIL_CAMPAIGN', 'DELETE_CHECKER', 0);

DELETE FROM `m_permission` WHERE `code`='CLOSE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'CLOSE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'CLOSE', 0);

DELETE FROM `m_permission` WHERE `code`='ACTIVATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'ACTIVATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'ACTIVATE', 0);

DELETE FROM `m_permission` WHERE `code`='REACTIVATE_EMAIL_CAMPAIGN';
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'REACTIVATE_EMAIL_CAMPAIGN', 'EMAIL_CAMPAIGN', 'REACTIVATE', 0);


INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('organisation', 'READ_EMAIL_CONFIGURATION', 'EMAIL_CONFIGURATION', 'READ', 0),
('organisation', 'UPDATE_EMAIL_CONFIGURATION', 'EMAIL_CONFIGURATION', 'UPDATE', 0);

Alter table m_client
ADD Column email_address varchar(150);

Alter table m_staff
ADD Column email_address varchar(150);


insert into job (name, display_name, cron_expression, create_time)
values ('Execute Email', 'Execute Email', '0 0/10 * * * ?', NOW());

insert into job (name, display_name, cron_expression, create_time)
values ('Update Email Outbound with campaign message', 'Update Email Outbound with campaign message', '0 0/15 * * * ?', NOW());

INSERT INTO `scheduled_email_configuration` (`name`)
VALUES ('SMTP_SERVER'),
('SMTP_PORT'),('SMTP_USERNAME'), ('SMTP_PASSWORD');

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES
('report', 'READ_Active Clients - Email', 'Active Clients - Email', 'READ', 0),
('report', 'READ_Prospective Clients - Email', 'Prospective Clients - Email', 'READ', 0),
('report', 'READ_Active Loan Clients - Email', 'Active Loan Clients - Email', 'READ', 0),
('report', 'READ_Loans in arrears - Email', 'Loans in arrears - Email', 'READ', 0),
('report', 'READ_Loans disbursed to clients - Email', 'Loans disbursed to clients - Email', 'READ', 0),
('report', 'READ_Loan payments due - Email', 'Loan payments due - Email', 'READ', 0),
('report', 'READ_Dormant Prospects - Email', 'Dormant Prospects - Email', 'READ', 0),
('report', 'READ_Active Group Leaders - Email', 'Active Group Leaders - Email', 'READ', 0),
('report', 'READ_Loan Payments Due (Overdue Loans) - Email', 'Loan Payments Due (Overdue Loans) - Email', 'READ', 0),
('report', 'READ_Loan Payments Received (Active Loans) - Email', 'Loan Payments Received (Active Loans) - Email', 'READ', 0),
('report', 'READ_Loan Payments Received (Overdue Loans) - Email', 'Loan Payments Received (Overdue Loans)  - Email', 'READ', 0),
('report', 'READ_Loan Fully Repaid - Email', 'Loan Fully Repaid - Email', 'READ', 0),
('report', 'READ_Loans Outstanding after final instalment date - Email', 'Loans Outstanding after final instalment date - Email', 'READ', 0),
('report', 'READ_Happy Birthday - Email', 'Happy Birthday - Email', 'READ', 0),
('report', 'READ_Loan Rejected - Email', 'Loan Rejected - Email', 'READ', 0),
('report', 'READ_Loan Approved - Email', 'Loan Approved - Email', 'READ', 0),
('report', 'READ_Loan Repayment - Email', 'Loan Repayment - Email', 'READ', 0);

