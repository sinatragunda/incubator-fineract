
INSERT INTO `stretchy_report` VALUES (null ,'PrincipalAppliedToday', 'Table', '', 'Loan', '
SELECT sum(m.principal_amount) as amount FROM m_loan m JOIN m_client mc ON m.client_id = mc.id WHERE m.submittedon_date = CURDATE() AND mc.office_id = ${officeId} ','Used For keeping track of loans applied today', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='PrincipalAppliedToday'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');