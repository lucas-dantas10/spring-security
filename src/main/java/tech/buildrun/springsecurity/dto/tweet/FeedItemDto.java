package tech.buildrun.springsecurity.dto.tweet; 

public record FeedItemDto(
    Long tweetId,
    String content,
    String username
) {
}
