package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import service.FacebookTrolling;
import service.factory.ServiceFactory;
import unil.NotificationUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CommentController implements Initializable {

    @FXML
    private JFXTextField txtLocation;
    @FXML
    private JFXTextArea txtResult;
    @FXML
    private JFXButton btnFindPost;
    @FXML
    private JFXButton btnFindComments;
    @FXML
    private JFXButton btnClear;
    @FXML
    private JFXTextField txtToken;
    @FXML
    private JFXTextField txtLink;
    @FXML
    private JFXDatePicker date;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnFindPost.setOnAction(e -> {
            try {
                loadPost();
            } catch (Exception e1) {
                NotificationUtil.informationAlert("Warning", "Something went wrong", NotificationUtil.SHORT);
            }
        });
        btnFindComments.setOnAction(e -> {
            try {
                loadComments();
            } catch (Exception e1) {
                NotificationUtil.informationAlert("Warning", "Something went wrong", NotificationUtil.SHORT);
            }
        });
        btnClear.setOnAction(e -> txtResult.clear());
    }

    /**
     * Get comments by txtLink.
     */
    private void loadComments() throws Exception {
        if (!validator()) {
            NotificationUtil.informationAlert("Information", "Some fields are empty", NotificationUtil.SHORT);
            return;
        }
        final Thread thread = new Thread(getComments());
        thread.start();
    }

    private Runnable getComments() {
        return () -> {
            try {
                List<String> comments = ServiceFactory.getFacebookTrolling().getComments(txtToken.getText(), txtLink.getText(), date.getValue(), txtLocation.getText());
                txtResult.setText(comments.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    private boolean validator() {
        return !("".equals(txtToken.getText()) || "".equals(txtLink.getText()) || date.getValue() == null);
    }

    /**
     * Get post by txtLink.
     */
    private void loadPost() throws Exception {
        if (!validator()) {
            NotificationUtil.informationAlert("Information", "Some fields are empty", NotificationUtil.SHORT);
            return;
        }

        final Thread thread = new Thread(getPost());
        thread.start();
    }

    private Runnable getPost() {
        return () -> {
            try {
                FacebookTrolling facebookTrolling = ServiceFactory.getFacebookTrolling();
                List<String> posts = facebookTrolling.getPosts(txtToken.getText(), txtLink.getText(), date.getValue(), txtLocation.getText());
                txtResult.clear();
                txtResult.setText(posts.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }
}
