package org.apache.fineract.portfolio.paymentrules.service;


import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.repo.PaymentRuleRepository;
import org.apache.fineract.portfolio.paymentrules.repo.PaymentRuleRepositoryWrapper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentRulesPlatformWriteServiceImpl implements PaymentRulesPlatformWriteService {

    private JdbcTemplate jdbcTemplate;
    private PaymentRuleRepositoryWrapper paymentRuleRepositoryWrapper;
    private PaymentRuleDomainService paymentRuleDomainService;
    private ClientRepositoryWrapper clientRepositoryWrapper;
    private PaymentRuleChain paymentRuleChain;

    @Autowired
    public PaymentRulesPlatformWriteServiceImpl(final RoutingDataSource dataSource, final PaymentRuleRepositoryWrapper paymentRuleRepositoryWrapper, final PaymentRuleDomainService paymentRuleDomainService ,final ClientRepositoryWrapper clientRepositoryWrapper ,final PaymentRuleChain paymentRuleChain) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.paymentRuleRepositoryWrapper = paymentRuleRepositoryWrapper;
        this.paymentRuleDomainService = paymentRuleDomainService;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.paymentRuleChain = paymentRuleChain;
    }

    @Override
    public CommandProcessingResult handlePayment(Long paymentRuleId, Long clientId){

        Client client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId ,false);
        PaymentRule paymentRule = paymentRuleRepositoryWrapper.findWithNotFoundDetection(paymentRuleId);

        paymentRuleChain.pay(paymentRule ,client);
        return null;
    }

    @Override
    public CommandProcessingResult create(JsonCommand command) {

        // validate for update here and what not 
        final PaymentRule paymentRule = paymentRuleDomainService.assembleFromJson(command);

        this.paymentRuleRepositoryWrapper.save(paymentRule);

        return new CommandProcessingResultBuilder().withEntityId(paymentRule.getId()).build();
    }

    @Override
    public CommandProcessingResult update(Long id, JsonCommand command) {
        return null;
    }

}