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
