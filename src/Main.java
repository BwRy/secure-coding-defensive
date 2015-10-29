import org.apache.commons.io.FileUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

public class Main {
    final static PrintStream errorStream = System.err;

    public static void main(String[] args) {
        InputData data = promptForInputData();

        // Do password hashing
        String hash = BCrypt.hashpw(data.getPassword(), BCrypt.gensalt());

        PrintWriter outputFile = null;
        try {
            outputFile = new PrintWriter(data.getOutputFilePath(), "UTF-8");
        } catch (FileNotFoundException e) {
            errorStream.println("Could not open output file for writing");
            System.exit(-1);
        } catch (UnsupportedEncodingException e) {
            errorStream.println("Encoding not supported");
            System.exit(-1);
        }

        outputFile.println(data.getFirstName());
        outputFile.println(data.getLastName());
        outputFile.println(hash);

        outputFile.close();

        System.out.println("Contents written to output file");

        // Read the input file
        File input = new File(data.getInputFilePath());
        
        try {
            System.out.print(FileUtils.readFileToString(input));
        } catch (IOException e) {
            errorStream.println("Couldn't read input file");
            System.exit(-1);
        }
    }


    /**
     * Checks if the addition between two integers will overflow
     * @param x
     * @param y
     * @return true if the result of addition will overflow
     */
    protected static boolean willAddOverflow(int x, int y) {
        return (y > 0 && x > Integer.MAX_VALUE - y) || (y < 0 && x < Integer.MIN_VALUE - y);
    }

    /**
     * Checks if the given string s is null or empty
     * @param s
     * @return
     */
    protected static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    /**
     * Checks if a file path is valid
     * @param path
     * @return
     */
    protected static boolean isValidPath(String path) {
        return startsWithExecutableDirectory(path) &&
                !isExecutablePath(path);
    }

    /**
     * Checks if the given paths starts with the executable directory (i.e. is a path contained within the path of the
     * executable)
     *
     * @param path assumed to be canonicalized
     * @return
     */
    protected static boolean startsWithExecutableDirectory(String path) {
        String directory = executablePath().substring(0, executablePath().lastIndexOf('/') - 1);
        return path.startsWith(directory);
    }

    /**
     * Checks the given path to see if it matches the executable path. This function assumes that the
     * path is canonicalized
     * @param path
     * @return
     */
    protected static boolean isExecutablePath(String path) {
        return path.equals(executablePath());
    }

    /**
     * Returns the path to the current executable (jar, class, etc.)
     * @return
     */
    private static String executablePath() {
        try {
            return Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (Exception e) {
            errorStream.println("Error occurred while getting executable path");
            System.exit(-1);
        }

        throw new RuntimeException("java is stupid");
    }

    /**
     * The god function
     * @return
     */
    private static InputData promptForInputData() {
        String first = null, last = null, inputFilePath = null, outputFilePath = null, password = null;
        Integer x = null, y = null;
        Scanner scanner = new Scanner(System.in);

        // Prompt for first/last name
        do {
            if (first != null && first.length() > 50) {
                errorStream.println("First name must be at most 50 chars");
            }

            if (last != null && last.length() > 50) {
                errorStream.println("Last name must be at most 50 chars");
            }

            System.out.println("What is your first name?");
            first = scanner.nextLine();

            System.out.println("What is your last name?");
            last = scanner.nextLine();
        } while (isNullOrEmpty(first) || isNullOrEmpty(last) || first.length() > 50 || last.length() > 50);

        // Prompt for two integers
        do {
            if (x != null) {
                errorStream.println("Numbers too large");
            }

            System.out.println("Enter the first number");
            x = scanner.nextInt();
            scanner.nextLine();

            System.out.println("Enter the second number");
            y = scanner.nextInt();
            scanner.nextLine();
        } while (willAddOverflow(x, y));

        // Prompt for input/output paths
        do {
            if (inputFilePath != null) {
                errorStream.println("Input or output file provided is not valid");
            }

            System.out.println("Enter the path for the input file");
            inputFilePath = scanner.nextLine();
            try {
                inputFilePath = new URI(inputFilePath).normalize().getPath();
            } catch (URISyntaxException e) {
                errorStream.println("Invalid URI");
                continue;
            }

            System.out.println("Enter the path for the output file");
            outputFilePath = scanner.nextLine();
            try {
                outputFilePath = new URI(inputFilePath).normalize().getPath();
            } catch (URISyntaxException e) {
                errorStream.println("Invalid URI");
                continue;
            }
        } while (!isValidPath(inputFilePath) && !isValidPath(outputFilePath));

        // Prompt for passwords
        while (true) {
            System.out.println("Enter password");
            password = scanner.nextLine();

            System.out.println("Confirm password");
            String confirmPassword = scanner.nextLine();

            if (!confirmPassword.equals(password)) {
                errorStream.println("Passwords do not match");
                continue;
            }

            // passwords match -- break
            break;
        }

        InputData data = new InputData();

        data.setFirstName(first);
        data.setLastName(last);
        data.setInputFilePath(inputFilePath);
        data.setOutputFilePath(outputFilePath);
        data.setX(x);
        data.setY(y);
        data.setPassword(password);

        return data;
    }
}
