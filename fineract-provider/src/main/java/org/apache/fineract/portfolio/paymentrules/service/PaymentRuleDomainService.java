package org.apache.fineract.portfolio.paymentrules.service;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.paymentrules.api.PaymentRulesConstants;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;
import org.apache.fineract.portfolio.paymentrules.domain.PaymentSequence;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.fineract.portfolio.paymentrules.enumerations.PAYMENT_CODE;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement ;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

@Service
public class PaymentRuleDomainService{

	private OfficeRepositoryWrapper officeRepositoryWrapper;
	private FromJsonHelper fromJsonHelper;
	@Autowired
	public PaymentRuleDomainService(final OfficeRepositoryWrapper officeRepositoryWrapper ,final FromJsonHelper fromJsonHelper){
		this.officeRepositoryWrapper = officeRepositoryWrapper;
		this.fromJsonHelper = fromJsonHelper;
	}

	public PaymentRule assembleFromJson(JsonCommand command){

		String name = command.stringValueOfParameterNamed(PaymentRulesConstants.nameParam);
		String tag = command.locale();

		Locale locale = Locale.forLanguageTag(tag);

		Long officeId = command.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);

		final Office office = officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);

		final PaymentRule paymentRule = new PaymentRule(name ,office ,null);

		final Set<PaymentSequence> paymentSequenceSet = assemblePaymentSequenceFromJson(command ,paymentRule ,locale);

		paymentRule.setPaymentSequenceSet(paymentSequenceSet);

		return paymentRule ;
	}

	public Set<PaymentSequence> assemblePaymentSequenceFromJson(JsonCommand command ,PaymentRule paymentRule ,Locale locale){

		Set<PaymentSequence> paymentSequenceSet = new HashSet<>();
		JsonArray array = command.arrayOfParameterNamed(PaymentRulesConstants.paymentSequenceParam);
		for(int i = 0 ; i< array.size() ;i++){
			JsonObject obj = array.get(i).getAsJsonObject();
			JsonElement element = JsonCommandHelper.toJsonElement(obj.toString());

			String value = fromJsonHelper.extractStringNamed(PaymentRulesConstants.valueParam ,element);
			Integer sequenceNumber = fromJsonHelper.extractIntegerNamed(PaymentRulesConstants.sequenceNumberParam ,element ,locale);
			Integer paymentSequenceCodeInt = fromJsonHelper.extractIntegerNamed(PaymentRulesConstants.paymentSequenceCodeParam ,element ,locale);

			PAYMENT_CODE paymentCode = PAYMENT_CODE.fromInt(paymentSequenceCodeInt);
			REF_TABLE refTable = paymentCode.getRefTable();
			PaymentSequence paymentSequence = new PaymentSequence(refTable ,paymentCode,paymentRule ,value ,sequenceNumber);
			paymentSequenceSet.add(paymentSequence);
		}
		return paymentSequenceSet ;
	
	}
} 

