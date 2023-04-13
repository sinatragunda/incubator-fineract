/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 April 2023 at 20:45
 */
package org.apache.fineract.presentation.screen.utility;

import org.apache.fineract.presentation.screen.data.ScreenData;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.apache.fineract.presentation.screen.service.ScreenCommandReciever;

import java.util.Collection;

public class ListScreensCommand implements IScreenCommand{

    private Collection<ScreenData> screenDataCollection;
    private ScreenCommandReciever screenCommandReciever;

    public ListScreensCommand(ScreenCommandReciever screenCommandReciever) {
        this.screenCommandReciever = screenCommandReciever;
    }

    @Override
    public void invoke() {
        screenDataCollection = screenCommandReciever.retrieveAll();
    }

    public Collection<ScreenData> getScreenDataCollection() {
        return screenDataCollection;
    }
}
