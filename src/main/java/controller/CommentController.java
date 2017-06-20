package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import service.FacebookTrolling;
import service.factory.ServiceFactory;
import unit.NotificationUtil;

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
                getPost();
            } catch (Exception e1) {
                NotificationUtil.informationAlert("Warning", "Something went wrong", NotificationUtil.SHORT);
            }
        });
        btnFindComments.setOnAction(e -> {
            try {
                getComments();
            } catch (Exception e1) {
                NotificationUtil.informationAlert("Warning", "Something went wrong", NotificationUtil.SHORT);
            }
        });
        btnClear.setOnAction(e -> txtResult.clear());
    }

    /**
     * Get comments by txtLink.
     */
    private void getComments() throws Exception {
        if (!validator()) {
            NotificationUtil.informationAlert("Information", "Some fields are empty", NotificationUtil.SHORT);
            return;
        }
        List<String> comments = ServiceFactory.getFacebookTrolling().getComments(txtToken.getText(), txtLink.getText(), date.getValue(), txtLocation.getText());
        txtResult.clear();
        txtResult.setText(comments.toString());
    }

    private boolean validator() {
        return !("".equals(txtToken.getText()) || "".equals(txtLink.getText()) || date.getValue() == null);
    }

    /**
     * Get post by txtLink.
     */
    private void getPost() throws Exception {
        if (!validator()) {
            NotificationUtil.informationAlert("Information", "Some fields are empty", NotificationUtil.SHORT);
            return;
        }
        FacebookTrolling facebookTrolling = ServiceFactory.getFacebookTrolling();
        List<String> posts = facebookTrolling.getPosts(txtToken.getText(), txtLink.getText(), date.getValue(), txtLocation.getText());
        txtResult.clear();
        txtResult.setText(posts.toString());
    }
}
