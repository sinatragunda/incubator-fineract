package org.apache.fineract.portfolio.paymentrules.service;


import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.repo.PaymentRuleRepository;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PaymentRulesPlatformWriteServiceImpl implements PaymentRulesPlatformWriteService {

    private JdbcTemplate jdbcTemplate;
    private PaymentRuleRepository paymentRuleRepository;
    private PaymentRuleDomainService paymentRuleDomainService;

    @Autowired
    public PaymentRulesPlatformWriteServiceImpl(final RoutingDataSource dataSource, final PaymentRuleRepository paymentRuleRepository, final PaymentRuleDomainService paymentRuleDomainService) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.paymentRuleRepository = paymentRuleRepository;
        this.paymentRuleDomainService = paymentRuleDomainService;
    }

    @Override
    public CommandProcessingResult create(JsonCommand command) {

        // validate for update here and what not 
        final PaymentRule paymentRule = paymentRuleDomainService.assembleFromJson(command);

        this.paymentRuleRepository.save(paymentRule);

        return new CommandProcessingResultBuilder().withEntityId(paymentRule.getId()).build();
    }

    @Override
    public CommandProcessingResult update(Long id, JsonCommand command) {
        return null;
    }

}