package org.isf.menu.dto;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;

public class UserMenuItemDTO {
	@NotNull
	@ApiModelProperty(notes="code of the menu item (must be unique)", example = "admtype", position = 1)
	private String 	code;

	@NotNull
	@ApiModelProperty(notes="button label of the menu item", example = "Admission Type", position = 2)
	private String 	buttonLabel;

	@NotNull
	@ApiModelProperty(notes="alt label of the menu item", example = "Admission Type", position = 3)
	private String 	altLabel;

	@ApiModelProperty(notes="tooltip label of the menu item", example = "Admission Type", position = 4)
	private String 	tooltip;

	@ApiModelProperty(notes="shortcut of the menu item", example = "A", position = 5)
	private char shortcut;

	@NotNull
	@ApiModelProperty(notes="parent submenu of the menu item", example = "types", position = 6)
	private String	mySubmenu;

	@NotNull
	@ApiModelProperty(notes="the main window class associated", example = "org.isf.admtype.gui.AdmissionTypeBrowser", position = 7)
	private String myClass;

	@NotNull
	@ApiModelProperty(notes="indicates if the menu item is a submenu or not", example = "true", position = 8)
	private boolean	isASubMenu;

	@NotNull
	@ApiModelProperty(notes="position of the menu item", example = "5", position = 9)
	private int position;
	
	public UserMenuItemDTO() {
		super();
	}

	public UserMenuItemDTO(String code, String buttonLabel, String altLabel, String tooltip, char shortcut,
			String mySubmenu, String myClass, boolean isASubMenu, int position) {
		super();
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
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getButtonLabel() {
		return buttonLabel;
	}

	public void setButtonLabel(String buttonLabel) {
		this.buttonLabel = buttonLabel;
	}

	public String getAltLabel() {
		return altLabel;
	}

	public void setAltLabel(String altLabel) {
		this.altLabel = altLabel;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	public char getShortcut() {
		return shortcut;
	}

	public void setShortcut(char shortcut) {
		this.shortcut = shortcut;
	}

	public String getMySubmenu() {
		return mySubmenu;
	}

	public void setMySubmenu(String mySubmenu) {
		this.mySubmenu = mySubmenu;
	}

	public String getMyClass() {
		return myClass;
	}

	public void setMyClass(String myClass) {
		this.myClass = myClass;
	}

	public boolean isASubMenu() {
		return isASubMenu;
	}

	public void setASubMenu(boolean isASubMenu) {
		this.isASubMenu = isASubMenu;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
	
}
