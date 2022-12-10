/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:01
 */
package org.apache.fineract.portfolio.localref.domain;

import org.apache.fineract.infrastructure.codes.domain.Code;
import org.apache.fineract.infrastructure.codes.domain.CodeRepository;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepository;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.portfolio.localref.helper.LocalRefConstants;
import org.joda.time.LocalDate;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LocalRefAssembler {

    private final CodeRepository codeRepository ;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;

    @Autowired
    public LocalRefAssembler(CodeRepository codeRepository , OfficeRepositoryWrapper officeRepositoryWrapper) {
        this.codeRepository = codeRepository;
        this.officeRepositoryWrapper = officeRepositoryWrapper;
    }

    public LocalRef assembleFromJson(JsonCommand command){

        final String name = command.stringValueOfParameterNamed(LocalRefConstants.nameParam);
        final String description = command.stringValueOfParameterNamed(LocalRefConstants.descriptionParam);
        final Integer refTableInt = command.integerValueOfParameterNamed(LocalRefConstants.refTableParam);
        final Integer refValueTypeInt = command.integerValueOfParameterNamed(LocalRefConstants.refValueTypeParam);
        final Long codeId = command.longValueOfParameterNamed(LocalRefConstants.codeIdParam);

        final REF_TABLE refTable = REF_TABLE.fromInt(refTableInt);
        final REF_VALUE_TYPE refValueType = REF_VALUE_TYPE.fromInt(refValueTypeInt);

        final Code code[] = {null} ;

        Optional.ofNullable(codeId).ifPresent(e->{
            code[0] = codeRepository.findOne(codeId);
        });

        final Long officeId = command.longValueOfParameterNamed(ClientApiConstants.officeIdParamName);

        final Office office = officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);

        System.err.println("------------------did we find office ? "+office.getName());

        final LocalDate submittedDate = command.localDateValueOfParameterNamed(LocalRefConstants.submittedDateParam);

        LocalRef localRef = new LocalRef(name ,description ,refTable ,refValueType,code[0],null , DateUtils.fromLocalDate(submittedDate) ,office);
        return localRef;

    }
}
