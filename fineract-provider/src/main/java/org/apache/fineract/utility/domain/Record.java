/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 05:26
 */
package org.apache.fineract.utility.domain;

import org.apache.fineract.portfolio.localref.data.LocalRefValueData;

import java.util.Collection;

public abstract class Record {

    private Collection<LocalRefValueData> localRefValueDataCollection;

    public void setLocalRefValueData(Collection<LocalRefValueData> localRefValueData) {
        this.localRefValueDataCollection = localRefValueData;
    }

    public abstract Long getId();
}
