/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 March 2023 at 02:59
 */
package org.apache.fineract.presentation.menu.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.presentation.menu.data.MenuItemData;
import org.apache.fineract.presentation.menu.domain.MenuItem;
import org.apache.fineract.presentation.menu.domain.ShortcutEntry;
import org.apache.fineract.presentation.menu.repo.MenuItemRepositoryWrapper;
import org.apache.fineract.presentation.menu.service.MenuItemReadPlatformService;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Notes
 * This class clashes with already implimented functionality of application table
 * This class does not use dynamic urls from aplication
 * If this class returns a hit then it reroutes to already created links in route page
 * However a loop will likely exists if the hit finds nothing then tries to fall to finding link in application and on and on again
 * A mechanism needs to be put into place to stop this
 */
@Service
public class CommandShortcutHelper {

    private MenuItemRepositoryWrapper menuItemRepositoryWrapper;

    @Autowired
    public CommandShortcutHelper(MenuItemRepositoryWrapper menuItemRepositoryWrapper) {
        this.menuItemRepositoryWrapper = menuItemRepositoryWrapper;
    }

    public ShortcutEntry shortcutEntry(String arg){

        String shortcut = token(arg ,0);
        System.err.println("=====================coalition of this arg "+arg+"==========and shortcut "+shortcut);
        MenuItem menuItem = menuItemRepositoryWrapper.findByShortcut(shortcut);
        boolean hasEntry = OptionalHelper.isPresent(menuItem);

        if(hasEntry){
            String url = buildUrl(menuItem ,arg);
            System.err.println("-----------------------main url to link is "+url);
            ShortcutEntry shortcutEntry = new ShortcutEntry(url ,true);
            return shortcutEntry;
        }
        return null ;
    }


    public static String token(String shortcut ,int index){

        String delim = " ";
        StringTokenizer token = new StringTokenizer(shortcut ,delim);
        List<String> data = new ArrayList<>();

        System.err.println("------------------counted tokens to ------"+token.countTokens());

        int tokenSize = token.countTokens();

        System.err.println("----------token size "+tokenSize+"----------------and index "+index);

        if(tokenSize >= (index+1)){
            System.err.println("-----------------compare these son for getting token at index for value at index---"+index);
            while(token.hasMoreTokens()) {
                String value = token.nextToken();
                data.add(value);
            }
            return data.get(index);
        }

        return null ;
    }

    public static String parameters(String shortcut){
        return token(shortcut ,1);
    }

    public static String buildUrl(MenuItem menuItem ,String shortcut){

        StringBuilder stringBuilder = new StringBuilder();

        String refTable = menuItem.getRefTable().getCode().toLowerCase();
        stringBuilder.append(refTable);
        stringBuilder.append("_");

        APPLICATION_ACTION action = menuItem.getApplicationAction();

        switch(action){
            case LANDING:
                stringBuilder.append(APPLICATION_ACTION.LIST.getCode());
                break;
            default:
                stringBuilder.append(menuItem.getApplicationAction().getValue());
                break;        
        }

        boolean hasDefaultParam = hasDefaultParam(menuItem);

        if(hasDefaultParam){

            String param = menuItem.getParam();
            stringBuilder = appendParam(stringBuilder ,param);
            return stringBuilder.toString();
        }
        /**
         * Modified 12/04/2023 at 1533 
         * Distinguish between default param and a user inserted param 
         */

        String param = parameters(shortcut);
        boolean hasParam = OptionalHelper.isPresent(param);

        if(hasParam){
            System.err.println("---------param is --------"+hasParam);
            stringBuilder = appendParam(stringBuilder ,param);
        }

        return stringBuilder.toString();
    
    }

    public static StringBuilder appendParam(StringBuilder stringBuilder ,String param){
        stringBuilder.append("/");
        stringBuilder.append(param);
        return stringBuilder;
    }


    public static boolean hasDefaultParam(MenuItem menuItem){
        boolean hasParam = OptionalHelper.isPresent(menuItem.getParam()); 
        return hasParam ;
    }
}
