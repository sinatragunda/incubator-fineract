package org.apache.fineract.presentation.menu.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Enumerated;
import javax.persistence.FetchType ;

@Entity
@Table(name ="m_menu_table")
public class MenuTable extends AbstractPersistableCustom<Long> {

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name ="menu_id")
	private Menu menu;

	@ManyToOne
	@JoinColumn(name ="menu_item_id")
	private MenuItem menuItem;

	public MenuTable(Menu menu, MenuItem menuItem) {
		this.menu = menu;
		this.menuItem = menuItem;
	}

	public MenuItem getMenuItem() {
		return menuItem;
	}
}