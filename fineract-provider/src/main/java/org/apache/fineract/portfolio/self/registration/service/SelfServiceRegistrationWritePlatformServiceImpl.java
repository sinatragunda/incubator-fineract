/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.self.registration.service;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.campaigns.sms.data.SmsProviderData;
import org.apache.fineract.infrastructure.campaigns.sms.domain.SmsCampaign;
import org.apache.fineract.infrastructure.campaigns.sms.service.SmsCampaignDropdownReadPlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.GmailBackedPlatformEmailService;
import org.apache.fineract.infrastructure.sms.domain.SmsMessage;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageRepository;
import org.apache.fineract.infrastructure.sms.domain.SmsMessageStatusType;
import org.apache.fineract.infrastructure.sms.scheduler.SmsMessageScheduledJobService;
import org.apache.fineract.organisation.staff.domain.Staff;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.exception.ClientNotFoundException;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.group.domain.Group;
import org.apache.fineract.portfolio.mailserver.domain.MailContent;
import org.apache.fineract.portfolio.mailserver.service.MailServerSenderFactory;
import org.apache.fineract.portfolio.self.registration.SelfServiceApiConstants;
import org.apache.fineract.portfolio.self.registration.domain.SelfServiceRegistration;
import org.apache.fineract.portfolio.self.registration.domain.SelfServiceRegistrationRepository;
import org.apache.fineract.portfolio.self.registration.exception.SelfServiceRegistrationNotFoundException;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.useradministration.domain.PasswordValidationPolicy;
import org.apache.fineract.useradministration.domain.PasswordValidationPolicyRepository;
import org.apache.fineract.useradministration.domain.Role;
import org.apache.fineract.useradministration.domain.RoleRepository;
import org.apache.fineract.useradministration.domain.UserDomainService;
import org.apache.fineract.useradministration.exception.RoleNotFoundException;
import org.apache.fineract.useradministration.service.AppUserReadPlatformService;
import org.apache.fineract.useradministration.service.AppUserWritePlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

@Service
public class SelfServiceRegistrationWritePlatformServiceImpl implements SelfServiceRegistrationWritePlatformService {

    private final SelfServiceRegistrationRepository selfServiceRegistrationRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final SelfServiceRegistrationReadPlatformService selfServiceRegistrationReadPlatformService;
    private final ClientRepositoryWrapper clientRepository;
    private final PasswordValidationPolicyRepository passwordValidationPolicy;
    private final UserDomainService userDomainService;
    private final GmailBackedPlatformEmailService gmailBackedPlatformEmailService;
    private final SmsMessageRepository smsMessageRepository;
    private SmsMessageScheduledJobService smsMessageScheduledJobService;
    private final SmsCampaignDropdownReadPlatformService smsCampaignDropdownReadPlatformService;
    private final AppUserReadPlatformService appUserReadPlatformService;
    private final RoleRepository roleRepository;
    private final MailServerSenderFactory mailServerSenderFactory;
    private final ClientReadPlatformService clientReadPlatformService;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final AppUserWritePlatformService appUserWritePlatformService;

    @Autowired
    public SelfServiceRegistrationWritePlatformServiceImpl(final SelfServiceRegistrationRepository selfServiceRegistrationRepository,
            final FromJsonHelper fromApiJsonHelper,
            final SelfServiceRegistrationReadPlatformService selfServiceRegistrationReadPlatformService,
            final ClientRepositoryWrapper clientRepository, final PasswordValidationPolicyRepository passwordValidationPolicy,
            final UserDomainService userDomainService, final GmailBackedPlatformEmailService gmailBackedPlatformEmailService,
            final SmsMessageRepository smsMessageRepository, SmsMessageScheduledJobService smsMessageScheduledJobService,
            final SmsCampaignDropdownReadPlatformService smsCampaignDropdownReadPlatformService,
            final AppUserReadPlatformService appUserReadPlatformService,final RoleRepository roleRepository,final MailServerSenderFactory mailServerSenderFactory ,final ClientReadPlatformService clientReadPlatformService ,final ClientRepositoryWrapper clientRepositoryWrapper ,final AppUserWritePlatformService appUserWritePlatformService) {
        this.selfServiceRegistrationRepository = selfServiceRegistrationRepository;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.selfServiceRegistrationReadPlatformService = selfServiceRegistrationReadPlatformService;
        this.clientRepository = clientRepository;
        this.passwordValidationPolicy = passwordValidationPolicy;
        this.userDomainService = userDomainService;
        this.gmailBackedPlatformEmailService = gmailBackedPlatformEmailService;
        this.smsMessageRepository = smsMessageRepository;
        this.smsMessageScheduledJobService = smsMessageScheduledJobService;
        this.smsCampaignDropdownReadPlatformService = smsCampaignDropdownReadPlatformService;
        this.appUserReadPlatformService = appUserReadPlatformService;
        this.roleRepository = roleRepository;
        this.mailServerSenderFactory = mailServerSenderFactory;
        this.clientReadPlatformService = clientReadPlatformService;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.appUserWritePlatformService = appUserWritePlatformService;
    }

    @Override
    public SelfServiceRegistration createRegistrationRequest(String apiRequestBodyAsJson) {
        
        Gson gson = new Gson();
        
        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("user");
        
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, apiRequestBodyAsJson,
                SelfServiceApiConstants.REGISTRATION_REQUEST_DATA_PARAMETERS);
        JsonElement element = gson.fromJson(apiRequestBodyAsJson.toString(), JsonElement.class);

        String accountNumber = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.accountNumberParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.accountNumberParamName).value(accountNumber).notNull().notBlank()
                .notExceedingLengthOf(100);

        String firstName = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.firstNameParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.firstNameParamName).value(firstName).notBlank()
                .notExceedingLengthOf(100);

        String lastName = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.lastNameParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.lastNameParamName).value(lastName).notBlank().notExceedingLengthOf(100);

        String username = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.usernameParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.usernameParamName).value(username).notBlank().notExceedingLengthOf(100);

        // validate password policy
        String password = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.passwordParamName, element);
        
        final PasswordValidationPolicy validationPolicy = this.passwordValidationPolicy.findActivePasswordValidationPolicy();
        final String regex = validationPolicy.getRegex();
        final String description = validationPolicy.getDescription();
        baseDataValidator.reset().parameter(SelfServiceApiConstants.passwordParamName).value(password)
                .matchesRegularExpression(regex, description).notExceedingLengthOf(100);

        String authenticationMode = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.authenticationModeParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.authenticationModeParamName).value(authenticationMode).notBlank()
                .isOneOfTheseStringValues(SelfServiceApiConstants.emailModeParamName, SelfServiceApiConstants.mobileModeParamName);

        String email = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.emailParamName, element);
        baseDataValidator.reset().parameter(SelfServiceApiConstants.emailParamName).value(email).notNull().notBlank()
                .notExceedingLengthOf(100);

        boolean isEmailAuthenticationMode = authenticationMode.equalsIgnoreCase(SelfServiceApiConstants.emailModeParamName);
        String mobileNumber = null;
        if (!isEmailAuthenticationMode) {
            mobileNumber = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.mobileNumberParamName, element);
            baseDataValidator.reset().parameter(SelfServiceApiConstants.mobileNumberParamName).value(mobileNumber).notNull()
                    .validatePhoneNumber();
        }
        validateForDuplicateUsername(username);

        throwExceptionIfValidationError(dataValidationErrors, accountNumber, firstName, lastName, mobileNumber, isEmailAuthenticationMode);

        String authenticationToken = randomAuthorizationTokenGeneration();
        Client client = this.clientRepository.getClientByAccountNumber(accountNumber);
        SelfServiceRegistration selfServiceRegistration = SelfServiceRegistration.instance(client, accountNumber, firstName, lastName,
                mobileNumber, email, authenticationToken, username, password);
        this.selfServiceRegistrationRepository.saveAndFlush(selfServiceRegistration);
        sendAuthorizationToken(selfServiceRegistration, isEmailAuthenticationMode);
        return selfServiceRegistration;

    }

    /**
     * Added 16/12/2022 at 0008
     */
    public CommandProcessingResult massRegistration(Long officeId){

        Collection<ClientData> clientDataCollection = clientReadPlatformService.retrieveAllForLookupByOfficeId(officeId);
        Collection<Client> clientCollection = new ArrayList();

        Consumer<ClientData> consumer = (e)->{
            Long clientId = e.getId();
            Client client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);
            clientCollection.add(client);
        };

        clientDataCollection.stream().forEach(consumer);

        Predicate<Client> userExists = (e)-> !userExists(e.emailAddress());

        Consumer<Client> register = (e)->{
            createSelfServiceUserEx(e);
        };

        clientCollection.stream().filter(userExists).forEach(register);
        return CommandProcessingResult.empty();
    }  

    // Added 25/09/2021 ,for creating self service user from client data 
    @Override
    public SelfServiceRegistration createSelfServiceUserEx(Client client){

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("user");
        
        String accountNumber = client.getAccountNumber();
        baseDataValidator.reset().parameter(SelfServiceApiConstants.accountNumberParamName).value(accountNumber).notNull().notBlank()
                .notExceedingLengthOf(100);

        String firstName = client.getFirstname();
        baseDataValidator.reset().parameter(SelfServiceApiConstants.firstNameParamName).value(firstName).notBlank()
                .notExceedingLengthOf(100);

        String lastName = client.getLastname();
        baseDataValidator.reset().parameter(SelfServiceApiConstants.lastNameParamName).value(lastName).notBlank().notExceedingLengthOf(100);

        String username = client.emailAddress();

        boolean hasEmailUsername = Optional.ofNullable(username).isPresent();

        if(!hasEmailUsername){
            return null ;
        }

        //System.err.println("---------------------username is "+username);

        baseDataValidator.reset().parameter(SelfServiceApiConstants.usernameParamName).value(username).notBlank().notExceedingLengthOf(100);

        final PasswordValidationPolicy validationPolicy = this.passwordValidationPolicy.findActivePasswordValidationPolicy();
        final String regex = validationPolicy.getRegex();
        final String description = validationPolicy.getDescription();

        String email = client.emailAddress();
        //String email = "treyviis@gmail.com";
        
        baseDataValidator.reset().parameter(SelfServiceApiConstants.emailParamName).value(email).notNull().notBlank()
                .notExceedingLengthOf(100);

        boolean isEmailAuthenticationMode = true;
        
        validateForDuplicateUsername(username);

//        if(throwErrorIfUserExists){
    //        throwExceptionIfValidationError(dataValidationErrors, accountNumber, firstName, lastName,null ,false);

  //      }

        String authenticationToken = randomAuthorizationTokenGeneration();

        SelfServiceRegistration selfServiceRegistration = SelfServiceRegistration.instance(client, accountNumber, firstName, lastName,
                null, email, authenticationToken, username, authenticationToken);

        boolean userExists = userExists(username);

        System.err.println("----------------------------user exists ? "+userExists);

        if(!userExists){
            System.err.println("----------------user doesnt exists ,highly unlikely will get here ");
            this.selfServiceRegistrationRepository.saveAndFlush(selfServiceRegistration);
            appUserWritePlatformService.createSelfServiceUser(client ,selfServiceRegistration);
            sendAuthorizationToken(selfServiceRegistration, isEmailAuthenticationMode);
        }
        return selfServiceRegistration;

    }

    public void validateForDuplicateUsername(String username) {
        boolean isDuplicateUserName = this.appUserReadPlatformService.isUsernameExist(username);
        if (isDuplicateUserName) {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(username)
                    .append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(),
                    SelfServiceApiConstants.usernameParamName, username);
        }
    }

    private boolean userExists(String username){
        boolean isDuplicateUserName = this.appUserReadPlatformService.isUsernameExist(username);
        return isDuplicateUserName;
    }

    public void sendAuthorizationToken(SelfServiceRegistration selfServiceRegistration, Boolean isEmailAuthenticationMode) {
        if (isEmailAuthenticationMode) {
            sendAuthorizationMail(selfServiceRegistration);
        } else {
            sendAuthorizationMessage(selfServiceRegistration);
        }
    }

    private void sendAuthorizationMessage(SelfServiceRegistration selfServiceRegistration) {
        Collection<SmsProviderData> smsProviders = this.smsCampaignDropdownReadPlatformService.retrieveSmsProviders();
        if (smsProviders.isEmpty()) { throw new PlatformDataIntegrityException("error.msg.mobile.service.provider.not.available",
                "Mobile service provider not available."); }
        Long providerId = (new ArrayList<>(smsProviders)).get(0).getId();
        
        final String message = "Hi  " + selfServiceRegistration.getFirstName() + "," + "\n"
                + "To create user, please use following details \n" + "Request Id : " + selfServiceRegistration.getId()
                + "\n Authentication Token : " + selfServiceRegistration.getAuthenticationToken();
        

        String externalId = null;

        Group group = null;
        Staff staff = null;
        SmsCampaign smsCampaign = null;
        boolean isNotification = false;
        SmsMessage smsMessage = SmsMessage.instance(externalId, group, selfServiceRegistration.getClient(), staff,
                SmsMessageStatusType.PENDING, message, selfServiceRegistration.getMobileNumber(), smsCampaign, isNotification);
        this.smsMessageRepository.save(smsMessage);
        this.smsMessageScheduledJobService.sendTriggeredMessage(new ArrayList<>(Arrays.asList(smsMessage)), providerId);
    }

    private void sendAuthorizationMail(SelfServiceRegistration selfServiceRegistration) {
        
        //final String subject = "Authorization token ";

        final String subject = "Self Service Membership";

        String body = "Hi  " + selfServiceRegistration.getFirstName() + "," + "\n" + "To create user, please use following details\n"
                + "Request Id : " + selfServiceRegistration.getId() + "\n Authentication Token : "
                + selfServiceRegistration.getAuthenticationToken();

        StringBuilder stringBuilder = new StringBuilder();

        URL url = null;
        try{
            url = new URL("https://nkwazi.portal.wesecbs.co");
        }
        catch (MalformedURLException m){
            System.err.println("----------------------------------"+m.getMessage());
        }


        stringBuilder.append(String.format("Dear %s %s,\n\n\n",selfServiceRegistration.getFirstName() ,selfServiceRegistration.getLastName()));
        stringBuilder.append("Happy Festive Season! Nkwazi Cooperative brings an exciting feature of real time experience with your membership. ");
        stringBuilder.append("Follow the link below or login from the website www.nkwazicoop.com to access an integrated member self- service.\n\n\n");
        stringBuilder.append(url.toString());
        stringBuilder.append("\n\n\nUse the login details provided, ensure to change your password at first login. Should you have challenges accessing your account, kindly contact us at customercare@nkwazicoop.com  or 260956797719");
        stringBuilder.append(String.format("\n\n\nUsername : %s\nPassword : %s\n",selfServiceRegistration.getUsername(), selfServiceRegistration.getPassword()));
        stringBuilder.append("\nNB: Keep your password confidential, regularly change your password.");
        

        body = stringBuilder.toString();

        final EmailDetail emailDetail = new EmailDetail(subject, body, selfServiceRegistration.getEmail(),
                selfServiceRegistration.getFirstName());
        //this.gmailBackedPlatformEmailService.sendDefinedEmail(emailDetail);

        MailContent mailContent = emailDetail.toMailContent();

        mailServerSenderFactory.sendMail(mailContent);

    }

    private void throwExceptionIfValidationError(final List<ApiParameterError> dataValidationErrors, String accountNumber,
            String firstName, String lastName, String mobileNumber, boolean isEmailAuthenticationMode) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
        boolean isClientExist = this.selfServiceRegistrationReadPlatformService.isClientExist(accountNumber, firstName, lastName,
                mobileNumber, isEmailAuthenticationMode);
        if (!isClientExist) { throw new ClientNotFoundException(); }
    }

    public static String randomAuthorizationTokenGeneration() {
        Integer randomPIN = (int) (Math.random() * 9000) + 1000;
        return randomPIN.toString();
    }

    @Override
    public AppUser createUser(String apiRequestBodyAsJson) {
        JsonCommand command = null;
        String username = null;
        try {
            Gson gson = new Gson();
            final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("user");
            this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, apiRequestBodyAsJson,
                    SelfServiceApiConstants.CREATE_USER_REQUEST_DATA_PARAMETERS);
            JsonElement element = gson.fromJson(apiRequestBodyAsJson.toString(), JsonElement.class);

            Long id = this.fromApiJsonHelper.extractLongNamed(SelfServiceApiConstants.requestIdParamName, element);
            baseDataValidator.reset().parameter(SelfServiceApiConstants.requestIdParamName).value(id).notNull().integerGreaterThanZero();
            
            command = JsonCommand.fromJsonElement(id, element ,fromApiJsonHelper);
            
            String authenticationToken = this.fromApiJsonHelper.extractStringNamed(SelfServiceApiConstants.authenticationTokenParamName,
                    element);
            baseDataValidator.reset().parameter(SelfServiceApiConstants.authenticationTokenParamName).value(authenticationToken).notBlank()
                    .notNull().notExceedingLengthOf(100);

            if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }

            SelfServiceRegistration selfServiceRegistration = this.selfServiceRegistrationRepository.getRequestByIdAndAuthenticationToken(
                    id, authenticationToken);
            
            if (selfServiceRegistration == null) { 
                throw new SelfServiceRegistrationNotFoundException(id, authenticationToken);
            }

            username = selfServiceRegistration.getUsername();
            Client client = selfServiceRegistration.getClient();
            final boolean passwordNeverExpire = true;
            final boolean isSelfServiceUser = true;
            final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("DUMMY_ROLE_NOT_USED_OR_PERSISTED_TO_AVOID_EXCEPTION"));
            final Set<Role> allRoles = new HashSet<>();
            Role role = this.roleRepository.getRoleByName(SelfServiceApiConstants.SELF_SERVICE_USER_ROLE);
            if(role != null){
                allRoles.add(role);
            }else{
                throw new RoleNotFoundException(SelfServiceApiConstants.SELF_SERVICE_USER_ROLE);
            }

            List<Client> clients = new ArrayList<>(Arrays.asList(client));
            
            User user = new User(selfServiceRegistration.getUsername(), selfServiceRegistration.getPassword(), authorities);
            AppUser appUser = new AppUser(client.getOffice(), user, allRoles, selfServiceRegistration.getEmail(), client.getFirstname(),
                    client.getLastname(), null, passwordNeverExpire, isSelfServiceUser, clients);
            
            this.userDomainService.create(appUser, true);
            return appUser;

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve, username);
            return null;
        } catch (final PersistenceException | AuthenticationServiceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleDataIntegrityIssues(command, throwable, dve, username);
            return null;
        }

    }

    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve, String username) {
        if (realCause.getMessage().contains("'username_org'")) {
            final StringBuilder defaultMessageBuilder = new StringBuilder("User with username ").append(username)
                    .append(" already exists.");
            throw new PlatformDataIntegrityException("error.msg.user.duplicate.username", defaultMessageBuilder.toString(), "username",
                    username);
        }
        throw new PlatformDataIntegrityException("error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }

}
