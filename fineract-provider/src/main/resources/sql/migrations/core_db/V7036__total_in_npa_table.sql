
INSERT INTO `stretchy_report` VALUES (null ,'TotalInNPA', 'Table', '', 'Loan', 'SELECT ifnull(SUM(ml.total_outstanding_derived),0) AS due FROM m_loan ml JOIN m_client mc ON mc.id = ml.client_id WHERE ml.is_npa = false AND mc.office_id = ${officeId}
','Total Value of loans in NPA', 0, 1);

INSERT INTO stretchy_report_parameter ( report_id, parameter_id, report_parameter_name) 
VALUES ((select sr.id from stretchy_report sr where sr.report_name='TotalInNPA'),
 (select sp.id from stretchy_parameter sp where sp.parameter_variable='officeId'), 
 'officeId');