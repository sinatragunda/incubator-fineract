/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 10:09
 */
package org.apache.fineract.presentation.screen.service;


import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.presentation.screen.helper.VersionRecordHelper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VersionWritePlatformServiceImpl implements VersionWritePlatformService{

    private PlatformSecurityContext context ;
    private VersionRecordAssembler versionRecordAssembler;
    private VersionPortfolioCommandWritePlatformService versionPortfolioCommandWritePlatformService;

    @Autowired
    public VersionWritePlatformServiceImpl(PlatformSecurityContext context, VersionRecordAssembler versionRecordAssembler ,VersionPortfolioCommandWritePlatformService versionPortfolioCommandWritePlatformService) {
        this.context = context;
        this.versionRecordAssembler = versionRecordAssembler;
        this.versionPortfolioCommandWritePlatformService = versionPortfolioCommandWritePlatformService;
    }

    @Override
    public CommandProcessingResult create(JsonCommand command) {

        System.err.println("----------validate----------------");
        VersionRecordHelper.validateRecord(versionPortfolioCommandWritePlatformService, versionRecordAssembler ,command);
        System.err.println("------------done validation -----------");
        CommandProcessingResult result = new CommandProcessingResultBuilder().withEntityId(5L).build();
        return result;
    }
}
