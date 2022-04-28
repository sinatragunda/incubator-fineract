INSERT INTO `stretchy_report` VALUES (null ,'MainGlBalances', 'Table', '', 'Accounting',
    'SELECT ifnull(SUM(age.amount),0) AS amount FROM acc_gl_journal_entry age JOIN acc_gl_account aga ON aga.id = age.account_id WHERE aga.classification_enum = ${subStatus}','Used For Getting Main GL Balances', 0, 1,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='MainGlBalances'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='subStatus'), 
 'subStatus');