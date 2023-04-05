/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 March 2023 at 03:01
 */
package org.apache.fineract.presentation.menu.domain;

public class ShortcutEntry {

    private String url ;
    private boolean isEntry;

    public ShortcutEntry(String url, boolean isEntry) {
        this.url = url;
        this.isEntry = isEntry;
    }
}
