/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:03
 */
package org.apache.fineract.presentation.screen.utility;


import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.service.ScreenCommandReciever;

public class ViewScreenCommand implements IScreenCommand {

    private Long id ;
    private ScreenCommandReciever screenCommandReciever;
    private ScreenData screenData;

    public ViewScreenCommand(ScreenCommandReciever screenCommandReciever ,Long id){
        this.id = id ;
        this.screenCommandReciever = screenCommandReciever;
    }

    @Override
    public void invoke() {
        System.err.println("=========view screen");
        this.screenData = screenCommandReciever.retrieveOne(id);
    }

    public ScreenData getScreenData() {
        return screenData;
    }
}
