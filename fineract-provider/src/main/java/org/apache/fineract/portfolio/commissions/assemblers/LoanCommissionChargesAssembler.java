/*

    Created by Sinatra Gunda
    At 10:08 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.assemblers;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.apache.fineract.portfolio.commissions.repo.CommissionChargesRepository;
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


    private CommissionChargesRepository commissionChargesRepository;
    private final FromJsonHelper fromApiJsonHelper;
    private final PlatformSecurityContext platformSecurityContext;

    @Autowired
    public LoanCommissionChargesAssembler(final CommissionChargesRepository commissionChargesRepository, final FromJsonHelper fromApiJsonHelper , final PlatformSecurityContext platformSecurityContext){
        this.commissionChargesRepository = commissionChargesRepository;
        this.fromApiJsonHelper = fromApiJsonHelper ;
        this.platformSecurityContext = platformSecurityContext ;
    }

    public CommissionCharge assembleFrom(Long id){

        CommissionCharge commissionCharge = this.commissionChargesRepository.findOne(id);
        return commissionCharge;
    }

    public CommissionCharge assembleFrom(final JsonCommand jsonCommand){

        final JsonElement element = jsonCommand.parsedJson();
        final Long id = this.fromApiJsonHelper.extractLongNamed(LoanApiConstants.loanCommissionChargeParam, element);
        return assembleFrom(id);
    }
}
