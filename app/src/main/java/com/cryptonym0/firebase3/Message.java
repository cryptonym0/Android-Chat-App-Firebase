package com.cryptonym0.firebase3;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties public class Message {

    public String userId;
    public String author;
    public String message;
    public long timestamp;

    public Message() {

    }

    public Message(String userId, String author, String message, long timestamp) {
        this.userId     = userId;
        this.author     = author;
        this.message    = message;
        this.timestamp  = timestamp;
    }

    @Exclude public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("author", author);
        result.put("message", message);
        result.put("timestamp", timestamp);
        return result;
    }

    @Override public String toString() {
        return "Message{" +
                "author='" + author + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}