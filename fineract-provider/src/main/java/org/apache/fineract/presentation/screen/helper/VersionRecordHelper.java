/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 09:56
 */
package org.apache.fineract.presentation.screen.helper;

import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.service.VersionPortfolioCommandWritePlatformService;
import org.apache.fineract.presentation.screen.service.VersionRecordAssembler;
import org.apache.fineract.utility.helper.ListHelper;

import java.util.Set;
import java.util.function.Consumer;
import java.util.List ;
public class VersionRecordHelper {


    private static Consumer<ScreenElement> validateElements = (e)->{

        ScreenElement screenElement = e;
        /**
         * Modified 14/04/2023 at 1452
         * Value is the value from database and param is the user value
         * Though nothing happens to the return value as of yet
         */
        COMPARISON_TYPE comparisonType = screenElement.getComparisonType();

        System.err.println("-----------screen element object is "+e);

        System.err.println("-----------validating with comparison type "+comparisonType);
        comparisonType.validate(screenElement);
    };

    private static Consumer<ScreenElement> validateChildElements = (e)->{
        System.err.println("----------------validate child elements of "+e.getDisplayName());
        Set<ScreenElement> childElements = e.getChildElements();
        System.err.println("---------------child elements size is "+childElements.size());

        /**
         * Modified 02/05/2023 at 1601
         * Child Elements need to be fed the parameter from the parent screen element else they throw null errors
         */
        String parameter = e.getParameter();
        Consumer<ScreenElement> setParameterToChildElements = (c)-> c.setParameter(parameter);
        childElements.stream().forEach(setParameterToChildElements.andThen(validateElements));

    };

    public static void validateRecord(VersionPortfolioCommandWritePlatformService versionWritePlatformService , VersionRecordAssembler versionRecordAssembler , JsonCommand command){

        Screen screen = versionRecordAssembler.assembleFromJson(command);
        REF_TABLE refTable = screen.getRefTable();
        Set<ScreenElement> screenElementSet = screen.getScreenElementSet();
        screenElementSet.stream().forEach(validateElements.andThen(validateChildElements));
        List list = ListHelper.fromSet(screenElementSet);

        System.err.println("--------------------time to validate now son");
        VersionPortfolioCommandFactory.create(versionWritePlatformService ,list ,refTable);
    }
}
