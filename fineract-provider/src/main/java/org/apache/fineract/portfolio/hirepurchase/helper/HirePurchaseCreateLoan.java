/*

    Created by Sinatra Gunda
    At 12:41 PM on 5/25/2022

*/
package org.apache.fineract.portfolio.hirepurchase.helper;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.hirepurchase.domain.HirePurchase;
import org.apache.fineract.portfolio.hirepurchase.repo.HirePurchaseRepository;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;

import java.util.Optional;

public class HirePurchaseCreateLoan {

    public static Long create(JsonCommand jsonCommand , Loan loan , HirePurchaseRepository hirePurchaseRepository){

        Long id[] = {null};
        String name = jsonCommand.stringValueOfParameterNamed("hirePurchase");
        Optional.ofNullable(name).ifPresent(e->{

            // if present then we have item to create now
            HirePurchase hirePurchase = new HirePurchase(loan ,name);
            hirePurchaseRepository.save(hirePurchase);
            id[0] = hirePurchase.getId();

        });

        return id[0];

    }
}
