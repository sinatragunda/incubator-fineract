/*

    Created by Sinatra Gunda
    At 11:32 AM on 8/2/2021

*/
package org.apache.fineract.portfolio.savings.helper;

import org.apache.fineract.portfolio.savings.domain.ProductBinding;
import org.apache.fineract.portfolio.savings.repo.ProductBindingRepository;

import java.util.List;

public class LoanFactoringHelper{


    public boolean loanMaximumReached(ProductBindingRepository productBindingRepository){

        // id is always one there
        //List<ProductBinding> productBindingList = productBindingRepository.findByProductBindingRootId(1);

        /// find all loans where product id matches ......

        return false ;


    }


}
