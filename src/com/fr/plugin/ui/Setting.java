package com.fr.plugin.ui;

import com.fr.plugin.FRFrameworkInit;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * Created by vito on 2017/9/15.
 */
public class Setting {
    private JComboBox versionComb;
    private JCheckBox functionRecordCheck;
    public JPanel settingPane;

    private String version = FRFrameworkInit.NEW_VERSION;

    public Setting() {

        versionComb.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                version = (String) e.getItem();
            }
        });
    }

    public boolean needFunctionRecord() {
        return functionRecordCheck.isSelected();
    }

    public String getVersion() {
        return version;
    }
}
