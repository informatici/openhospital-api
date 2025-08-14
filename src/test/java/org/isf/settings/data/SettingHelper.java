/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2024 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
package org.isf.settings.data;

import java.util.List;
import java.util.stream.IntStream;

import org.isf.settings.model.Setting;
import org.isf.settings.model.SettingCategory;
import org.isf.settings.model.SettingValueType;

public class SettingHelper {

	public static Setting boolSetting() {
		Setting setting = new Setting();

		setting.setType(SettingValueType.bool);
		setting.setValue("TRUE");
		setting.setDefaultValue("FALSE");
		setting.setId(1);
		setting.setCategory(SettingCategory.application);
		setting.setNeedRestart(false);
		setting.setDescription("A boolean setting");
		setting.setCode("BOOL_SETTING");

		return setting;
	}

	public static Setting textSetting() {
		Setting setting = new Setting();

		setting.setId(1);
		setting.setCode("TEXT_SETTING");
		setting.setType(SettingValueType.text);
		setting.setValue("value");
		setting.setDefaultValue("default value");
		setting.setCategory(SettingCategory.application);
		setting.setNeedRestart(false);
		setting.setDescription("A text setting");

		return setting;
	}

	public static Setting numberSetting() {
		Setting setting = new Setting();

		setting.setId(1);
		setting.setCode("NUMBER_SETTING");
		setting.setType(SettingValueType.number);
		setting.setValue("1");
		setting.setDefaultValue("0");
		setting.setCategory(SettingCategory.application);
		setting.setNeedRestart(true);
		setting.setDescription("A number setting");

		return setting;
	}

	public static Setting selectSetting() {
		Setting setting = new Setting();

		setting.setId(1);
		setting.setCode("SELECT_SETTING");
		setting.setType(SettingValueType.select);
		setting.setValue("value 1");
		setting.setDefaultValue("value 1");
		setting.setValueOptions("value 1,value 2,value 3,value 4");
		setting.setCategory(SettingCategory.general);
		setting.setNeedRestart(true);
		setting.setDescription("A number setting");

		return setting;
	}

	public static List<Setting> generate(int number) {
		return IntStream.range(0, number).mapToObj(i -> {
			Setting setting;
			if (i >= 4 && i%4 == 0) {
				setting = selectSetting();
				setting.setId(i);
				return setting;
			} else if (i >= 3 && i%3 == 0) {
				setting = textSetting();
				setting.setId(i);
				return setting;
			} else if (i >= 2 && i%2 == 0) {
				setting = numberSetting();
				setting.setId(i);
				return setting;
			} else {
				setting = boolSetting();
				setting.setId(i);
				return setting;
			}
		}).toList();
	}
}
