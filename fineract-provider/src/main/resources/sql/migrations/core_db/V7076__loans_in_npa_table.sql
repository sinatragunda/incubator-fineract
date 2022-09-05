
INSERT INTO `stretchy_report` VALUES (null ,'Loans In Npa', 'Table', '', 'Loan','SELECT  
ml.account_no AS "Account Number" ,
mc.display_name AS "Client Name" ,
DATE_FORMAT(ml.submittedon_date ,"%d %M %Y") AS "Applied Date" ,
IFNULL(ml.principal_amount,0) as "Principal" ,
IFNULL(ml.interest_outstanding_derived ,0) AS "Interest" ,
ifnull(ml.arrearstolerance_amount,0) AS "Arreas Tolerance" ,
IFNULL(ml.total_outstanding_derived ,0) AS "Total Due"
FROM m_loan ml JOIN m_client mc ON ml.client_id = mc.id WHERE mc.office_id = ${officeId} and
ml.is_npa= true','Used For keeping track of loans applied today', 0, 1,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='Loans In Npa'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');