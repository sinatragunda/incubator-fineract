/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 November 2022 at 03:54
 */
package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.helper.EntityMap;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetailRepository;
import org.apache.fineract.portfolio.paymenttype.domain.PaymentType;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;
import org.apache.fineract.portfolio.remittance.data.RxData;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.apache.fineract.portfolio.remittance.data.RxDealValidator;
import org.apache.fineract.portfolio.remittance.domain.RxDeal;
import org.apache.fineract.portfolio.remittance.domain.RxDealAsembler;
import org.apache.fineract.portfolio.remittance.domain.RxDealReceive;
import org.apache.fineract.portfolio.remittance.domain.RxDealTransaction;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;
import org.apache.fineract.portfolio.remittance.helper.RxDealDataHelper;
import org.apache.fineract.portfolio.remittance.repo.RxDealReceiveRepository;
import org.apache.fineract.portfolio.remittance.repo.RxDealRepository;
import org.apache.fineract.portfolio.remittance.repo.RxDealTransactionRepository;
import org.apache.fineract.portfolio.savings.data.SavingsAccountTransactionDTO;
import org.apache.fineract.portfolio.savings.data.TransactionDateData;
import org.apache.fineract.portfolio.savings.domain.*;
import org.apache.fineract.portfolio.savings.service.SavingsAccountWritePlatformService;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.joda.time.LocalDate ;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class RxDealWritePlatformServiceImpl implements RxDealWritePlatformService{


    private PlatformSecurityContext context;
    private JdbcTemplate jdbcTemplate ;
    private RxDealValidator rxDealValidator ;
    private RxDealAsembler rxDealAsembler;
    private RxDealDataHelper rxDealDataHelper;
    private SavingsAccountDomainService savingsAccountDomainService;
    private RxDealRepository rxDealRepository;
    private RxDealTransactionRepository rxDealTransactionRepository;
    private SavingsAccountAssembler savingsAccountAssembler;
    private RxDealReceiveRepository rxDealReceiveRepository;
    private BusinessEventNotifierService businessEventNotifierService;
    private SavingsAccountWritePlatformService savingsAccountWritePlatformService;
    private SavingsAccountTransactionRepository savingsAccountTransactionRepository;
    private FromJsonHelper fromJsonHelper;

    @Autowired
    public RxDealWritePlatformServiceImpl(final PlatformSecurityContext context , final RoutingDataSource routingDataSource ,final RxDealValidator rxDealValidator ,final RxDealAsembler rxDealAsembler ,final RxDealDataHelper rxDealDataHelper ,final SavingsAccountDomainService savingsAccountDomainService ,final RxDealRepository rxDealRepository ,final  RxDealTransactionRepository rxDealTransactionRepository ,final SavingsAccountAssembler savingsAccountAssembler ,final RxDealReceiveRepository rxDealReceiveRepository ,final BusinessEventNotifierService businessEventNotifierService ,final SavingsAccountWritePlatformService savingsAccountWritePlatformService ,final  SavingsAccountTransactionRepository savingsAccountTransactionRepository ,final FromJsonHelper fromJsonHelper){
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(routingDataSource);
        this.rxDealValidator = rxDealValidator;
        this.rxDealAsembler = rxDealAsembler;
        this.rxDealDataHelper = rxDealDataHelper;
        this.savingsAccountDomainService = savingsAccountDomainService;
        this.rxDealRepository = rxDealRepository ;
        this.rxDealTransactionRepository = rxDealTransactionRepository;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.rxDealReceiveRepository = rxDealReceiveRepository;
        this.businessEventNotifierService = businessEventNotifierService;
        this.savingsAccountWritePlatformService = savingsAccountWritePlatformService;
        this.savingsAccountTransactionRepository = savingsAccountTransactionRepository;
        this.fromJsonHelper = fromJsonHelper;
    }
    @Override
    public CommandProcessingResult createRxDeal(JsonCommand jsonCommand){

        //rxDealValidator.validateForCreate(jsonCommand.json());
        RxDealData rxDealData = rxDealAsembler.assembleFrom(jsonCommand);

        BigDecimal amount = rxDealData.getAmount();

        Client client = rxDealDataHelper.createOrGetClient(rxDealData);

        LocalDate transactionDate = DateUtils.toLocalDate(rxDealData.getTransactionDate());

        final SavingsAccount savingsAccount = savingsAccountAssembler.assembleFrom(rxDealData.getPayinAccountId());


        String note = String.format("Rx Deal from %s with id %d\n ",client.getDisplayName() ,client.getId());

        SavingsAccountTransaction savingsAccountTransaction =  savingsAccountDomainService.handleDepositLite(savingsAccount ,transactionDate ,amount ,note);
        RxDeal rxDeal = rxDealData.rxDeal();
        rxDeal.setClient(client);


        Optional.ofNullable(savingsAccountTransaction).ifPresent(e-> rxDeal.setSavingsAccountTransaction(e));
        Optional.ofNullable(client).ifPresent(e-> rxDeal.setClient(e));
        rxDeal.initDeal();


        //System.err.println("-------------charges paid list "+savingsAccountTransaction.getSavingsAccountChargesPaid().size());

        //BigDecimal totalCharges = savingsAccountTransaction.getSavingsAccountChargesPaid().stream().map(SavingsAccountChargePaidBy::getAmount).reduce(BigDecimal.ZERO ,BigDecimal::add);

        //System.err.println("-------------total chargs calcuated by this shit is "+totalCharges);

        /**
         * Added 07/11/2022 at 1413
         * Add updated amount since transaction amount will be
         */
        BigDecimal amountAfterCharges = savingsAccountTransaction.getAmount();
        BigDecimal totalCharges = amount.subtract(amountAfterCharges);

        System.err.println("--------------amount afer charges ---------"+amountAfterCharges);

        System.err.println("------------transaction should return amount it sent now then we set it to rxdeal ,new amount  "+totalCharges);

        rxDeal.setAmount(amountAfterCharges);
        rxDeal.setTotalCharges(totalCharges);

        rxDealRepository.save(rxDeal);
        Long rxDealId = rxDeal.getId();

        System.err.println("----------final transaction id is -----------"+rxDealId);
        //CommandProcessingResultBuilder.
        RxDealTransaction rxDealTransaction = new RxDealTransaction(rxDeal);

        System.err.println("rxDeal is is ============="+rxDealTransaction.getRxDeal().getId());

        rxDealTransactionRepository.save(rxDealTransaction);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BusinessEventNotificationConstants.BUSINESS_EVENTS.RX_SEND,
                EntityMap.construct(BusinessEventNotificationConstants.BUSINESS_ENTITY.RX, rxDealTransaction));


        return new CommandProcessingResultBuilder().withEntityId(rxDeal.getId()).withClientId(client.getId()).build();

    }

    @Override
    public CommandProcessingResult receiveRxDeal(final Long id ,JsonCommand jsonCommand) {


        System.err.println("-------------start updating this shit son "+id);

        // validate for update here
        RxDealReceive  rxDealReceive = rxDealAsembler.assemblerForReceive(id ,jsonCommand);
        RxDeal rxDeal = rxDealReceive.getRxDeal();
        Client client = rxDeal.getClient();

        BigDecimal amount = rxDeal.getAmount();

        System.err.println("this thing is dead set null ,taking client belonging to ? "+rxDealReceive.getSavingsAccountTransaction().getSavingsAccount().getClient().getDisplayName());

        SavingsAccount payoutSavingsAccount = rxDeal.getSavingsAccountTransaction().getSavingsAccount();

        System.err.println("------------------amount for deal is "+amount);

        System.err.println("------------perform function on savoings account -------"+payoutSavingsAccount.accountBalance());

        System.err.println("---------------------do we have payout account ? "+Optional.ofNullable(payoutSavingsAccount).isPresent());


        /**
         * To be checked 07/11/2022 at 1535
         * Get real LocalDate from assemble class
         * Date format error from converting to localdate from date giving us some pretty bad error thats resulting in interest calculation
         */
        LocalDate transactionDate = jsonCommand.localDateValueOfParameterNamed("transactionDate");

        //LocalDate transactionDate = DateUtils.toLocalDate(rxDealReceive.getTransactionDate());

        String note = String.format("Rx payout transaction for client %s to receiver %s",client.getDisplayName() ,rxDealReceive.getName());

        System.err.println("-----------------------note is -----------------"+note);


        System.err.println("-------------------transaction date in borched request is -------------------"+rxDealReceive.getTransactionDate());

        System.err.println("------------------the not botched date is "+transactionDate);

        Map map = rxDealAsembler.assembleForWithdrawal(rxDealReceive);
        map.put("note" ,note);

        jsonCommand = JsonCommandHelper.jsonCommand(fromJsonHelper ,map);

        SavingsAccount savingsAccount = rxDeal.getSavingsAccountTransaction().getSavingsAccount();
        Long savingsAccountId = savingsAccount.getId();

        CommandProcessingResult result = savingsAccountWritePlatformService.withdrawal(savingsAccountId ,jsonCommand);

        Long transactionId = result.resourceId();

        SavingsAccountTransaction savingsAccountTransaction = this.savingsAccountTransactionRepository.findOne(transactionId);

        rxDealReceive.setSavingsAccountTransaction(savingsAccountTransaction);
        rxDealReceive.setStatus(RX_DEAL_STATUS.CLOSED);

        rxDealReceiveRepository.save(rxDealReceive);

        rxDeal.setRxDealStatus(RX_DEAL_STATUS.CLOSED);
        rxDealRepository.save(rxDeal);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BusinessEventNotificationConstants.BUSINESS_EVENTS.RX_RECEIVE,
                EntityMap.construct(BusinessEventNotificationConstants.BUSINESS_ENTITY.RX, rxDealReceive));

        return  new CommandProcessingResultBuilder().withEntityId(rxDeal.getId()).build();

    }
}
