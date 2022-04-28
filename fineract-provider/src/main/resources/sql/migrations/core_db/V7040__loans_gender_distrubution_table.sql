
INSERT INTO `stretchy_report` VALUES (null ,'LoansGenderDistrubution', 'Table', '', 'Loan', 'SELECT 
SUM(ml.principal_disbursed_derived)AS amount ,
mp.name AS NAME,
ifnull(mcv.code_value ,"Not Specified") AS gender 
FROM m_loan ml 
JOIN m_client mc ON mc.id = ml.client_id
JOIN m_product_loan mp ON mp.id = ml.product_id
left JOIN m_code_value mcv ON mcv.id = mc.gender_cv_id 
WHERE ml.loan_status_id = 300
AND mc.office_id = ${officeId}
GROUP BY mc.gender_cv_id','Loans Gender Distrubution Summary', 0, 1 ,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='LoansGenderDistrubution'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');