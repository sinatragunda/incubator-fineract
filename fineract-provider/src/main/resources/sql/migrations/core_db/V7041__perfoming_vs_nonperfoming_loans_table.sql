
INSERT INTO `stretchy_report` VALUES (null ,'LoansPerfomance', 'Table', '', 'Loan', 'SELECT 
SUM(ml.principal_disbursed_derived)AS amount ,
ml.is_npa AS npa
FROM m_loan ml 
JOIN m_client mc ON mc.id = ml.client_id
WHERE ml.loan_status_id = 300
AND mc.office_id = ${officeId}
GROUP BY ml.is_npa','Loans Perfomance Summary ,grouped by isNpa', 0, 1 ,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='LoansPerfomance'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');