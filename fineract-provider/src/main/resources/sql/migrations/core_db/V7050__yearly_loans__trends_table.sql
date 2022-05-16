INSERT INTO `stretchy_report` VALUES (null ,'YearlyLoansTrends', 'Table', '', 'Loan',
    'SELECT MONTH(ml.approvedon_date) AS monthDay,
     SUM(ifnull(ml.approved_principal,0)) AS approvedPrincipal, 
    SUM(ifnull(ml.principal_amount_proposed,0)) AS appliedPrincipal
    FROM 
m_loan ml
JOIN m_client mc ON mc.id = ml.client_id
WHERE ml.loan_status_id = 300 AND YEAR(CURDATE()) AND mc.office_id = ${officeId}
GROUP BY MONTH(ml.approvedon_date)','Yearly loan trends', 0, 1,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='YearlyLoansTrends'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');

