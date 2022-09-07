
INSERT INTO `stretchy_report` VALUES (null ,'Loans Disbursed Today', 'Table', '', 'Loan','SELECT 
ml.id AS "ID",
DATE_FORMAT(m.transaction_date ,"%d %M %Y") AS "Transaction Date",
mc.display_name AS "Client Name",
mpl.name AS "Product Name",
ifnull(ml.principal_amount_proposed ,0) AS "Principal",
ifnull(m.amount,0) as Amount 
FROM m_loan_transaction m 
JOIN m_loan ml ON ml.id = m.loan_id
JOIN m_product_loan mpl ON mpl.id = ml.product_id
JOIN m_client mc ON mc.id = ml.client_id
where m.transaction_type_enum = 1 
AND m.transaction_date = CURDATE()
AND m.office_id = ${officeId}','Used For keeping track of loans disbursed today', 0, 1,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='Loans Disbursed Today'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');