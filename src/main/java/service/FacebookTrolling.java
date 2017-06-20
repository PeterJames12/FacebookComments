package service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public interface FacebookTrolling {

    /**
     * @return list of post by given parameters.
     */
    List<String> getPosts(String token, String link, LocalDate since, String location) throws IOException;

    /**
     * @return all comments from given post id.
     */
    List<String> getComments(String token, String link, LocalDate since, String location) throws IOException;
}


