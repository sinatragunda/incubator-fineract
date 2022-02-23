
INSERT INTO `stretchy_report` VALUES (null ,'ActiveSavingsBalances', 'Table', '', 'Loan', 'SELECT SUM(ms.account_balance_derived) as amount FROM m_savings_account ms join m_client mc on mc.id = ms.client_id where ms.status_enum = 300 AND mc.office_id = ${officeId}
','Total balances of active savings accounts', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='ActiveSavingsBalances'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');