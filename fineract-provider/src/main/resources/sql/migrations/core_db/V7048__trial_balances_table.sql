INSERT INTO `stretchy_report` VALUES (null ,'TrialBalances', 'Table', '', 'Accounting',
    'SELECT if(age.type_enum=1,sum(age.amount),0) AS debit,if(age.type_enum=2,sum(age.amount),0) AS credit FROM acc_gl_journal_entry age WHERE age.office_id = ${officeId} GROUP BY age.type_enum','Used For Getting Trial Balances', 0, 1,1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='TrialBalances'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');

