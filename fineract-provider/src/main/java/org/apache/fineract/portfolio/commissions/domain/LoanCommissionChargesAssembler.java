/*

    Created by Sinatra Gunda
    At 10:08 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.domain;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.commissions.repo.LoanCommissionChargesRepository;
import org.apache.fineract.portfolio.loanaccount.api.LoanApiConstants;


import com.google.gson.JsonElement;


@Service
public class LoanCommissionChargesAssembler {


    private LoanCommissionChargesRepository loanCommissionChargesRepository ;
    private final FromJsonHelper fromApiJsonHelper;


    @Autowired
    public LoanCommissionChargesAssembler(LoanCommissionChargesRepository loanCommissionChargesRepository ,FromJsonHelper fromApiJsonHelper){
        this.loanCommissionChargesRepository = loanCommissionChargesRepository ;
        this.fromApiJsonHelper = fromApiJsonHelper ;
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
