/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserMenuItemDTO {

	@NotNull
	@Schema(description = "Code of the menu item (must be unique)", example = "admtype", maxLength = 50)
	private String code;

	@NotNull
	@Schema(description = "Button label of the menu item", example = "Admission Type", maxLength = 50)
	private String buttonLabel;

	@NotNull
	@Schema(description = "Alt label of the menu item", example = "Admission Type", maxLength = 50)
	private String altLabel;

	@Schema(description = "Tooltip label of the menu item", example = "Admission Type", maxLength = 100)
	private String tooltip;

	@Schema(description = "Shortcut of the menu item", example = "A")
	private char shortcut;

	@NotNull
	@Schema(description = "Parent submenu of the menu item", example = "types", maxLength = 50)
	private String mySubmenu;

	@NotNull
	@Schema(description = "The main window class associated", example = "org.isf.admtype.gui.AdmissionTypeBrowser", maxLength = 100)
	private String myClass;

	@NotNull
	@Schema(description = "Indicates if the menu item is a submenu or not", example = "true")
	private boolean isASubMenu;

	@NotNull
	@Schema(description = "Position of the menu item", example = "5")
	private int position;

	public UserMenuItemDTO() {
	}

	public UserMenuItemDTO(String code, String buttonLabel, String altLabel, String tooltip, char shortcut,
			String mySubmenu, String myClass, boolean isASubMenu, int position) {
		this.code = code;
		this.buttonLabel = buttonLabel;
		this.altLabel = altLabel;
		this.tooltip = tooltip;
		this.shortcut = shortcut;
		this.mySubmenu = mySubmenu;
		this.myClass = myClass;
		this.isASubMenu = isASubMenu;
		this.position = position;
	}

	public String getCode() {
		return this.code;
	}

	public String getButtonLabel() {
		return this.buttonLabel;
	}

	public String getAltLabel() {
		return this.altLabel;
	}

	public String getTooltip() {
		return this.tooltip;
	}

	public char getShortcut() {
		return this.shortcut;
	}

	public String getMySubmenu() {
		return this.mySubmenu;
	}

	public String getMyClass() {
		return this.myClass;
	}

	public boolean isASubMenu() {
		return this.isASubMenu;
	}

	public int getPosition() {
		return this.position;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public void setAltLabel(String altLabel) {
		this.altLabel = altLabel;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public void setShortcut(char shortcut) {
		this.shortcut = shortcut;
	}

	public void setMySubmenu(String mySubmenu) {
		this.mySubmenu = mySubmenu;
	}

	public void setMyClass(String myClass) {
		this.myClass = myClass;
	}

	public void setASubMenu(boolean isASubMenu) {
		this.isASubMenu = isASubMenu;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
