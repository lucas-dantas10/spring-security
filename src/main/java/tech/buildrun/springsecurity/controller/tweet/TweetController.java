package tech.buildrun.springsecurity.controller.tweet;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import tech.buildrun.springsecurity.dto.tweet.CreateTweetDto;
import tech.buildrun.springsecurity.dto.tweet.FeedDto;
import tech.buildrun.springsecurity.dto.tweet.FeedItemDto;
import tech.buildrun.springsecurity.entities.Role;
import tech.buildrun.springsecurity.entities.Tweet;
import tech.buildrun.springsecurity.repository.TweetRepository;
import tech.buildrun.springsecurity.repository.UserRepository;

@RestController
public class TweetController {
    
    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> listTweet(
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        var tweets = tweetRepository.findAll(
            PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
            .map(tweet -> new FeedItemDto(tweet.GetTweetId(), tweet.getContent(), tweet.getUser().getUsername()));

        return ResponseEntity.ok(
            new FeedDto(
                tweets.getContent(), 
                page, 
                pageSize, 
                tweets.getTotalPages(), 
                tweets.getTotalElements())
        );
    }

    @PostMapping("/tweet")
    public ResponseEntity<Void> createTweet(
        @RequestBody CreateTweetDto request,
        JwtAuthenticationToken token
    ) {
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(request.content());

        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweet/{id}") 
    public ResponseEntity<Void> deleteTweet(
        @PathVariable("id") Long tweetId,
        JwtAuthenticationToken token 
    ) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = tweetRepository.findById(tweetId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        boolean isAdmin = user.get().getRoles()
            .stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (
            isAdmin 
            || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))
        ) {
            tweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }
}
