package six.eared.macaque.plugin.idea.notify;

import com.intellij.notification.*;

public class Notify {
    private static final NotificationGroup NOTIFY_GROUP = NotificationGroupManager.getInstance().getNotificationGroup(NotifyGroupName.BALLOON);


    public static void success(String msg) {
        Notification notify = NOTIFY_GROUP.createNotification(msg, NotificationType.INFORMATION);
        Notifications.Bus.notify(notify);
    }


    public static void error(String msg) {
        Notification notify = NOTIFY_GROUP.createNotification(msg, NotificationType.ERROR);
        Notifications.Bus.notify(notify);
    }
}
