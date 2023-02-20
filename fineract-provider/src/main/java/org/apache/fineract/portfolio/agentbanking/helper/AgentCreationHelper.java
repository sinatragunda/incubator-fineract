/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 14 February 2023 at 13:12
 */
package org.apache.fineract.portfolio.agentbanking.helper;

import org.apache.fineract.infrastructure.codes.domain.CodeValue;
import org.apache.fineract.infrastructure.codes.domain.CodeValueRepositoryWrapper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.agentbanking.domain.Agent;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.savings.domain.*;
import org.apache.fineract.portfolio.savings.service.SavingsProductRepositoryWrapper;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class AgentCreationHelper {

    private ClientWritePlatformService clientWritePlatformService;
    private AppUserWritePlatformService appUserWritePlatformService;
    private PlatformSecurityContext context ;
    private CodeValueRepositoryWrapper codeValueRepositoryWrapper;
    private ClientRepositoryWrapper clientRepositoryWrapper;
    private SavingsProductRepositoryWrapper savingsProductRepositoryWrapper;
    private SavingsAccountRepository savingsAccountRepository;

    @Autowired
    public AgentCreationHelper(ClientWritePlatformService clientWritePlatformService, AppUserWritePlatformService appUserWritePlatformService, PlatformSecurityContext context, CodeValueRepositoryWrapper codeValueRepositoryWrapper, ClientRepositoryWrapper clientRepositoryWrapper ,SavingsProductRepositoryWrapper savingsProductRepositoryWrapper ,SavingsAccountRepository savingsAccountRepository) {
        this.clientWritePlatformService = clientWritePlatformService;
        this.appUserWritePlatformService = appUserWritePlatformService;
        this.context = context;
        this.codeValueRepositoryWrapper = codeValueRepositoryWrapper;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.savingsProductRepositoryWrapper = savingsProductRepositoryWrapper;
        this.savingsAccountRepository = savingsAccountRepository;
    }

    public Agent createAgent(Staff agent , JsonCommand command){


        AppUser createdByUser = context.authenticatedUser();

        Client client = createClientCommand(createdByUser ,agent ,command);

        System.err.println("----------------we have created client here "+client.getId());

        openDefaultAccount(createdByUser ,client ,command);

        return agent ;
    }

    private void openDefaultAccount(AppUser createdByUser ,Client client ,JsonCommand command){

        Long productId = command.longValueOfParameterNamed(ClientApiConstants.savingsProductIdParamName);
        String dateFormat = command.stringValueOfParameterNamed(ClientApiConstants.dateFormatParamName);
        String en = command.stringValueOfParameterNamed(ClientApiConstants.localeParamName);

        Locale locale = Locale.forLanguageTag(en);
        final DateTimeFormatter fmt = DateTimeFormat.forPattern(command.dateFormat()).withLocale(locale);

        CommandProcessingResult result = clientWritePlatformService.openSavingsAccountEx(client ,fmt ,productId);

    }

    private Client createClientCommand(AppUser createByUser ,Staff staff ,JsonCommand command){

        final String emailAddress = command.stringValueOfParameterNamed(ClientApiConstants.emailAddressParamName);
        final String mobileNo = command.stringValueOfParameterNamed(ClientApiConstants.mobileNoParamName);
        final Long savingsProductId = command.longValueOfParameterNamed(ClientApiConstants.savingsProductIdParamName);
        final Boolean isActive = command.booleanObjectValueOfParameterNamed(ClientApiConstants.activeParamName);

        CodeValue clientType = codeValueRepositoryWrapper.findOneByLabel("DDAC");
        boolean isStaff = true ;
        String tag = "#Agent";

        Client client = Client.agent(createByUser, staff ,emailAddress ,mobileNo ,savingsProductId ,clientType ,isStaff ,tag,isActive);
        clientRepositoryWrapper.save(client);
        return client;
    }
}
