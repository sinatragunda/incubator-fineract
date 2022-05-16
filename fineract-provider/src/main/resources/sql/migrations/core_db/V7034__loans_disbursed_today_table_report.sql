
-- Added 22/02/2022 
-- First query thing seems to work let me look for more data 

-- Newly Added 11/05/2011
ALTER TABLE `stretchy_report` ADD COLUMN `self_service_user_report` smallint(5) default 0 ;


INSERT INTO `stretchy_report` VALUES (null ,'LoansDisbursedToday', 'Table', '', 'Loan', 'SELECT ifnull(sum(m.amount),0) as amount FROM m_loan_transaction m WHERE m.transaction_date = CURDATE() AND m.transaction_type_enum = 1 AND m.office_id = ${officeId}','Used For keeping track of loans disbursed today', 0, 1 ,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='LoansDisbursedToday'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');
