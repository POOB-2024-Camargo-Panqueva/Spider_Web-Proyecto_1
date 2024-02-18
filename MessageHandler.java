import javax.swing.*;

public abstract class MessageHandler {

    /**
     * Displays an error message dialog with the specified message.
     *
     * @param message The error message to display.
     */
    public static void showError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an error message dialog with the specified title and message.
     *
     * @param title   The title of the error message dialog.
     * @param message The error message to display.
     */
    public static void showError(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an information message dialog with the specified message.
     *
     * @param message The information message to display.
     */
    public static void showInfo(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an information message dialog with the specified title and message.
     *
     * @param title   The title of the information message dialog.
     * @param message The information message to display.
     */
    public static void showInfo(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
