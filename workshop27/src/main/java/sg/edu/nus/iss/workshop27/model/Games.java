package sg.edu.nus.iss.workshop27.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

public class Games implements Serializable {
    private int limit;
    private int offset;
    private int total;
    private LocalDateTime timestamp;
    private int year;
    private String operator;
    private List<String> yearList = new ArrayList<>();
    private List<Game> gamesList = new ArrayList<>();

    public Games() {
    }

    public Games(int limit, int offset, int total, LocalDateTime timestamp, List<Game> gamesList) {
        this.limit = limit;
        this.offset = offset;
        this.total = total;
        this.timestamp = timestamp;
        this.gamesList = gamesList;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<String> getYearList() {
        return yearList;
    }

    public void setYearList(List<String> yearList) {
        this.yearList = yearList;
    }

    public List<Game> getGamesList() {
        return gamesList;
    }

    public void setGamesList(List<Game> gamesList) {
        this.gamesList = gamesList;
    }

    @Override
    public String toString() {
        return "Games [limit=" + limit + ", offset=" + offset + ", total=" + total + ", timestamp=" + timestamp
                + ", gamesList=" + gamesList + "]";
    }

    public JsonObject toJSONByOffsetLimit() {
        JsonArrayBuilder jsArr = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfGames = this.getGamesList().stream()
                .map(g -> g.toJSON())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfGames) {
            jsArr.add(jsonObjectBuilder);
        }
        return Json.createObjectBuilder()
                .add("offset", this.offset)
                .add("limit", this.limit)
                .add("total", this.total)
                .add("timestamp", this.timestamp.toString())
                .add("games", jsArr)
                .build();
    }

    public JsonObject toJSONByPage() {
        JsonArrayBuilder jsArr = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfGames = this.getGamesList().stream()
                .map(g -> g.toJSON())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfGames) {
            jsArr.add(jsonObjectBuilder);
        }
        return Json.createObjectBuilder()
                .add("page", this.offset)
                .add("size", this.limit)
                .add("total", this.total)
                .add("timestamp", this.timestamp.toString())
                .add("games", jsArr)
                .build();
    }

    public JsonObject toJSONByYear() {
        JsonArrayBuilder jsArr = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfGames = this.getGamesList().stream()
                .map(g -> g.toJSON())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfGames) {
            jsArr.add(jsonObjectBuilder);
        }
        return Json.createObjectBuilder()
                .add("operator", this.operator)
                .add("year", this.year)
                .add("games", jsArr)
                .build();
    }

    public JsonObject toJSONByYearList() {
        JsonArrayBuilder jsArr = Json.createArrayBuilder();
        List<JsonObjectBuilder> listOfGames = this.getGamesList().stream()
                .map(g -> g.toJSON())
                .toList();
        for (JsonObjectBuilder jsonObjectBuilder : listOfGames) {
            jsArr.add(jsonObjectBuilder);
        }

        JsonArrayBuilder yearJsArr = Json.createArrayBuilder();

        for (String year : this.yearList) {
            yearJsArr.add(year);
        }

        return Json.createObjectBuilder()
                .add("years", yearJsArr)
                .add("games", jsArr)
                .build();
    }

}
