import javax.swing.*;

public abstract class MessageHandler {

    /**
     * Displays an error message dialog with the specified message.
     *
     * @param message The error message to display.
     */
    public static void showError(String message) {
        if (SpiderWeb.TEST_MODE)
            System.out.println(message);
        else
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a fatal error message dialog with the specified message and throws an Exception.
     *
     * @param message The error message to display.
     */
    public static void showFatalError(String message) throws Exception {

        if (SpiderWeb.TEST_MODE)
            System.out.println(message);
        else
            JOptionPane.showMessageDialog(null, message, "Fatal Error", JOptionPane.ERROR_MESSAGE);

        throw new Exception(message);
    }

    /**
     * Displays an error message dialog with the specified title and message.
     *
     * @param title   The title of the error message dialog.
     * @param message The error message to display.
     */
    public static void showError(String title, String message) {

        if (SpiderWeb.TEST_MODE)
            System.out.println(title + ": " + message);
        else
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a fatal error message dialog with the specified title and message and throws an Exception.
     *
     * @param title   The title of the error message dialog.
     * @param message The error message to display.
     */
    public static void showFatalError(String title, String message) throws Exception {

        if (SpiderWeb.TEST_MODE)
            System.out.println(title + ": " + message);
        else
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
        throw new Exception(message);
    }

    /**
     * Displays an information message dialog with the specified message.
     *
     * @param message The information message to display.
     */
    public static void showInfo(String message) {

        if (SpiderWeb.TEST_MODE)
            System.out.println(message);
        else
            JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays an information message dialog with the specified title and message.
     *
     * @param title   The title of the information message dialog.
     * @param message The information message to display.
     */
    public static void showInfo(String title, String message) {

        if (SpiderWeb.TEST_MODE)
            System.out.println(title + ": " + message);
        else
            JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}
