/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 17 April 2023 at 09:56
 */
package org.apache.fineract.presentation.screen.helper;

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
            //System.err.println("------------value is null might through error when getting or comparing ? ");
            Object value = e.getValue();
            Object param = e.getParameter();

            /**
             * Modified 14/04/2023 at 1452
             * Value is the value from database and param is the user value
             */
            boolean val = screenElement.getComparisonType().validate(screenElement ,value ,param);
            //System.err.println("-----------------done validating "+e.getDisplayName()+"--------with status "+val);
        };

        screenElementSet.stream().forEach(validateElements);
    }
}
