package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import message.Message;
import database.DatabaseConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class RegisterForm {

    public PasswordField TxtBoxRegisterFormPassword;
    public Label loginMessageLabel;
    public Hyperlink HykRegisterForm;
    public TextField TxtLoginFormBoxUserName;
    public Button BtnLoginFormRegister;
    public Hyperlink HykLoginForm;
    @FXML
    private TextField TxtRegisterFormBoxUserName;

    @FXML
    private PasswordField TxtConfirmPasswordBoxPassword;

    @FXML
    private Button BtnRgisterFormRegister;

    @FXML
    void BtnRegisterFormRegisterLoginOnAction(ActionEvent event) {
        String username = TxtRegisterFormBoxUserName.getText();
        String password = TxtBoxRegisterFormPassword.getText();
        String confirmPassword = TxtConfirmPasswordBoxPassword.getText();

        if (username.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            showAlert(AlertType.WARNING, Message.ENTER_USERNAME_PASSWORD);
        } else if (!validatePassword(password)) {
            showAlert(AlertType.WARNING, Message.PASSWORD_REQUIREMENTS);
        } else if (!password.equals(confirmPassword)) {
            showAlert(AlertType.WARNING, Message.PASSWORDS_DO_NOT_MATCH);
        } else {
            try (Connection connection = DatabaseConnection.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(
                         "INSERT INTO new_table (username, password) VALUES (?, ?)")) {

                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);

                int rowsInserted = preparedStatement.executeUpdate();
                if (rowsInserted > 0) {
                    showAlert(AlertType.INFORMATION, Message.LOGIN_SUCCESS);
                } else {
                    showAlert(AlertType.ERROR, Message.LOGIN_FAILED);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database error: " + e.getMessage());
            }

            // Clear the text boxes
            TxtRegisterFormBoxUserName.clear();
            TxtBoxRegisterFormPassword.clear();
            TxtConfirmPasswordBoxPassword.clear();
        }
    }

    @FXML
    void TextboxRegisterFormUsernameOnAction(ActionEvent event) {
        TxtBoxRegisterFormPassword.requestFocus(); // Move focus to the password field
    }

    @FXML
    void TxtBoxRegisterFormPasswordOnAction(ActionEvent event) {
        TxtConfirmPasswordBoxPassword.requestFocus(); // Move focus to the confirm password field
    }

    @FXML
    void TxtBoxConfirmPasswordOnAction(ActionEvent event) {
        BtnRgisterFormRegister.fire(); // Simulate a click on the register button
    }


    private void showAlert(AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle("Register Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean validatePassword(String password) {
        if (password.length() < 6) {
            return false;
        }
        String upperCaseChars = "(.*[A-Z].*)";
        String lowerCaseChars = "(.*[a-z].*)";
        String numbers = "(.*[0-9].*)";
        String specialChars = "(.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*)";
        return password.matches(upperCaseChars) && password.matches(lowerCaseChars) && password.matches(numbers) && password.matches(specialChars);
    }


    public void HykRegisterFormOnAction(ActionEvent actionEvent) {
        try {
            // Load the LoginForm.fxml
            Parent loginForm = FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"));

            // Get the current stage
            Stage stage = (Stage) ((Hyperlink) actionEvent.getSource()).getScene().getWindow();

            // Set the scene to the login form
            stage.setScene(new Scene(loginForm, 600, 400));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Failed to open login form: " + e.getMessage());
        }
    }
}