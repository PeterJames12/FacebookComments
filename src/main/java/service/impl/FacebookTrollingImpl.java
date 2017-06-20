package service.impl;

import com.restfb.*;
import com.restfb.types.Post;
import service.Analyzer;
import service.FacebookTrolling;
import service.factory.ServiceFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class FacebookTrollingImpl implements FacebookTrolling {

    private static String status;

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<String> getPosts(String token, String link, LocalDate date, String location) throws IOException {
        Date since = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Connection<Post> lists = getPostList(token, since, link);
        List<String> posts = new LinkedList<>();
        for (List<Post> list : lists) {
            list.forEach(s -> posts.add(s.getMessage()));
        }
        writer(posts.toString(), location);
        return posts;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public List<String> getComments(String token, String link, LocalDate date, String location) throws IOException {
        Date since = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<String> list = getListId(token, since, link);
        FacebookClient facebookClient = new DefaultFacebookClient(token, Version.VERSION_2_7);
        Map<String, String> postsMap = new HashMap<>();
        list.forEach(s -> {
            Connection<Post> posts = facebookClient
                    .fetchConnection(s + "/comments", Post.class);
            posts.forEach(feed -> feed.forEach(post -> {
                postsMap.put(post.getMessage(), post.getFrom().getName());
            }));
        });

        List<String> fullList = new LinkedList<>();
        postsMap.forEach((k, v) -> {
            fullList.add("User "
                    + v
                    + "\n"
                    + "added comments"
                    + "\n"
                    + k
                    + "\n"
                    + "\n"
                    + "Coefficient is : "
                    + calculateCoefficient(k)
                    + "\n"
                    + "Status: "
                    + status
                    + "\n"
                    + "\n");
            try {
                writer(fullList.toString(), location);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return fullList;
    }

    private Float calculateCoefficient(String text) {
        Analyzer analyzer = ServiceFactory.getAnalyzer();
        final Float coefficient = analyzer.getCoeffFromCommentOrPost(text);
        if (coefficient > 7.0) {
            status = "Troll";
        } else {
            status = "Normal";
        }
        return coefficient;
    }

    /**
     * @return list of post's id.
     */
    private List<String> getListId(String token, Date since, String link) {
        Connection<Post> list = getPostList(token, since, link);
        List<String> listId = new LinkedList<>();

        for (List<Post> posts : list) {
            posts.forEach(s -> listId.add(s.getId()));
        }
        return listId;
    }

    /**
     * @return list of post by given date and token.
     */
    private Connection<Post> getPostList(String token, Date since, String link) {
        FacebookClient facebookClient = new DefaultFacebookClient(token, Version.VERSION_2_7);
        return facebookClient.fetchConnection(link + "/feed", Post.class,
                Parameter.with("since", since));
    }

    /**
     * Write text to given file.
     */
    private void writer(String text, String location) throws IOException {
        final File file = new File(location);
        final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        bufferedWriter.append(text);
        bufferedWriter.flush();
    }
}
