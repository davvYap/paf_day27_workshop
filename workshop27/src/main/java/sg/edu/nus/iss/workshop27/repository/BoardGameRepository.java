package sg.edu.nus.iss.workshop27.repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import sg.edu.nus.iss.workshop27.model.Comment;
import sg.edu.nus.iss.workshop27.model.Game;
import sg.edu.nus.iss.workshop27.model.Review;

@Repository
public class BoardGameRepository {

    @Autowired
    MongoTemplate mongoTemplate;

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS";

    public List<Game> getAllGamesByPagination(int page, int size) {
        Query query = new Query();

        Pageable pageable = PageRequest.of(page, size);

        query.with(pageable);

        return mongoTemplate.find(query, Document.class, "games").stream()
                .map(d -> Game.convertFromDocument(d))
                .toList();
    }

    public List<Game> getAllGames(int limits, int offset) {
        Query query = Query.query(Criteria.where("gid").exists(true)).limit(limits).skip(offset);

        return mongoTemplate.find(query, Document.class, "games").stream()
                .map(d -> Game.convertFromDocument(d))
                .toList();
    }

    // sort games based on ascending or descending
    public List<Game> getGamesByRanking(int limit, int offset, String direction) {
        Query query = Query.query(Criteria.where("gid").exists(true)).limit(limit).skip(offset);

        if (direction.equalsIgnoreCase("asc")) {
            query.with(Sort.by(Sort.Direction.ASC, "ranking"));
        } else if (direction.equalsIgnoreCase("desc")) {
            query.with(Sort.by(Sort.Direction.DESC, "ranking"));
        }
        return mongoTemplate.find(query, Document.class, "games").stream()
                .map(d -> Game.convertFromDocument(d))
                .toList();
    }

    // by gid
    public Game getGameByGid(Integer id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("gid").is(id));

        // IMPORTANT Since we direct create a game object from query, thus the fields
        // within the class must be
        // similar name with the attributes in database, if not will be null

        Game game = mongoTemplate.findOne(query, Game.class, "games");
        System.out.println("Game from repo >>>>>>>>>>>>>>>>>>>>> " + game.toString());
        return game;
    }

    // by _id
    public Optional<Document> getGameByObjectId(String id) {
        ObjectId objectId = new ObjectId(id);
        return Optional.ofNullable(mongoTemplate.findById(objectId, Document.class, "games"));
    }

    // by either gid or _id
    public Optional<Document> getGameById(String id) {
        if (ObjectId.isValid(id)) {
            return Optional.ofNullable(mongoTemplate.findById(id, Document.class, "games"));
        }
        Query query = Query.query(Criteria.where("gid").is(Integer.parseInt(id)));
        return mongoTemplate.find(query, Document.class, "games").stream().findFirst();

    }

    // get games based on year
    public List<Game> getGamesByYear(String operator, int year) {
        Query query = new Query();

        if (operator.equalsIgnoreCase("gte")) {
            query = Query.query(Criteria.where("year").gte(year));
        } else if (operator.equalsIgnoreCase("lte")) {
            query = Query.query(Criteria.where("year").lte(year));
        } else {
            query = Query.query(Criteria.where("year").gt(year));
        }
        query.with(Sort.by(Sort.Direction.ASC, "year"));

        return mongoTemplate.find(query, Document.class, "games")
                .stream().map(d -> Game.convertFromDocument(d))
                .toList();
    }

    // get games based on List of gid
    public List<Game> getGamesByListofYear(List<Integer> years) {
        Query query = Query.query(Criteria.where("year").in(years));
        return mongoTemplate.find(query, Document.class, "games")
                .stream().map(d -> Game.convertFromDocument(d))
                .toList();
    }

    // IMPORTANT workshop 27
    public ObjectId insertReview(Review r) {
        Document doc = new Document();
        doc = Document.parse(r.toJSONInsert().toString());
        Document newDoc = mongoTemplate.insert(doc, "reviews");
        ObjectId id = newDoc.getObjectId("_id");
        return id;
    }

    // public Review insertReview(Review r) {
    // mongoTemplate.insert(r, "reviews");
    // return r;
    // }

    public Optional<Document> getReviewByCid(String cid) {
        Query q = Query.query(Criteria.where("cid").is(cid));
        return mongoTemplate.find(q, Document.class, "reviews").stream().findFirst();
    }

    public long updateReview(Comment comment, String cid, List<Comment> comments) {
        Query q = Query.query(Criteria.where("cid").is(cid));
        Update updateOps = new Update()
                .set("comment", comment.getComment())
                .set("rating", comment.getRating())
                .set("posted", getLocalDateTimeNowString())
                .set("edited", comments.stream().map(c -> c.toJSONObjectBuilder().build().toString()).toList());
        // .set("edited", comments);
        UpdateResult updateResult = mongoTemplate.updateMulti(q, updateOps, Document.class, "reviews");
        return updateResult.getModifiedCount();
    }

    public String getLocalDateTimeNowString() {
        LocalDateTime postedTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        String formattedDateTime = postedTime.format(formatter);
        return formattedDateTime;
    }
}
