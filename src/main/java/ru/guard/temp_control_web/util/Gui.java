package ru.guard.temp_control_web.util;

import java.awt.*;

public class Gui {

    private final TrayIcon trayIcon;

    public Gui() {
        SystemTray systemTray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/icon.png"));

        trayIcon = new TrayIcon(image, "Temp control");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("Temp control");
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        setMenu();
    }

    public void setMenu() {
        PopupMenu popupMenu = new PopupMenu();

        MenuItem menuItem1 = new MenuItem("Woop");
        menuItem1.addActionListener(e -> System.out.println("check"));
        MenuItem menuItem2 = new MenuItem("Poop");
        menuItem2.addActionListener(e -> System.out.println("check"));

        popupMenu.add(menuItem1);
        popupMenu.add(menuItem2);
        trayIcon.setPopupMenu(popupMenu);
    }

    public void showNotification(String title, String text) {
        trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
    }
}
