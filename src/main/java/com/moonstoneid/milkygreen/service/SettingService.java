package com.moonstoneid.milkygreen.service;

import java.util.Objects;

import com.moonstoneid.milkygreen.model.Setting;
import com.moonstoneid.milkygreen.repo.SettingRepository;
import org.springframework.stereotype.Service;

@Service
public class SettingService {

    private static final String SETTING_VALUE_TRUE = "true";
    private static final String SETTING_VALUE_FALSE = "false";

    private static final String SETTING_NAME_ALLOW_AUTO_IMPORT = "allow_auto_import";

    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public boolean isAllowAutoImport() {
        String value = getValue(SETTING_NAME_ALLOW_AUTO_IMPORT);
        return Objects.equals(value, SETTING_VALUE_TRUE);
    }

    public void setAllowAutoImport(boolean allowAutoImport) {
        String value = allowAutoImport ? SETTING_VALUE_TRUE : SETTING_VALUE_FALSE;
        saveValue(SETTING_NAME_ALLOW_AUTO_IMPORT, value);
    }

    private String getValue(String name) {
        Setting setting = settingRepository.findById(name).orElse(null);
        return setting != null ? setting.getValue() : null;
    }

    private void saveValue(String name, String value) {
        Setting setting = new Setting();
        setting.setName(name);
        setting.setValue(value);
        settingRepository.save(setting);
    }

}
