/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 March 2023 at 09:50
 */
package org.apache.fineract.presentation.screen.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.presentation.screen.domain.Screen;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreenRepositoryWrapper {

    private final ScreenRepository screenRepository;

    @Autowired
    public ScreenRepositoryWrapper(final ScreenRepository screenRepository) {
        this.screenRepository =screenRepository;
    }

    public Screen findOneWithNotFoundDetection(Long id){
        Screen screen = screenRepository.findOne(id);
        boolean has = OptionalHelper.isPresent(screen);
        if(has){

        }
        return screen;
    }

    public Screen save(Screen screen){
        screenRepository.saveAndFlush(screen);
        return screen;
    }
}
