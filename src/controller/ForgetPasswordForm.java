package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import database.DatabaseConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgetPasswordForm {

    public TextField txtForgetPasswordUsername;
    public PasswordField PldForgetpasswordPreviousPassword;
    public PasswordField pldForegetPasswordNewPassword;
    public Button btnForgetPassword;
    public PasswordField PldForgetPasswordConfirmPassword;

    public void btnForgetPasswordOnAction(ActionEvent actionEvent) {
        String username = txtForgetPasswordUsername.getText();
        String previousPassword = PldForgetpasswordPreviousPassword.getText();
        String newPassword = pldForegetPasswordNewPassword.getText();
        String confirmNewPassword = PldForgetPasswordConfirmPassword.getText();

        if (username.isBlank() || previousPassword.isBlank() || newPassword.isBlank() || confirmNewPassword.isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Please fill in all field");
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            showAlert(Alert.AlertType.WARNING, "New password and confirm password do not match");
            return;
        }

        if (newPassword.length() <= 6) {
            showAlert(Alert.AlertType.WARNING, "New password must be more than 6 characters");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement checkUserStmt = connection.prepareStatement(
                     "SELECT * FROM new_table WHERE username = ? AND password = ?");
             PreparedStatement updatePasswordStmt = connection.prepareStatement(
                     "UPDATE new_table SET password = ? WHERE username = ?")) {

            checkUserStmt.setString(1, username);
            checkUserStmt.setString(2, previousPassword);

            ResultSet resultSet = checkUserStmt.executeQuery();
            if (resultSet.next()) {
                updatePasswordStmt.setString(1, newPassword);
                updatePasswordStmt.setString(2, username);
                updatePasswordStmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Password updated successfully");

                // Load and display the LoginForm
                Parent loginForm = FXMLLoader.load(getClass().getResource("/view/LoginForm.fxml"));
                Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                stage.setScene(new Scene(loginForm, 600, 400));
            } else {
                showAlert(Alert.AlertType.ERROR, "Username or previous password is incorrect");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error: " + e.getMessage());
        }

        // Clear the text fields
        txtForgetPasswordUsername.clear();
        PldForgetpasswordPreviousPassword.clear();
        pldForegetPasswordNewPassword.clear();
        PldForgetPasswordConfirmPassword.clear();
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void txtForgetPasswordUsernameOnAction(ActionEvent actionEvent) {
    }

    public void PldForgetpasswordPreviousPasswordOnAction(ActionEvent actionEvent) {
    }

    public void pldForegetPasswordNewPasswordOnAction(ActionEvent actionEvent) {
    }

    public void PldForgetPasswordConfirmPasswordOnAction(ActionEvent actionEvent) {
    }
}