
INSERT INTO `stretchy_report` VALUES (null ,'ActiveLoansPortfolio', 'Table', '', 'Loan', 'SELECT sum(ml.principal_disbursed_derived) as amount FROM m_loan ml join m_client mc on mc.id = ml.client_id where ml.loan_status_id = 300 AND mc.office_id = ${officeId}
','Total principal of all active loans', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='ActiveLoansPortfolio'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');