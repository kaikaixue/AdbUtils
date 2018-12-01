package com.xk.mutils.view;

import com.alibaba.fastjson.JSON;
import com.xk.mutils.bean.Config;
import com.xk.mutils.Utils;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 操作区
 */
public class OperationArea extends JPanel {


    private String builtIn_DATE = "DATE";

    private int deviceHeight = 30;
    private ButtonGroup group;

    private String name;
    private SelectDeviceArea selectDeviceArea;

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);


        setBackground(Color.black);
        setLayout(null);
        selectDeviceArea = new SelectDeviceArea();
        selectDeviceArea.setBounds(0, 0, 100, getHeight());
        add(selectDeviceArea);

        InstallAppArea installAppArea = new InstallAppArea();
        installAppArea.setBounds(selectDeviceArea.getX() + selectDeviceArea.getWidth(), 0, 200, getHeight());
        VariateArea variateArea = new VariateArea();
        variateArea.setBounds(installAppArea.getX() + installAppArea.getWidth(), 0, 200, getHeight());
        CustomAdbArea customAdbArea = new CustomAdbArea();

//
        add(installAppArea);
        add(variateArea);
//        add(customAdbArea);


    }


    private void init() {
//        setLayout(null);
//        JPanel deviceListPanel = new JPanel();
//        deviceListPanel.setLayout(new FlowLayout());
//
//        List<Config.Device> deviceList = config.getDeviceList();
//        this.group = new ButtonGroup();
//
//        JRadioButton nullDevice = new JRadioButton("无", true);
//        nullDevice.setName("无");
//        deviceListPanel.add(nullDevice);
//        this.group.add(nullDevice);
//        for (Config.Device device : deviceList) {
//            JRadioButton deviceItem = new JRadioButton(device.getDeviceName());
//            deviceItem.setName(device.getDeviceId());
//            deviceListPanel.add(deviceItem);
//            this.group.add(deviceItem);
//        }
//        deviceListPanel.setBounds(0, 0, getWidth(), deviceHeight);
//
//
//        JPanel buttons = new JPanel();
//        buttons.setBackground(Color.BLACK);
//        buttons.setBounds(0, deviceListPanel.getHeight(), getWidth(), getHeight() - deviceListPanel.getHeight());
//        buttons.setLayout(new FlowLayout());
//        initButton(buttons);
//        initClearButton(buttons);
//        add(deviceListPanel);
//        add(buttons);
//
//
//        InstallAppArea installAppArea = new InstallAppArea();
//        add(installAppArea);
//        this.setVisible(true);
//


    }

    private void initClearButton(JPanel parent) {
        JButton jButton = new Button("清空日志", new Runnable() {
            @Override
            public void run() {
                Utils.clear();
            }
        });
        parent.add(jButton);

    }

    private void resetDeviceName() {
        Enumeration<AbstractButton> elements = group.getElements();
        while (elements.hasMoreElements()) {
            AbstractButton abstractButton = elements.nextElement();
            if (abstractButton.isSelected()) {
                name = abstractButton.getName();
                break;
            }
        }
    }


    private void initButton(JPanel parent) {
//        List<Config.AdbCmd> adbCmdList = config.getAdbCmdList();
//        for (Config.AdbCmd adbCmd : adbCmdList) {
//            if (adbCmd.isHide()) {
//                continue;
//            }
//            JButton jButton = new Button(adbCmd.getFunName(), new Runnable() {
//                @Override
//                public void run() {
//                    Date date = new Date();
//                    Map<String, Object> variates = getVariates();
//                    resetDeviceName();
//                    String deviceCmd;
//                    if (adbCmd.getType() == null || "adb".equals(adbCmd.getType())) {
//                        List<String> cmdArray = adbCmd.getCmdArray();
//                        for (String cmd : cmdArray) {
//
////===================
////内置关键字
//                            String dateTemp = "${" + builtIn_DATE + "}";
//                            if (cmd.contains(dateTemp)) {
//                                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
//                                String time = format.format(date);
//                                cmd = cmd.replace(dateTemp, time + "");
//                            }
////===================
//
//                            for (String key : variates.keySet()) {
//                                String temp = "${" + key + "}";
//                                if (cmd.contains(temp)) {
//                                    cmd = cmd.replace(temp, variates.get(key) + "");
//                                }
//                            }
//                            if (name.equals("无")) {
//                                deviceCmd = " ";
//                            } else {
//                                deviceCmd = " -s " + name + " ";
//                            }
//                            Utils.executeShell(config.getAdbPath(), deviceCmd, cmd);
//                        }
//                    }
//                }
//            });
//            parent.add(jButton);
//        }
    }

    /**
     * 从底部变量区获取变量的map
     *
     * @return
     */
    private Map getVariates() {
        try {

            String variateAreaJson = Utils.getVariateAreaJson();
            String json = "{" + variateAreaJson + "}";
            Object parse = JSON.parse(json);
            Field map = parse.getClass().getDeclaredField("map");

            map.setAccessible(true);
            Map content = (Map) map.get(parse);

            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setConfig(Config config) {
        selectDeviceArea.setConfig(config);
    }
}
