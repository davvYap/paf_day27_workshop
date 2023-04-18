package sg.edu.nus.iss.workshop27.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Review implements Serializable {
    private String cid; // comment_id
    private String user;
    private int rating;
    private String comment;
    private int gid;
    private LocalDateTime posted;
    private String boargameName;
    private List<Comment> editedComments = new ArrayList<>();

    public Review() {
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getGid() {
        return gid;
    }

    public void setGid(int gid) {
        this.gid = gid;
    }

    public LocalDateTime getPosted() {
        return posted;
    }

    public void setPosted(LocalDateTime posted) {
        this.posted = posted;
    }

    public String getBoargameName() {
        return boargameName;
    }

    public void setBoargameName(String boargameName) {
        this.boargameName = boargameName;
    }

    public List<Comment> getEditedComments() {
        return editedComments;
    }

    public void setEditedComments(List<Comment> editedComments) {
        this.editedComments = editedComments;
    }

    @Override
    public String toString() {
        return "Review [cid=" + cid + ", user=" + user + ", rating=" + rating + ", comment=" + comment + ", gid=" + gid
                + ", posted=" + posted + ", boargameName=" + boargameName + ", editedComments=" + editedComments + "]";
    }

    public static Review createFromDocument(Document d) {
        Review r = new Review();
        r.setCid(d.getString("cid"));
        r.setUser(d.getString("user"));
        r.setGid(d.getInteger("ID"));
        r.setComment(d.getString("comment"));
        r.setRating(d.getInteger("rating"));
        r.setBoargameName(d.getString("name"));

        List<Document> list = d.get("edited", List.class);
        if (list != null) {
            r.setEditedComments(list.stream().map(doc -> Comment.convertFromDocument(doc)).toList());
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
        LocalDateTime dateTime = LocalDateTime.parse(d.getString("posted"), formatter);
        r.setPosted(dateTime);
        return r;
    }

    public JsonObject toJSONInsert() {
        return Json.createObjectBuilder()
                .add("user", this.user)
                .add("rating", this.rating)
                .add("cid", this.cid)
                .add("comment", this.comment)
                .add("ID", this.gid)
                .add("posted", this.posted.toString())
                .add("name", this.boargameName)
                .build();
    }

    public JsonObject toJSONUpdate() {
        JsonArrayBuilder jsArr = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfComments = this.getEditedComments().stream()
                .map(c -> c.toJSONObjectBuilder())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfComments) {
            jsArr.add(jsonObjectBuilder);
        }

        return Json.createObjectBuilder()
                .add("user", this.user)
                .add("rating", this.rating)
                .add("comment", this.comment)
                .add("ID", this.gid)
                .add("posted", this.posted.toString())
                .add("name", this.boargameName)
                .add("edited", jsArr)
                .build();

    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("user", this.user)
                .add("rating", this.rating)
                .add("comment", this.comment)
                .add("ID", this.gid)
                .add("posted", this.posted.toString())
                .add("name", this.boargameName)
                .add("edited", this.editedComments.isEmpty() ? false : true)
                .add("timestamp", LocalDateTime.now().toString())
                .build();
    }

    public JsonObject toJSONHistory() {
        JsonArrayBuilder jsArr = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfComments = this.getEditedComments().stream()
                .map(c -> c.toJSONObjectBuilder())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfComments) {
            jsArr.add(jsonObjectBuilder);
        }

        return Json.createObjectBuilder()
                .add("user", this.user)
                .add("rating", this.rating)
                .add("comment", this.comment)
                .add("ID", this.gid)
                .add("posted", this.posted.toString())
                .add("name", this.boargameName)
                .add("edited", jsArr)
                .add("timestamp", LocalDateTime.now().toString())
                .build();

    }

}
