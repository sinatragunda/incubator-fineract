
INSERT INTO `stretchy_report` VALUES (null ,'LoanProductsPortfolio', 'Table', '', 'Loan', 'SELECT 
SUM(ml.principal_disbursed_derived)AS amount ,
mp.name AS name 
FROM m_loan ml 
JOIN m_client mc ON mc.id = ml.client_id
JOIN m_product_loan mp ON mp.id = ml.product_id 
WHERE ml.loan_status_id = 300
AND mc.office_id = ${officeId}
GROUP BY mp.name
','Loan Products Portfolio Summary', 0, 1 ,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='LoanProductsPortfolio'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');