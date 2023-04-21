/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 March 2023 at 02:03
 */
package org.apache.fineract.presentation.screen.utility;


import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.service.ScreenCommandReciever;

public class ViewScreenCommand implements IScreenCommand {

    private Long id ;
    private String name ;
    private ScreenCommandReciever screenCommandReciever;
    private ScreenData screenData;
    private Boolean viewByName = false;

    public ViewScreenCommand(ScreenCommandReciever screenCommandReciever ,Long id){
        this.id = id ;
        this.screenCommandReciever = screenCommandReciever;

    }


    public ViewScreenCommand(ScreenCommandReciever screenCommandReciever ,String name){
        this.name = name ;
        this.screenCommandReciever = screenCommandReciever;
        this.viewByName = true ;
    }

    @Override
    public void invoke() {

        System.err.println("=========view screen---"+viewByName);
        if(viewByName){
            this.screenData = screenCommandReciever.retrieveOneByName(name);
            return ;
        }
        this.screenData = screenCommandReciever.retrieveOne(id);
    }

    public ScreenData getScreenData() {
        return screenData;
    }
}
