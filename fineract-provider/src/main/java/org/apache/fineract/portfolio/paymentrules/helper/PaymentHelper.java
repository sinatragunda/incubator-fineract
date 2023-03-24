package org.apache.fineract.portfolio.paymentrules.helper;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.helper.LoanScheduleHelper;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsProduct;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.fineract.portfolio.savings.domain.SavingsProductProperties;
import org.joda.time.LocalDate;

import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountReadPlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentHelper{

	private SavingsAccountDomainService savingsAccountDomainService;
	private LoanReadPlatformService loanReadPlatformService ;
	private ShareAccountReadPlatformService shareAccountReadPlatformService;
	private LoanWritePlatformService loanWritePlatformService;
	private FromJsonHelper fromJsonHelper;
	private final LoanScheduleHelper loanScheduleHelper;

	@Autowired
	public PaymentHelper(SavingsAccountDomainService savingsAccountDomainService, LoanReadPlatformService loanReadPlatformService, ShareAccountReadPlatformService shareAccountReadPlatformService, LoanWritePlatformService loanWritePlatformService, FromJsonHelper fromJsonHelper ,LoanScheduleHelper loanScheduleHelper) {
		this.savingsAccountDomainService = savingsAccountDomainService;
		this.loanReadPlatformService = loanReadPlatformService;
		this.shareAccountReadPlatformService = shareAccountReadPlatformService;
		this.loanWritePlatformService = loanWritePlatformService;
		this.fromJsonHelper = fromJsonHelper;
		this.loanScheduleHelper = loanScheduleHelper;
	}

	@Transactional
	public void handlePayment(SavingsAccount savingsAccount , BigDecimal amount , LocalDate transactionDate){



		System.err.println("-------------handle payment now son --------------");
		Long accountId = savingsAccount.getId();
		SavingsProduct savingsProduct = savingsAccount.savingsProduct();
		SavingsProductProperties savingsProductProperties = savingsProduct.savingsProductProperties();
		boolean hasPaymentRule = hasPaymentRule(savingsProductProperties);

		BigDecimal amountSpentSoFar = BigDecimal.ZERO;

		Consumer<PaymentSequence> process = (e)->{
			switch (e.getPaymentCode()){
				case ACCOUNT_SWEEP:
					accountSweep(savingsAccount ,amount ,transactionDate);
					break;
			}
		};

		if(hasPaymentRule){
			PaymentRule rule = savingsProductProperties.paymentRule();
			Set<PaymentSequence> set = rule.getPaymentRuleSequence();
			set.stream().forEach(process);
		}
	}

	public BigDecimal accountSweep(SavingsAccount savingsAccount , BigDecimal amount , LocalDate transactionDate){
		savingsAccountDomainService.handleDepositLite(savingsAccount , transactionDate, amount,"Comment");
		return BigDecimal.ZERO ;
	}

	public JsonCommand loanTransactionCommand(LocalDate transactionDate ,BigDecimal amount){

		Map<String ,Object> map = new HashMap<>();
		map.put("transactionDate",transactionDate);
		map.put("transactionAmount",amount);
		map.put("locale","en");
		map.put("dateFormat" ,"dd MMM yyyy");

		return JsonCommandHelper.jsonCommand(fromJsonHelper ,map);
	}

	public BigDecimal loanRepayment(Client client ,PaymentSequence paymentSequence){

		// get client loans
		Long clientId  = client.getId();
		Long productId = Long.valueOf(paymentSequence.getValue());
		List<LoanAccountData> clientLoansByPortfolio = loanReadPlatformService.retrieveAllForClientAndProduct(clientId ,productId);

		//loanWritePlatformService.ma
		return BigDecimal.ZERO;
	}

	public boolean hasPaymentRule(SavingsProductProperties savingsProductProperties){
		PaymentRule paymentRule = savingsProductProperties.paymentRule();
		return OptionalHelper.isPresent(paymentRule);
	}




}