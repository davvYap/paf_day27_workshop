package sg.edu.nus.iss.workshop27.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;

public class Comment implements Serializable {
    private String comment;
    private int rating;
    private LocalDateTime posted;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    public Comment() {
    }

    public Comment(String comment, int rating, LocalDateTime posted) {
        this.comment = comment;
        this.rating = rating;
        this.posted = posted;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public LocalDateTime getPosted() {
        return posted;
    }

    public void setPosted(LocalDateTime posted) {
        this.posted = posted;
    }

    @Override
    public String toString() {
        return "Comment [comment=" + comment + ", rating=" + rating + ", posted=" + posted + "]";
    }

    public static Comment convertFromJSON(String json) throws IOException {
        Comment c = new Comment();
        if (json != null) {
            try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
                JsonReader r = Json.createReader(is);
                JsonObject jsObj = r.readObject();
                c.setComment(jsObj.getString("comment"));
                c.setRating(jsObj.getInt("rating"));
            }
        }
        c.setPosted(LocalDateTime.now());
        return c;
    }

    public static Comment convertFromJSONforUpdate(String json) throws IOException {
        Comment c = new Comment();
        if (json != null) {
            try (InputStream is = new ByteArrayInputStream(json.getBytes())) {
                JsonReader r = Json.createReader(is);
                JsonObject jsObj = r.readObject();
                c.setComment(jsObj.getString("comment"));
                c.setRating(jsObj.getInt("rating"));

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
                c.setPosted(LocalDateTime.parse(jsObj.getString("posted"), formatter));
            }
        }
        return c;
    }

    public static Comment convertFromDocument(Document d) {
        Comment c = new Comment();
        c.setComment(d.getString("comment"));
        c.setRating(d.getInteger("rating"));
        // Date postedDate = d.getDate("posted");
        // Instant instant = postedDate.toInstant();
        // LocalDateTime posted = LocalDateTime.ofInstant(instant,
        // ZoneId.systemDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        c.setPosted(LocalDateTime.parse(d.getString("posted"), formatter));
        return c;
    }

    public JsonObjectBuilder toJSONObjectBuilder() {
        // to standardize the output localdatetime
        LocalDateTime postedTime = this.getPosted();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String formattedDateTime = postedTime.format(formatter);

        return Json.createObjectBuilder()
                .add("comment", this.comment)
                .add("rating", this.rating)
                .add("posted", formattedDateTime);
    }

}
