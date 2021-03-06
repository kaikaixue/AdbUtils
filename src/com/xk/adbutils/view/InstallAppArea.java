package com.xk.adbutils.view;

import com.xk.adbutils.Constant;
import com.xk.adbutils.ShellUtils;
import com.xk.adbutils.ThreadUtils;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/**
 * 安装apk的工作区
 */
public class InstallAppArea extends MJpanel {

    private JList sourceList;
    private ArrayList<File> listFiles;

    @Override
    protected void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel jLabel = new JLabel("安装apk：");
        add(jLabel);
        sourceList = new JList();
        sourceList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sourceListScroller = new JScrollPane(sourceList);
        add(sourceListScroller);


        JPanel btnContainer = new JPanel();
        btnContainer.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;


        JButton refresh = new JButton("刷新");
        refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refreshApks(0);
            }
        });


        JButton install = new JButton("安装");
        install.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                installApk();
            }


        });
        //第一次延时执行，保证可获取到变量
        refreshApks(1000);
        btnContainer.add(refresh, gridBagConstraints);
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        btnContainer.add(install, gridBagConstraints);
        add(btnContainer);
    }

    @Override
    protected void onConfigLoaded() {

    }

    private void installApk() {
        int selectedIndex = sourceList.getSelectedIndex();
        if (selectedIndex == -1) {
            LogArea.addText("ERROR!!! 请选择要安装的apk！！！", Color.RED);
            return;
        }
        File file = listFiles.get(selectedIndex);
        String defaultUninstallPackageName = VariateArea.instance.getValue("uninstallPackageName");
        if (defaultUninstallPackageName == null || defaultUninstallPackageName.equals("")) {
            defaultUninstallPackageName = Constant.JDPackageName;
        }
        ShellUtils.executeShellWithLog("adb uninstall " + defaultUninstallPackageName);
        ShellUtils.executeShellWithLog("adb install -r " + file.getAbsolutePath());
        if (defaultUninstallPackageName.equals(Constant.JDPackageName)) {
            ShellUtils.executeShellWithLog("adb shell am start -n com.jingdong.app.mall/com.jingdong.app.mall.main.MainActivity");
        }
    }


    public void refreshApks(long delayTime) {

        ThreadUtils.executeDelay(() -> {
            String path = VariateArea.instance.getValue(Constant.KEY_APK_PATH);
            if (path == null || "".equals(path)) {
                path = "./";
            }
            File customApkPathParent = new File(path);
            String absolutePath = customApkPathParent.getAbsolutePath();
            File file = new File(absolutePath);
            File[] files = file.listFiles();
            Arrays.sort(files, (file12, t1) -> {
                //针对jd做特殊的排序处理
                if (file12.getName().contains("JDMALL-") && t1.getName().contains("JDMALL-")) {
                    String[] split = file12.getName().split("-");
                    String[] split1 = t1.getName().split("-");
                    return -split[2].compareTo(split1[2]);
                } else if (file12.getName().contains("JDMALL-")) {
                    return -1;
                } else if (t1.getName().contains("JDMALL-")) {
                    return 1;
                }
                return -file12.getName().compareTo(t1.getName());
            });
            listFiles = new ArrayList<>();
            DefaultListModel listModel = new DefaultListModel();
            for (File file1 : files) {
                if (file1.getName().endsWith(".apk")) {
                    listModel.addElement(file1.getName().replace(".apk", "").replace("JDMALL-", ""));
                    listFiles.add(file1);
                }
            }
            sourceList.setModel(listModel);
        },delayTime);
    }
}
