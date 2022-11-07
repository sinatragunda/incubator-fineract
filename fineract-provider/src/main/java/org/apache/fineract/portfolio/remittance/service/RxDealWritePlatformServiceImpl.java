/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 November 2022 at 03:54
 */
package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.helper.EntityMap;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
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
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountDomainService;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.joda.time.LocalDate ;

import java.math.BigDecimal;
import java.util.Date;
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

    @Autowired
    public RxDealWritePlatformServiceImpl(final PlatformSecurityContext context , final RoutingDataSource routingDataSource ,final RxDealValidator rxDealValidator ,final RxDealAsembler rxDealAsembler ,final RxDealDataHelper rxDealDataHelper ,final SavingsAccountDomainService savingsAccountDomainService ,final RxDealRepository rxDealRepository ,final  RxDealTransactionRepository rxDealTransactionRepository ,final SavingsAccountAssembler savingsAccountAssembler ,final RxDealReceiveRepository rxDealReceiveRepository ,final BusinessEventNotifierService businessEventNotifierService){
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

        System.err.println("-------------transaction date is "+transactionDate);

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
    public CommandProcessingResult updateRxDeal(JsonCommand jsonCommand) {

        // validate for update here
        RxDealReceive  rxDealReceive = rxDealAsembler.assemblerForReceive(jsonCommand);
        RxDeal rxDeal = rxDealReceive.getRxDeal();
        Client client = rxDeal.getClient();
        BigDecimal amount = rxDeal.getAmount();
        SavingsAccount payoutSavingsAccount = rxDeal.getSavingsAccountTransaction().getSavingsAccount();
        LocalDate transactionDate = DateUtils.toLocalDate(rxDealReceive.getTransactionDate());

        String note = String.format("Rx payout transaction for client %s to receiver %s",client.getDisplayName() ,rxDealReceive.getName());

        SavingsAccountTransaction savingsAccountTransaction = savingsAccountDomainService.handleWithdrawalLite(payoutSavingsAccount ,transactionDate ,amount ,note);
        rxDealReceive.setSavingsAccountTransaction(savingsAccountTransaction);
        rxDealReceiveRepository.save(rxDealReceive);

        this.businessEventNotifierService.notifyBusinessEventWasExecuted(BusinessEventNotificationConstants.BUSINESS_EVENTS.RX_RECEIVE,
                EntityMap.construct(BusinessEventNotificationConstants.BUSINESS_ENTITY.CLIENT, rxDealReceive));

        CommandProcessingResult result  = new CommandProcessingResultBuilder().withEntityId(rxDealReceive.getId()).build();
        return result;
    }
}
