/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 09:56
 */
package org.apache.fineract.presentation.screen.helper;

import com.wese.component.defaults.enumerations.COMPARISON_TYPE;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.service.VersionRecordAssembler;

import java.util.Set;
import java.util.function.Consumer;

public class VersionRecordHelper {

    public static void validateRecord(VersionRecordAssembler versionRecordAssembler ,JsonCommand command){

        Screen screen = versionRecordAssembler.assembleFromJson(command);
        Set<ScreenElement> screenElementSet = screen.getScreenElementSet();

        Consumer<ScreenElement> validateElements = (e)->{

            ScreenElement screenElement = e;
            /**
             * Modified 14/04/2023 at 1452
             * Value is the value from database and param is the user value
             * Though nothing happens to the return value as of yet
             */
            COMPARISON_TYPE comparisonType = screenElement.getComparisonType();
            comparisonType.validate(screenElement);
        };

        Consumer<ScreenElement> validateChildElements = (e)->{
            System.err.println("----------------validate child elements of "+e.getDisplayName());
            Set<ScreenElement> childElements = e.getChildElements();
            System.err.println("---------------child elements size is "+childElements.size());
            childElements.stream().forEach(validateElements);
        };

        screenElementSet.stream().forEach(validateElements.andThen(validateChildElements));
    }
}
