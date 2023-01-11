--
-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements. See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership. The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License. You may obtain a copy of the License at
--
-- http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied. See the License for the
-- specific language governing permissions and limitations
-- under the License.
--

CREATE TABLE `m_savings_account_charge` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`savings_account_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	`is_penalty` TINYINT(1) NOT NULL DEFAULT '0',
	`charge_time_enum` SMALLINT(5) NOT NULL,
	`due_for_collection_as_of_date` DATE NULL DEFAULT NULL,
	`charge_calculation_enum` SMALLINT(5) NOT NULL,
	`calculation_percentage` DECIMAL(19,6) NULL DEFAULT NULL,
	`calculation_on_amount` DECIMAL(19,6) NULL DEFAULT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	`amount_paid_derived` DECIMAL(19,6) NULL DEFAULT NULL,
	`amount_waived_derived` DECIMAL(19,6) NULL DEFAULT NULL,
	`amount_writtenoff_derived` DECIMAL(19,6) NULL DEFAULT NULL,
	`amount_outstanding_derived` DECIMAL(19,6) NOT NULL DEFAULT '0.000000',
	`is_paid_derived` TINYINT(1) NOT NULL DEFAULT '0',
	`waived` TINYINT(1) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`),
	INDEX `charge_id` (`charge_id`),
	INDEX `m_savings_account_charge_ibfk_2` (`savings_account_id`),
	CONSTRAINT `m_savings_account_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
	CONSTRAINT `m_savings_account_charge_ibfk_2` FOREIGN KEY (`savings_account_id`) REFERENCES `m_savings_account` (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `m_savings_product_charge` (
	`savings_product_id` BIGINT(20) NOT NULL,
	`charge_id` BIGINT(20) NOT NULL,
	PRIMARY KEY (`savings_product_id`, `charge_id`),
	INDEX `charge_id` (`charge_id`),
	CONSTRAINT `m_savings_product_charge_ibfk_1` FOREIGN KEY (`charge_id`) REFERENCES `m_charge` (`id`),
	CONSTRAINT `m_savings_product_charge_ibfk_2` FOREIGN KEY (`savings_product_id`) REFERENCES `m_savings_product` (`id`)
)
ENGINE=InnoDB DEFAULT CHARSET=utf8;



INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'CREATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'CREATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'CREATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'UPDATE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'UPDATE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'UPDATE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'DELETE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'DELETE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'DELETE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'WAIVE_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'WAIVE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'WAIVE_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'WAIVE', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'PAY_SAVINGSACCOUNTCHARGE', 'SAVINGSACCOUNTCHARGE', 'PAY', 0);
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`) VALUES ( 'portfolio', 'PAY_SAVINGSACCOUNTCHARGE_CHECKER', 'SAVINGSACCOUNTCHARGE', 'PAY', 0);



CREATE TABLE `m_savings_account_charge_paid_by` (
	`id` BIGINT(20) NOT NULL AUTO_INCREMENT,
	`savings_account_transaction_id` BIGINT(20) NOT NULL,
	`savings_account_charge_id` BIGINT(20) NOT NULL,
	`amount` DECIMAL(19,6) NOT NULL,
	PRIMARY KEY (`id`),
	INDEX `FK__m_savings_account_transaction` (`savings_account_transaction_id`),
	INDEX `FK__m_savings_account_charge` (`savings_account_charge_id`),
	CONSTRAINT `FK__m_savings_account_charge` FOREIGN KEY (`savings_account_charge_id`) REFERENCES `m_savings_account_charge` (`id`),
	CONSTRAINT `FK__m_savings_account_transaction` FOREIGN KEY (`savings_account_transaction_id`) REFERENCES `m_savings_account_transaction` (`id`)
)
COLLATE='utf8_general_ci'
ENGINE=InnoDB;


ALTER TABLE `m_savings_account`
	ADD COLUMN `total_fees_charge_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `total_withdrawal_fees_derived`,
	ADD COLUMN `total_penalty_charge_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `total_fees_charge_derived`;

delete from acc_product_mapping  where financial_account_type=10 and payment_type is not null;



/**Permissions for proposing and accepting a transfer**/
INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'PROPOSEANDACCEPTTRANSFER_CLIENT', 'CLIENT', 'PROPOSEANDACCEPTTRANSFER', 0);

INSERT INTO `m_permission` (`grouping`, `code`, `entity_name`, `action_name`, `can_maker_checker`)
VALUES ('portfolio', 'PROPOSEANDACCEPTTRANSFER_CLIENT_CHECKER', 'CLIENT', 'PROPOSEANDACCEPTTRANSFER', 0);


/***Delete previously set defaults**/
delete from acc_product_mapping where financial_account_type=10 and product_type=2;

/***Set the proper defaults**/
INSERT INTO `acc_product_mapping` (`gl_account_id`,`product_id`,`product_type`,`payment_type`,`charge_id`,`financial_account_type`)
select mapping.gl_account_id,mapping.product_id,mapping.product_type,mapping.payment_type,mapping.charge_id, 10
from acc_product_mapping mapping
where mapping.financial_account_type = 2 and mapping.product_type=2;

INSERT INTO `job` (`name`, `display_name`, `cron_expression`, `create_time`, `task_priority`, `group_name`, `previous_run_start_time`, `next_run_time`, `job_key`, `initializing_errorlog`, `is_active`, `currently_running`, `updates_allowed`, `scheduler_group`, `is_misfired`) VALUES ('Pay Due Savings Charges', 'Pay Due Savings Charges', '0 0 12 * * ?', '2013-09-23 00:00:00', 5, NULL, NULL, NULL, 'Pay Due Savings ChargesJobDetail1 _ DEFAULT', NULL, 1, 0, 1, 0, 0);



/***Set defaults for income from Penalties for existing Saving Products**/
INSERT INTO `acc_product_mapping` (`gl_account_id`,`product_id`,`product_type`,`payment_type`,`charge_id`,`financial_account_type`)
select mapping.gl_account_id,mapping.product_id,mapping.product_type,mapping.payment_type,mapping.charge_id, 5
from acc_product_mapping mapping
where mapping.financial_account_type = 4 and mapping.product_type=2;

ALTER TABLE `m_savings_account_charge`
	CHANGE COLUMN `due_for_collection_as_of_date` `charge_due_date` DATE NULL DEFAULT NULL AFTER `charge_time_enum`;

ALTER TABLE `m_savings_account_charge`
	ADD COLUMN `fee_on_month` SMALLINT(5) NULL DEFAULT NULL AFTER `charge_due_date`,
	ADD COLUMN `fee_on_day` SMALLINT(5) NULL DEFAULT NULL AFTER `fee_on_month`,
	ADD COLUMN `is_active` TINYINT(1) NOT NULL DEFAULT '1' AFTER `waived`;


delimiter //
CREATE PROCEDURE migrate_withdrwal_fees()
begin
	declare no_more_rows boolean default false;
	declare v_currency_code  VARCHAR(3);
	declare v_withdrawal_fee_type_enum  SMALLINT(5);
	declare v_withdrawal_fee_amount_charge_def DECIMAL(19,6);
	declare v_account_id BIGINT(20);
	declare v_withdrawal_fee_amount DECIMAL(19,6);
	declare t_calculation_percentage DECIMAL(19,6);
	declare t_calculation_on_amount DECIMAL(19,6);
	declare t_withdrawal_fee_name VARCHAR(100);

	declare t_charge_id BIGINT(20);
	declare t_savings_charge_id BIGINT(20);

	-- savings transaction variables
	declare v_savings_transaction_id BIGINT(20);
	declare v_transaction_amount DECIMAL(19,6);


	declare cursor1 cursor for
      select sa.currency_code, sa.withdrawal_fee_type_enum, sa.withdrawal_fee_amount
      from m_savings_account sa where sa.withdrawal_fee_amount is not null and sa.withdrawal_fee_amount > 0 group by sa.currency_code, sa.withdrawal_fee_type_enum;

	declare cursor2 cursor for
      select sa.id, sa.withdrawal_fee_amount from m_savings_account sa where sa.currency_code=v_currency_code and sa.withdrawal_fee_type_enum=v_withdrawal_fee_type_enum and sa.withdrawal_fee_amount is not null and sa.withdrawal_fee_amount > 0;

    declare cursor3 cursor for
      select sat.id, sat.amount from m_savings_account_transaction sat
      where sat.savings_account_id=v_account_id and sat.transaction_type_enum=4;

	declare continue handler for not found
      set no_more_rows := true;

	open cursor1;
    LOOP1: loop
	fetch cursor1 into v_currency_code, v_withdrawal_fee_type_enum, v_withdrawal_fee_amount_charge_def;
	if no_more_rows then
		close cursor1;
		leave LOOP1;
	end if;
	-- set withdrawal fee name
	if(v_withdrawal_fee_type_enum = 1) then
		set t_withdrawal_fee_name = CONCAT('Withdrawal fee-Flat-',v_currency_code);
	else
		set t_withdrawal_fee_name = CONCAT('Withdrawal fee-Percentage-',v_currency_code);
	end if;

	-- get charge id if already exists
	set t_charge_id = (select id from m_charge where name = t_withdrawal_fee_name);

	if t_charge_id is null then
		-- add withdrawal fee to charges
		INSERT INTO `m_charge` (`name`, `currency_code`, `charge_applies_to_enum`, `charge_time_enum`, `charge_calculation_enum`, `charge_payment_mode_enum`, `amount`, `is_penalty`, `is_active`, `is_deleted`) VALUES (t_withdrawal_fee_name , v_currency_code, 2, 5, v_withdrawal_fee_type_enum, 0, v_withdrawal_fee_amount_charge_def, 0, 1, 0);

		-- get inserted charge id
		set t_charge_id = last_insert_id();
	end if;

	open cursor2;
        LOOP2: loop
		fetch cursor2 into v_account_id, v_withdrawal_fee_amount;
		if no_more_rows then
		set no_more_rows := false;
		close cursor2;
		leave LOOP2;
            end if;

            if (v_withdrawal_fee_type_enum=1) then
		set t_calculation_percentage = NULL;
		set t_calculation_on_amount = NULL;
            else
	            set t_calculation_percentage = v_withdrawal_fee_amount;
	            set v_withdrawal_fee_amount = 0;
	            set t_calculation_on_amount = 0;
            end if;

            if not exists (select id from m_savings_account_charge sac where
		sac.savings_account_id=v_account_id and sac.charge_id=t_charge_id and sac.charge_time_enum=5) then

	            -- attach withdrawal charge to savings
	            INSERT INTO `m_savings_account_charge` (`savings_account_id`, `charge_id`, `is_penalty`, `charge_time_enum`, `charge_due_date`, `fee_on_month`, `fee_on_day`, `charge_calculation_enum`, `calculation_percentage`, `calculation_on_amount`, `amount`, `amount_paid_derived`, `amount_waived_derived`, `amount_writtenoff_derived`, `amount_outstanding_derived`, `is_paid_derived`, `waived`, `is_active`) VALUES (v_account_id, t_charge_id, 0, 5, NULL, NULL, NULL, v_withdrawal_fee_type_enum, t_calculation_percentage, t_calculation_on_amount, v_withdrawal_fee_amount, NULL, NULL, NULL, 0.000000, 0, 0, 1);

	            -- set savings account charge id
	            set t_savings_charge_id = last_insert_id();

	        else

			set t_savings_charge_id = (select id from m_savings_account_charge sac where sac.savings_account_id=v_account_id and sac.charge_id=t_charge_id and sac.charge_time_enum=5);

	        end if;


            open cursor3;
            LOOP3: loop
		fetch cursor3 into v_savings_transaction_id, v_transaction_amount;

			if no_more_rows then
			set no_more_rows := false;
			close cursor3;
			leave LOOP3;
	            end if;

	            if not exists (select id from m_savings_account_charge_paid_by sacp where
		sacp.savings_account_transaction_id=v_savings_transaction_id and sacp.savings_account_charge_id=t_savings_charge_id) then

		            -- insert a record into savings account charge paid by
		            INSERT INTO `m_savings_account_charge_paid_by` (`savings_account_transaction_id`, `savings_account_charge_id`, `amount`) VALUES(v_savings_transaction_id, t_savings_charge_id, v_transaction_amount);

		        end if;

	        end loop LOOP3;
        end loop LOOP2;
    end loop LOOP1;
end //

CREATE PROCEDURE migrate_annual_fees()
begin
	declare no_more_rows boolean default false;
	declare v_currency_code  VARCHAR(3);
	declare v_annual_fee_amount_charge_def DECIMAL(19,6);
	declare v_account_id BIGINT(20);
	declare v_annual_fee_amount DECIMAL(19,6);
	declare v_annual_fee_on_month SMALLINT(5);
	declare v_annual_fee_on_day SMALLINT(5);
	declare v_annual_fee_next_due_date DATE;
	declare t_annual_fee_name VARCHAR(100);
	declare t_charge_id BIGINT(20);
	declare t_savings_charge_id BIGINT(20);

	-- savings transaction variables
	declare v_savings_transaction_id BIGINT(20);
	declare v_transaction_amount DECIMAL(19,6);


	declare cursor1 cursor for
      select sa.currency_code, sa.annual_fee_amount
      from m_savings_account sa where sa.annual_fee_amount is not null and sa.annual_fee_on_month is not null and sa.annual_fee_on_day is not null group by sa.currency_code;

	declare cursor2 cursor for
      select sa.id, sa.annual_fee_amount, sa.annual_fee_on_month, sa.annual_fee_on_day, sa.annual_fee_next_due_date from m_savings_account sa where sa.currency_code=v_currency_code and sa.annual_fee_amount is not null and sa.annual_fee_on_month is not null and sa.annual_fee_on_day is not null;

    declare cursor3 cursor for
      select sat.id, sat.amount from m_savings_account_transaction sat
      where sat.savings_account_id=v_account_id and sat.transaction_type_enum=5;

	declare continue handler for not found
      set no_more_rows := true;

	open cursor1;
    LOOP1: loop
	fetch cursor1 into v_currency_code, v_annual_fee_amount_charge_def;

	if no_more_rows then
		close cursor1;
		leave LOOP1;
	end if;

	-- set annual fee name
	set t_annual_fee_name = CONCAT('Annual fee - ',v_currency_code);

	-- get charge id if already exists
	set t_charge_id = (select id from m_charge where name = t_annual_fee_name);

	if t_charge_id is null then

		-- add annual fee to charges
		INSERT INTO `m_charge` (`name`, `currency_code`, `charge_applies_to_enum`, `charge_time_enum`, `charge_calculation_enum`, `charge_payment_mode_enum`, `amount`, `is_penalty`, `is_active`, `is_deleted`) VALUES (t_annual_fee_name, v_currency_code, 2, 6, 1, 0, v_annual_fee_amount_charge_def, 0, 1, 0);

		-- get inserted charge id
		set t_charge_id = last_insert_id();

	end if;

	open cursor2;
        LOOP2: loop
		fetch cursor2 into v_account_id, v_annual_fee_amount, v_annual_fee_on_month, v_annual_fee_on_day, v_annual_fee_next_due_date;

		if no_more_rows then
		set no_more_rows := false;
		close cursor2;
		leave LOOP2;
            end if;

            if not exists (select id from m_savings_account_charge sac where
		sac.savings_account_id=v_account_id and sac.charge_id=t_charge_id and sac.charge_time_enum=6) then

	            -- attach annual charge to savings
	            INSERT INTO `m_savings_account_charge` (`savings_account_id`, `charge_id`, `is_penalty`, `charge_time_enum`, `charge_due_date`, `fee_on_month`, `fee_on_day`, `charge_calculation_enum`, `calculation_percentage`, `calculation_on_amount`, `amount`, `amount_paid_derived`, `amount_waived_derived`, `amount_writtenoff_derived`, `amount_outstanding_derived`, `is_paid_derived`, `waived`, `is_active`) VALUES (v_account_id, t_charge_id, 0, 6, v_annual_fee_next_due_date, v_annual_fee_on_month, v_annual_fee_on_day, 1, NULL, NULL, v_annual_fee_amount, NULL, NULL, NULL, v_annual_fee_amount, 0, 0, 1);

	            -- set savings account charge id
	            set t_savings_charge_id = last_insert_id();

	        else

			set t_savings_charge_id = (select id from m_savings_account_charge sac where sac.savings_account_id=v_account_id and sac.charge_id=t_charge_id and sac.charge_time_enum=6);

	        end if;

            open cursor3;
            LOOP3: loop
		fetch cursor3 into v_savings_transaction_id, v_transaction_amount;

			if no_more_rows then
			set no_more_rows := false;
			close cursor3;
			leave LOOP3;
	            end if;

	            if not exists (select id from m_savings_account_charge_paid_by sacp where
		sacp.savings_account_transaction_id=v_savings_transaction_id and sacp.savings_account_charge_id=t_savings_charge_id) then

		            -- insert a record into savings account charge paid by
		            INSERT INTO `m_savings_account_charge_paid_by` (`savings_account_transaction_id`, `savings_account_charge_id`, `amount`) VALUES(v_savings_transaction_id, t_savings_charge_id, v_transaction_amount);

		        end if;

	        end loop LOOP3;
        end loop LOOP2;
    end loop LOOP1;
end //

delimiter ;

call migrate_withdrwal_fees();
call migrate_annual_fees();

drop procedure if exists migrate_annual_fees;
drop procedure if exists migrate_withdrwal_fees;


ALTER TABLE `m_savings_account`
	DROP COLUMN `annual_fee_amount`,
	DROP COLUMN `annual_fee_on_month`,
	DROP COLUMN `annual_fee_on_day`,
	DROP COLUMN `annual_fee_next_due_date`,
	DROP COLUMN `withdrawal_fee_amount`,
	DROP COLUMN `withdrawal_fee_type_enum`;

ALTER TABLE `m_savings_product`
	DROP COLUMN `annual_fee_amount`,
	DROP COLUMN `annual_fee_on_month`,
	DROP COLUMN `annual_fee_on_day`;

/***Set defaults for income from Penalties for existing Saving Products**/
ALTER TABLE `m_loan_transaction`
	ADD COLUMN `overpayment_portion_derived` DECIMAL(19,6) NULL DEFAULT NULL AFTER `penalty_charges_portion_derived`;

/**Add dummy liability account if organization already has a loan product with accounting enabled**/
INSERT INTO `acc_gl_account` (`name`, `hierarchy`, `gl_code`,`account_usage`, `classification_enum`,`description`)
select 'Loan Overpayments (Temp)', '.', '22000-Temp', 1, 2,'Temporary account to track Loan overpayments Liabilities'
FROM m_product_loan WHERE accounting_type != 1
limit 1;


/**Map a liability account for every loan which has accounting enabled**/
INSERT INTO `acc_product_mapping` (`gl_account_id`,`product_id`,`product_type`,`financial_account_type`)
select (select max(id) from acc_gl_account where classification_enum=2 and account_usage=1 LIMIT 1), mapping.product_id, mapping.product_type, 11
from acc_product_mapping mapping
where mapping.financial_account_type = 2 and mapping.product_type=1;


