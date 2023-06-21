package org.apache.fineract.portfolio.paymentrules.helper;


import org.apache.fineract.helper.BigDecimalHelper;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.monetary.domain.MonetaryCurrency;
import org.apache.fineract.portfolio.account.service.AccountTransfersWritePlatformService;
import org.apache.fineract.portfolio.accounts.data.AccountData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.data.LoanAccountData;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepaymentScheduleInstallment;
import org.apache.fineract.portfolio.loanaccount.domain.LoanRepositoryWrapper;
import org.apache.fineract.portfolio.loanaccount.helper.LoanScheduleHelper;
import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.loanaccount.service.LoanWritePlatformService;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;
import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;
import org.apache.fineract.portfolio.savings.data.SavingsAccountData;
import org.apache.fineract.portfolio.savings.domain.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.fineract.portfolio.savings.exception.*;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.portfolio.shareaccounts.data.ShareAccountData;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;
import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountWritePlatformService;
import org.apache.fineract.utility.helper.FunctionsHelper;
import org.apache.fineract.utility.service.ServiceWrapper;
import org.apache.fineract.wese.helper.ComparatorUtility;


import org.apache.fineract.portfolio.shareaccounts.service.ShareAccountReadPlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;

import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.context.annotation.Lazy;
@Service
public class SavingsPaymentRuleHelper {

	private SavingsAccountDomainService savingsAccountDomainService;
	private LoanReadPlatformService loanReadPlatformService ;
	private ShareAccountReadPlatformService shareAccountReadPlatformService;
	private FromJsonHelper fromJsonHelper;
	private final LoanScheduleHelper loanScheduleHelper;
	private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
	private final LoanRepositoryWrapper loanRepositoryWrapper;
	private final AccountTransfersWritePlatformService accountTransfersWritePlatformService;
	private final ShareAccountWritePlatformService shareAccountWritePlatformService;

	private final Predicate<LoanAccountData> isActive = (e)-> e.isActive();


	@Autowired
	public SavingsPaymentRuleHelper(final ServiceWrapper serviceWrapper,final LoanWritePlatformService loanWritePlatformService , final ShareAccountWritePlatformService shareAccountWritePlatformService ,final LoanScheduleHelper loanScheduleHelper , final LoanRepositoryWrapper loanRepositoryWrapper ,final AccountTransfersWritePlatformService accountTransfersWritePlatformService) {
		this.loanReadPlatformService = serviceWrapper.getLoanReadPlatformService();
		this.shareAccountReadPlatformService = serviceWrapper.getShareAccountReadPlatformService();
		this.fromJsonHelper = new FromJsonHelper();
		this.loanScheduleHelper = loanScheduleHelper;
		this.savingsAccountReadPlatformService = serviceWrapper.getSavingsAccountReadPlatformService();
		this.loanRepositoryWrapper = loanRepositoryWrapper;
		this.savingsAccountDomainService = loanWritePlatformService.getSavingsAccountDomainService();
		this.accountTransfersWritePlatformService = accountTransfersWritePlatformService;
		this.shareAccountWritePlatformService = shareAccountWritePlatformService;

	}

	@Transactional
	public void handlePayment(SavingsAccountTransaction savingsAccountTransaction){

		//System.err.println("-------------handle payment now son --------------"+OptionalHelper.has(savingsAccountTransaction));

		SavingsAccount savingsAccount = savingsAccountTransaction.getSavingsAccount();
		Client client = savingsAccount.getClient();


		BigDecimal amountVar[] = {savingsAccountTransaction.getAmount()};

		SavingsProduct savingsProduct = savingsAccount.savingsProduct();

		SavingsProductProperties savingsProductProperties = savingsProduct.savingsProductProperties();

		boolean hasPaymentRule = hasPaymentRule(savingsProductProperties);

		//System.err.println("-----------------has payment rule son ? "+hasPaymentRule);

		if(hasPaymentRule) {

			LocalDate transactionDate = savingsAccountTransaction.getTransactionLocalDate();

			Consumer<PaymentSequence> process = (e) -> {

				PAYMENT_CODE paymentCode = e.getPaymentCode();

				switch (paymentCode) {
					case ACCOUNT_SWEEP:
						amountVar[0] = accountSweep(client ,e, amountVar[0],transactionDate);
						break;
					case LOAN_EARLY_REPAYMENT:
					case LOAN_REPAYMENT:
						amountVar[0] = loanRepayment(client ,savingsAccount ,e ,amountVar[0],transactionDate);
						break;

					case SHARE_REDEEM:
						redeemShares(client ,e ,transactionDate);
						break;
				}
			};

			PaymentRule rule = savingsProductProperties.paymentRule();
			Set<PaymentSequence> set = rule.getPaymentRuleSequence();
			
			//System.err.println("-----------------------------------payment sequence size is "+set.size());

			set.stream().forEach(process);
		}
	}

	/**
	 * Modified 16/06/2023
	 * Amount is transaction amount to deposit
	 * AmountSpentSoFar is total money spent ,it should always be less than amount or equals to
	 */
	private BigDecimal accountSweep(Client client ,PaymentSequence paymentSequence ,  BigDecimal amount, LocalDate transactionDate){

		boolean hasSufficientFunds = BigDecimalHelper.isFirstGreater(amount ,BigDecimal.ZERO);

		if(hasSufficientFunds) {

			String productParam = paymentSequence.getValue();
			// some exception here
			Boolean has = OptionalHelper.has(productParam);

			if (has){
				Long productId = Long.valueOf(productParam);
				Long clientId = client.getId();
				List<SavingsAccountData> savingsAccountList = savingsAccountReadPlatformService.retrieveAllForClientUnderPortfolio(clientId, productId);

				if (savingsAccountList.isEmpty()) {
					throw new SavingsAccountNotFoundException(client.getId());
				}

				SavingsAccountData savingsAccountData = savingsAccountList.stream().findFirst().get();
				Long savingsAccountId = savingsAccountData.getId();

				savingsAccountDomainService.handleDepositLite(savingsAccountId, transactionDate, amount, "Payment Rule Account Sweep");
				amount = amount.subtract(amount);
			}
			else{
				throw new AccountSweepingRequiresProduct();
			}
		}
		return amount ;
	}

	/**
	 *	 Added 19/06/2023 at 0811
	 *	 Transfer all account balances to a specific defuallt account belong to a client
	 */
	public BigDecimal balanceTransfer(Client client ,PaymentSequence paymentSequence ,LocalDate transactionDate){

		Long clientId = client.getId();

		String productParam = paymentSequence.getValue();
		// some exception here
		Boolean has = OptionalHelper.has(productParam);

		SavingsAccountData savingsAccountData = null ;

		Long primaryAccountId = null ;

		BigDecimal transferedBalance = BigDecimal.ZERO;

		System.err.println("--------------------product param is"+productParam);

		if(!has){
			primaryAccountId = client.savingsAccountId();

			System.err.println("------------primary account id is "+primaryAccountId);
			has = OptionalHelper.has(primaryAccountId);
			if(!has) {
				throw new ClientDefaultSavingsAccountNotFound();
			}
			savingsAccountData = savingsAccountReadPlatformService.retrieveOne(primaryAccountId);
		}
		else {
			Long productId = Long.valueOf(productParam);
			Collection<SavingsAccountData> savingsAccountDataCollection = savingsAccountReadPlatformService.retrieveAllForClientUnderPortfolio(clientId ,productId);
			savingsAccountData = savingsAccountDataCollection.stream().findFirst().get();
			primaryAccountId = savingsAccountData.getId();
		}

		client.updateSavingsAccount(primaryAccountId);

		Collection<SavingsAccountData> savingsAccountList = savingsAccountReadPlatformService.retrieveAllForLookup(clientId);

		SavingsAccountData finalSavingsAccountData = savingsAccountData;

		Consumer<SavingsAccountData> accountTransfer = (e)-> {
			BigDecimal accountBalance = e.getAccountBalance();
			boolean isDepositable = ComparatorUtility.isDepositableAmount(accountBalance);
			if(isDepositable) {
				// do some account transfer here son
				JsonCommand command = accountTransferPayload(client , finalSavingsAccountData,e ,transactionDate ,accountBalance);
				CommandProcessingResult result = accountTransfersWritePlatformService.create(command);
				if(result.hasCommandId()){
					//transferedBalance = transferedBalance.add(accountBalance);
				}
			}
		};

		Long finalPrimaryAccountId = primaryAccountId;

		Predicate<SavingsAccountData> notTransferAccount = (e)-> e.getId().equals(finalPrimaryAccountId);
		savingsAccountList.stream().filter(notTransferAccount.negate()).forEach(accountTransfer);

		return transferedBalance;
	}

	private BigDecimal loanRepayment(Client client ,SavingsAccount savingsAccount ,PaymentSequence paymentSequence ,BigDecimal amount, LocalDate transactionDate){

		List<LoanAccountData> clientLoans = getLoanAccountData(client, paymentSequence);

		System.err.println("------------client loans are "+clientLoans.size());

		PAYMENT_CODE paymentCode = paymentSequence.getPaymentCode();

		BigDecimal amountVar[] = {amount};

		Consumer<LoanAccountData> loanRepayment = (e)->{

			Long loanId = e.getId();

			Loan loan = loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);


			LoanRepaymentScheduleInstallment loanRepaymentScheduleInstallment = loanScheduleHelper.loanRepaymentScheduleInstallment(loan ,transactionDate ,paymentCode);

			Boolean hasInstallment = OptionalHelper.has(loanRepaymentScheduleInstallment);
			Boolean isDepositable = ComparatorUtility.isDepositableAmount(amountVar[0]);

			System.err.println("----------has installment ? "+hasInstallment);

			if(hasInstallment && isDepositable){

				MonetaryCurrency monetaryCurrency = loan.getCurrency();

				BigDecimal dueAmount = loanRepaymentScheduleInstallment.getDue(monetaryCurrency).getAmount();

				boolean hasSufficientFunds = BigDecimalHelper.isFirstGreater(amountVar[0] ,dueAmount);

				System.err.println("-------------has sufficient funds here ? "+hasSufficientFunds);

				/**
				 * Modified 170/06/2023
				 * If has not sufficient funds then do partial payment that is if amount is greater than 0 just pay it
				 */
				if(!hasSufficientFunds){
					dueAmount = amountVar[0];
					System.err.println("--------------------due amount is adjusted for dispariity"+dueAmount);
				}

				Long savingsAccountId = savingsAccount.getId();
				JsonCommand command = loanRepaymentPayloadViaTransfer(client ,savingsAccountId,loan, transactionDate , dueAmount);

				System.err.println("-----------loan repayment json is "+command);

				CommandProcessingResult result = accountTransfersWritePlatformService.create(command);

				boolean hasCommandId = result.hasCommandId();
				if(hasCommandId){
					amountVar[0] = amountVar[0].subtract(dueAmount).abs();
				}
			}
		};
		clientLoans.stream().filter(isActive).forEach(loanRepayment);
		return amountVar[0];
	}

	public BigDecimal closeLoansWithPayoff(Client client ,PaymentSequence paymentSequence ,LocalDate transactionDate){

		List<LoanAccountData> clientLoans = getLoanAccountData(client, paymentSequence);

		Long savingsAccountId = client.savingsAccountId();

		SavingsAccountData savingsAccountData = savingsAccountReadPlatformService.retrieveOne(savingsAccountId);

		System.err.println("------------client loans are "+clientLoans.size());

		BigDecimal accountBalance[] = {savingsAccountData.getAccountBalance()};

		Consumer<LoanAccountData> loanRepayment = (e)->{

			Long loanId = e.getId();

			Loan loan = loanRepositoryWrapper.findOneWithNotFoundDetection(loanId);

			Boolean isDepositable = ComparatorUtility.isDepositableAmount(accountBalance[0]);

			if(isDepositable){

				BigDecimal dueAmount = loan.getLoanSummary().getTotalOutstanding();

				boolean hasSufficientFunds = BigDecimalHelper.isFirstGreater(accountBalance[0] ,dueAmount);

				System.err.println("-------------has sufficient funds here ,block if funds are not sufficcient? "+hasSufficientFunds);

				/**
				 * Modified 17/06/2023
				 * If has not sufficient funds then do partial payment that is if amount is greater than 0 just pay it
				 */
				if(!hasSufficientFunds){
					throw new InsufficientAccountBalanceToPayOffLoan();
				}

				JsonCommand command = loanRepaymentPayloadViaTransfer(client ,savingsAccountId ,loan, transactionDate , dueAmount);

				System.err.println("-----------loan repayment json is "+command);

				CommandProcessingResult result = accountTransfersWritePlatformService.create(command);

				boolean hasCommandId = result.hasCommandId();
				if(hasCommandId){
					accountBalance[0] = accountBalance[0].subtract(dueAmount).abs();
				}
			}
		};
		clientLoans.stream().filter(isActive).forEach(loanRepayment);
		return accountBalance[0];
	}

	private List<LoanAccountData> getLoanAccountData(Client client, PaymentSequence paymentSequence) {

		Long clientId  = client.getId();

		boolean hasProductParam = hasProductParam(paymentSequence);

		List<LoanAccountData> clientLoans = new ArrayList<>();

		if(hasProductParam){
			Long productId = Long.valueOf(paymentSequence.getValue());
			clientLoans= loanReadPlatformService.retrieveAllForClientAndProduct(clientId ,productId);
		}
		else{
			clientLoans = loanReadPlatformService.retrieveAllForClient(clientId);
		}
		return clientLoans;
	}

	private JsonCommand loanRepaymentPayloadViaTransfer(Client client ,Long savingsAccountId ,Loan loan ,LocalDate transactionDate ,BigDecimal amount){

		Map<String ,Object> map = new HashMap<>();
		map.put("transferDate",transactionDate);
		map.put("transferAmount",amount);
		map.put("locale","en");
		map.put("dateFormat" ,"yyyy-MM-dd");
		map.put("fromClientId",client.getId());
		map.put("toClientId" ,client.getId());
		map.put("fromOfficeId" ,client.getOffice().getId());
		map.put("toOfficeId", client.getOffice().getId());
		map.put("fromAccountId", savingsAccountId);
		map.put("fromAccountType", 2);
		map.put("toAccountType", 1);
		map.put("toAccountId", loan.getId());
		map.put("transferDescription", "Payment Rule Payment");

		return JsonCommandHelper.jsonCommand(fromJsonHelper ,map);
	}


	private JsonCommand accountTransferPayload(Client client ,SavingsAccountData sourceAccount ,SavingsAccountData destinationAccount ,LocalDate transactionDate ,BigDecimal amount){

		Map<String ,Object> map = new HashMap<>();
		map.put("transferDate",transactionDate);
		map.put("transferAmount",amount);
		map.put("locale","en");
		map.put("dateFormat" ,"yyyy-MM-dd");
		map.put("fromClientId",client.getId());
		map.put("toClientId" ,client.getId());
		map.put("fromOfficeId" ,client.getOffice().getId());
		map.put("toOfficeId", client.getOffice().getId());
		map.put("fromAccountId", sourceAccount.getId());
		map.put("fromAccountType", 2);
		map.put("toAccountType", 2);
		map.put("toAccountId", destinationAccount.getId());
		map.put("transferDescription", "Account sweep transfer");

		return JsonCommandHelper.jsonCommand(fromJsonHelper ,map);
	}

	private boolean hasProductParam(PaymentSequence paymentSequence){

		String productParam = paymentSequence.getValue();
		Boolean hasProductParam = OptionalHelper.has(productParam);
		return hasProductParam;
	}


	public boolean hasPaymentRule(SavingsProductProperties savingsProductProperties){
		
		Boolean hasProperties = OptionalHelper.has(savingsProductProperties);
		if(hasProperties){
			PaymentRule paymentRule = savingsProductProperties.paymentRule();
			return OptionalHelper.isPresent(paymentRule);
		}
		return false ;
	}

	public BigDecimal redeemShares(Client client ,PaymentSequence paymentSequence ,LocalDate transactionDate) {

		boolean hasParam = hasProductParam(paymentSequence);
		Long clientId = client.getId();

		List<AccountData> shareAccountDataList = shareAccountReadPlatformService.retrieveForClient(clientId);

		if(hasParam){
			Long productId = Long.valueOf(paymentSequence.getValue());
			Predicate<AccountData> filter = (e)-> {
				ShareAccountData shareAccountData = (ShareAccountData) e;
				return shareAccountData.getProductId().equals(productId);
			};

			shareAccountDataList = shareAccountDataList.stream().filter(filter).collect(Collectors.toList());
		}

		Consumer<AccountData> closeShareAccount = (e)->{
			ShareAccountData shareAccountData = (ShareAccountData)e;
			Long shareAccoutId = shareAccountData.getId();

			Map<String ,Object> map = new HashMap<>();
			map.put("transferDate",transactionDate);
			map.put("locale","en");
			map.put("dateFormat" ,"yyyy-MM-dd");

			JsonCommand command = JsonCommandHelper.jsonCommand(fromJsonHelper ,map);
			shareAccountWritePlatformService.closeShareAccount(shareAccoutId ,command);
		};
		shareAccountDataList.stream().forEach(closeShareAccount);
		return BigDecimal.ZERO;
	}




}