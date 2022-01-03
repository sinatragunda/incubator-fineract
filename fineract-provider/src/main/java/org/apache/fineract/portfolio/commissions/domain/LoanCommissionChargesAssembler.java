/*

    Created by Sinatra Gunda
    At 10:08 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.domain;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.commissions.repo.LoanCommissionChargesRepository;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;


import com.google.gson.JsonElement;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;


@Service
public class LoanCommissionChargesAssembler {


    private LoanCommissionChargesRepository loanCommissionChargesRepository ;
    private final FromJsonHelper fromApiJsonHelper;
    private final PlatformSecurityContext platformSecurityContext;

    @Autowired
    public LoanCommissionChargesAssembler(final LoanCommissionChargesRepository loanCommissionChargesRepository ,final FromJsonHelper fromApiJsonHelper ,final PlatformSecurityContext platformSecurityContext){
        this.loanCommissionChargesRepository = loanCommissionChargesRepository ;
        this.fromApiJsonHelper = fromApiJsonHelper ;
        this.platformSecurityContext = platformSecurityContext ;
    }

    public LoanCommissionCharge assembleFrom(Long id){

        LoanCommissionCharge loanCommissionCharge = this.loanCommissionChargesRepository.findOne(id);
        return loanCommissionCharge ;
    }

    public LoanCommissionCharge assembleFrom(final JsonCommand jsonCommand){

        final JsonElement element = jsonCommand.parsedJson();
        final Long id = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.loanCommissionChargeParam, element);
        return assembleFrom(id);
    }
}
