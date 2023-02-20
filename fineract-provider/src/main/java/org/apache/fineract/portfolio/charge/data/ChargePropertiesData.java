/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 16 February 2023 at 03:03
 */
package org.apache.fineract.portfolio.charge.data;

import org.apache.fineract.portfolio.charge.domain.ChargeProperties;

public class ChargePropertiesData {

    private Long id ;
    private boolean commissionedCharge ;

    protected ChargePropertiesData(){}

    public ChargePropertiesData(Long id, boolean commissionedCharge) {
        this.id = id;
        this.commissionedCharge = commissionedCharge;
    }
}
