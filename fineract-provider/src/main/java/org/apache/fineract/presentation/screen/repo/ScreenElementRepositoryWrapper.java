/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 March 2023 at 09:52
 */
package org.apache.fineract.presentation.screen.repo;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.presentation.screen.domain.ScreenElement;
import org.apache.fineract.presentation.screen.exceptions.ScreenElementNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScreenElementRepositoryWrapper {

    private ScreenElementRepository screenElementRepository;

    @Autowired
    public ScreenElementRepositoryWrapper(ScreenElementRepository screenElementRepository) {
        this.screenElementRepository = screenElementRepository;
    }

    public ScreenElement save(ScreenElement screenElement){
        screenElement = screenElementRepository.saveAndFlush(screenElement);
        return screenElement;
    }


    public ScreenElement findOneWithNotFoundDetection(Long id){

        ScreenElement screenElement = screenElementRepository.findOne(id);
        boolean has = OptionalHelper.isPresent(screenElement);
        if(!has){
            System.err.println("---------------why exception now found or bad argumenting ? "+has);
            throw new ScreenElementNotFoundException(id);
        }
        return screenElement;
    }

}
