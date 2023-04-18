package sg.edu.nus.iss.workshop27.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.workshop27.exception.GameNotFoundException;
import sg.edu.nus.iss.workshop27.model.Comment;
import sg.edu.nus.iss.workshop27.model.Game;
import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.repository.BoardGameRepository;

@Service
public class BoardGameService {

    @Autowired
    BoardGameRepository boardgameRepository;

    public List<Game> getAllGamesByPagination(int page, int size) {
        return boardgameRepository.getAllGamesByPagination(page, size);
    }

    public List<Game> getAllGames(int limit, int offset) {
        return boardgameRepository.getAllGames(limit, offset);
    }

    public List<Game> getGamesByRanking(int limit, int offset, String direction) {
        return boardgameRepository.getGamesByRanking(limit, offset, direction);
    }

    public Game getGameByGid(Integer gameId) {
        return boardgameRepository.getGameByGid(gameId);
    }

    public Game getGameByObjectId(String id) {
        if (boardgameRepository.getGameByObjectId(id).isEmpty()) {
            return null;
        }
        return Game.convertFromDocument(boardgameRepository.getGameByObjectId(id).get());
    }

    public Game getGameById(String id) {
        if (boardgameRepository.getGameById(id).isEmpty()) {
            return null;
        }
        return Game.convertFromDocument(boardgameRepository.getGameById(id).get());
    }

    public List<Game> getGamesByYear(String operator, int year) {
        return boardgameRepository.getGamesByYear(operator, year);
    }

    public List<Game> getGamesByListofYear(List<Integer> years) {
        return boardgameRepository.getGamesByListofYear(years);
    }

    public List<String> getListFromString(String input) {
        String[] inputArray = input.split(",");
        List<String> list = new ArrayList<>();
        for (String string : inputArray) {
            list.add(string.strip());
        }
        return list;
    }

    // IMPORTANT workshop 27
    public Review insertReview(Review r) throws GameNotFoundException {
        Game g = getGameByGid(r.getGid());
        if (g == null) {
            System.out.println("Game not found!!");
            throw new GameNotFoundException("Game not found!!!");
        }
        r.setBoargameName(g.getName());
        r.setPosted(LocalDateTime.now());
        boardgameRepository.insertReview(r);
        return r;
    }

    public Optional<Review> getReviewByCid(String cid) {
        Optional<Document> doc = boardgameRepository.getReviewByCid(cid);
        if (doc.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(Review.createFromDocument(doc.get()));
    }

    public Optional<Review> updateReview(Comment comment, String cid) {
        Optional<Document> doc = boardgameRepository.getReviewByCid(cid);
        if (doc.isEmpty()) {
            return Optional.empty();
        }
        Review review = Review.createFromDocument(doc.get());
        Comment oldComment = new Comment(review.getComment(), review.getRating(), review.getPosted());

        // update new review
        review.setComment(comment.getComment());
        review.setRating(comment.getRating());
        review.setPosted(comment.getPosted());

        List<Comment> comments = new ArrayList<>();
        List<Comment> existingComments = review.getEditedComments();
        for (Comment c : existingComments) {
            comments.add(c);
        }
        comments.add(oldComment);
        review.setEditedComments(comments);

        boardgameRepository.updateReview(comment, cid, comments);
        return Optional.ofNullable(review);
    }

}
