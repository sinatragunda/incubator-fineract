
INSERT INTO `stretchy_report` VALUES (null ,'Deposits Today', 'Table', '', 'Savings', 'SELECT
mo.name AS Branch,
mst.transaction_date AS Date, 
mst.id AS Id, 
mc.display_name AS CLIENT,
mst.amount AS Amount
FROM 
m_savings_account_transaction mst 
LEFT JOIN m_savings_account ms ON ms.id = mst.savings_account_id
LEFT JOIN m_client mc ON mc.id = ms.client_id
LEFT JOIN m_office mo ON mo.id = mst.office_id
WHERE mst.transaction_date = CURDATE()
AND mst.transaction_type_enum = 1
AND mst.office_id =${officeId}
','Total deposits today', 0, 1 ,1 ,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='Deposits Today'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');