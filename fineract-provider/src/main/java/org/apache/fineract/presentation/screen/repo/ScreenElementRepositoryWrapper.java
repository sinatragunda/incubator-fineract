/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 March 2023 at 09:52
 */
package org.apache.fineract.presentation.screen.repo;

import org.apache.fineract.presentation.screen.domain.ScreenElement;
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
}
