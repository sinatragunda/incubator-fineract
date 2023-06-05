package org.apache.fineract.presentation.menu.service;


import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.portfolio.localref.enumerations.APPLICATION_ACTION;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.helper.LocalRefConstants;
import org.apache.fineract.presentation.menu.domain.Menu;
import org.apache.fineract.presentation.menu.domain.MenuItem;
import org.apache.fineract.presentation.menu.enumerations.MENU_PLACEMENT;
import org.apache.fineract.presentation.menu.helper.MenuConstants;
import org.apache.fineract.presentation.menu.repo.MenuItemRepositoryWrapper;
import org.apache.fineract.presentation.menu.repo.MenuRepositoryWrapper;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.wese.helper.JsonCommandHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class MenuAssembler {

 	private FromJsonHelper fromJsonHelper ;
 	private MenuItemRepositoryWrapper menuItemRepositoryWrapper;
	 private MenuRepositoryWrapper menuRepositoryWrapper;

	 @Autowired
	public MenuAssembler(FromJsonHelper fromJsonHelper, MenuItemRepositoryWrapper menuItemRepositoryWrapper, MenuRepositoryWrapper menuRepositoryWrapper) {
		this.fromJsonHelper = fromJsonHelper;
		this.menuItemRepositoryWrapper = menuItemRepositoryWrapper;
		this.menuRepositoryWrapper = menuRepositoryWrapper;
	}

	public List<MenuItem> menuItemsFromJson(JsonCommand command){
		JsonArray array = command.arrayOfParameterNamed(MenuConstants.menuItemsParam);
 		List<MenuItem> menuItems = new ArrayList<>();
 		for(JsonElement element : array){
			JsonObject jsonObject = element.getAsJsonObject();
 			JsonCommand arg = JsonCommandHelper.jsonCommand(fromJsonHelper, jsonObject.toString());
 			MenuItem item = menuItemFromJson(arg);
 			menuItems.add(item);
 		}
 		return menuItems;
 	}

 	public List<MenuItem> menuItemsFromJsonIdKeys(JsonCommand command){

 		 JsonArray array = command.arrayOfParameterNamed(MenuConstants.menuItemsParam);
 		 List<MenuItem> menuItems = new ArrayList<>();

		 boolean has = OptionalHelper.has(array);

		 if(has) {
			 for(JsonElement element : array) {
				 JsonObject jsonObject = element.getAsJsonObject();
				 JsonCommand arg = JsonCommandHelper.jsonCommand(fromJsonHelper, jsonObject.toString());
				 Long id = arg.longValueOfParameterNamed("id");
				 MenuItem menuItem = this.menuItemRepositoryWrapper.findOneWithNotFoundDetection(id);
				 menuItems.add(menuItem);
			 }
		 }

 		return menuItems;
 	}


 	public MenuItem menuItemFromJson(JsonCommand command){

 		Locale locale = Locale.forLanguageTag("en");

 		final String name =  command.stringValueOfParameterNamed(GenericConstants.nameParam);
 		final String shortcut = command.stringValueOfParameterNamed(MenuConstants.shortcutParam);
 		final String param = command.stringValueOfParameterNamed(MenuConstants.paramParam);
 		final Integer refTableInt = command.integerValueOfParameterNamed(LocalRefConstants.refTableParam ,locale);
 		final Integer applicationActionInt = command.integerValueOfParameterNamed(MenuConstants.applicationActionParam,locale);

 		final REF_TABLE refTable = REF_TABLE.fromInt(refTableInt);
 		final APPLICATION_ACTION action = (APPLICATION_ACTION) EnumTemplateHelper.fromInt(APPLICATION_ACTION.values(), applicationActionInt);
 		MenuItem menuItem = new MenuItem(name ,action ,refTable ,shortcut ,param);
 		return menuItem; 
 	}


 	public Menu menuFromJson(JsonCommand command){

 		Locale locale = Locale.forLanguageTag("en");

 		final String name =  command.stringValueOfParameterNamed(GenericConstants.nameParam);
 		final Integer menuPlacementInt = command.integerValueOfParameterNamed(MenuConstants.menuPlacementParam ,locale);
 		final MENU_PLACEMENT menuPlacement = (MENU_PLACEMENT) EnumTemplateHelper.fromInt(MENU_PLACEMENT.values() ,menuPlacementInt);
 		
 		final Long parentId = command.longValueOfParameterNamed(MenuConstants.parentMenuIdParam);

 		Boolean hasParentMenu = OptionalHelper.isPresent(parentId);
 		List<MenuItem> menuItems = menuItemsFromJsonIdKeys(command);
 		
 		Menu parentMenu = null ;
 		if(hasParentMenu){
 			parentMenu = menuRepositoryWrapper.findOneWithNotFoundDetection(parentId);
 		}

 		Menu menu = new Menu(name ,parentMenu ,menuPlacement ,null ,menuItems);
 		return menu; 
 	}
 }