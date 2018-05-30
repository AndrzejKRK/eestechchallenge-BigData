import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.Session;
import twitter4j.*;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Date;
import java.util.Optional;

public class TwitterStreamer {

    public static void main(String[] args) {
        final Configuration configuration = new ConfigurationBuilder()
                .setDebugEnabled(true)
                .setOAuthConsumerKey("IfohF2wI8gcOyOzxJyl5xPoOI") // paste your consumer key
                .setOAuthConsumerSecret("CpyvQJgabkLDKuXHwEbuOaRZXnqgigRVOFfGHWIriSVnpiyLH6") //paste your CONSUMER_SECRET
                .setOAuthAccessToken("2963516433-PbRm8oAQXno4PkoMpx7K72GDx6lMw3w9ZuEElua") //paste your OAUTH_ACCESS_TOKEN
                .setOAuthAccessTokenSecret("i0lYAsSYfOVhC6z23WBM6H4ZVE2LwP8zdzQsPeALQDYll") // paste your OAUTH_ACCESS_TOKEN
                .build();


        Cluster cluster = Cluster.builder()
                .withClusterName("Test Cluster")
                .addContactPoint("localhost")
                .withPort(9042)
                .build();


        StatusListener listener = new StatusListener() {

            public void onStatus(Status status) {
//                TweetEntity tweet = new TweetEntity();
//                tweet.setLanguage(status.getLang());
//                tweet.setLanguage(status.getPlace());
//                System.out.println("\nText\n" + status.getText());
//                System.out.println("\nCreatedDate\n" + status.getCreatedAt());
//                System.out.println("\nId\n" + status.getId());
//                System.out.println("\nLanguage of user\n" + status.getUser().getLang());
//                System.out.println("\nCountry\n" + status.getPlace().getCountry());
//                System.out.println("\nRetweet count\n" + status.getRetweetCount());
//                status.getUser().getFollowersCount()

                Place place = status.getPlace();
                User user = status.getUser();
                Session session = cluster.connect("keyspace_name");
                BoundStatement bs = session.prepare("insert into sample_table" +
                        "(id, username_id, tweet, created_date, language, country, folowers_count, retweet_count) " +
                        "values (?,?,?,?,?,?,?,?)")
                        .bind(Optional.of(status.getId()).orElse(0L),
                                status.getUser().getId(),
                                Optional.ofNullable(status.getText()).orElse(""),
                                LocalDate.fromMillisSinceEpoch(status.getCreatedAt() != null ? status.getCreatedAt().getTime() : new Date().getTime()),
                                Optional.ofNullable(status.getUser().getLang()).orElse(""),
                                place != null ? place.getCountry() : "",
                                user != null ? user.getFollowersCount() : 0,
                                Optional.of(status.getRetweetCount()).orElse(0));
                session.execute(bs);
                session.close();
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
            }

            public void onScrubGeo(long l, long l1) {

            }

            public void onStallWarning(StallWarning stallWarning) {

            }

            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        };

        TwitterStream twitterStream = new TwitterStreamFactory(configuration).getInstance();
        twitterStream.addListener(listener);
        twitterStream.filter(new FilterQuery().track(
                "beer",
                "trump",
                "facebook",
                "isis",
                "AI",
                "EA",
                "Poland",
                "holidays",
                "christmas",
                "hawking"));


    }
}
