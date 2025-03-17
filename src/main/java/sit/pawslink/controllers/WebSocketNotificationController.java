package sit.pawslink.controllers;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import sit.pawslink.dto.ScanRecordResponse;

@RestController
public class WebSocketNotificationController {
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketNotificationController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendScanNotification(ScanRecordResponse scanRecord) {
        Integer ownerId = scanRecord.getOwnerId();
        String destination = "/topic/scan-notification/" + ownerId;

        System.out.println("🔔 Sending WebSocket Notification for: " + scanRecord.getScanId() + " to " + destination);

        messagingTemplate.convertAndSend(destination, scanRecord);
    }
}
