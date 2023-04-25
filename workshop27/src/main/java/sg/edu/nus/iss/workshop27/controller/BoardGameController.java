package sg.edu.nus.iss.workshop27.controller;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import sg.edu.nus.iss.workshop27.model.Comment;
import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.service.BoardGameService;

@RestController
public class BoardGameController {

    @Autowired
    BoardGameService boardGameService;

    @PostMapping(path = "/review", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postReviewOnBoardgames(@ModelAttribute Review review) {
        try {
            boardGameService.insertReview(review);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok(review.toJSONInsert().toString());
    }

    @PutMapping(path = "/review/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateReviewOnBoardgame(@RequestBody String json,
            @PathVariable String reviewId) throws IOException {
        Comment comment = Comment.convertFromJSON(json);

        Optional<Review> opReview = boardGameService.updateReview(comment, reviewId);
        if (opReview.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Json.createObjectBuilder().add("Error", "Review not found!")
                            .build().toString());
        }
        Review review = opReview.get();

        return ResponseEntity.ok().body(review.toJSONUpdate().toString());
    }

    @GetMapping(path = "/review/{reviewId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReview(@PathVariable String reviewId) {
        Optional<Review> opReview = boardGameService.getReviewByCid(reviewId);
        if (opReview.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Json.createObjectBuilder().add("Error", "Review not found!")
                            .build().toString());
        }
        Review review = opReview.get();
        return ResponseEntity.ok().body(review.toJSON().toString());
    }

    @GetMapping(path = "/review/{reviewId}/history", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getReviewHistory(@PathVariable String reviewId) {
        Optional<Review> opReview = boardGameService.getReviewByCid(reviewId);
        if (opReview.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Json.createObjectBuilder().add("Error", "Review not found!")
                            .build().toString());
        }
        Review review = opReview.get();
        return ResponseEntity.ok().body(review.toJSONUpdate().toString());
    }

}
