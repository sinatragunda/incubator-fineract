/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:04
 */
package org.apache.fineract.presentation.screen.utility;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.presentation.screen.service.ScreenCommandReciever;

public class CreateScreenCommand implements IScreenCommand {

    private JsonCommand command;
    private ScreenCommandReciever screenCommandReciever;
    private CommandProcessingResult result ;

    public CreateScreenCommand(ScreenCommandReciever screenCommandReciever ,JsonCommand command){
        this.screenCommandReciever = screenCommandReciever;
        this.command = command;
    }

    @Override
    public void invoke() {
        System.err.println("--------create screen---------- in invoke");
        this.result = screenCommandReciever.createScreen(command);
    }

    public CommandProcessingResult getResult(){
        return result;
    }
}
