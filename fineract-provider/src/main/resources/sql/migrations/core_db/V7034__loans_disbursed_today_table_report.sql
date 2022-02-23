
-- Added 22/02/2022 
-- First query thing seems to work let me look for more data 

INSERT INTO `stretchy_report` VALUES (null ,'LoansDisbursedToday', 'Table', '', 'Loan', 'SELECT m.amount as amount FROM m_loan_transaction m WHERE m.transaction_date = CURDATE() AND m.transaction_type_enum = 1 AND m.office_id = ${officeId}','Used For keeping track of loans disbursed today', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='LoansDisbursedToday'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');
