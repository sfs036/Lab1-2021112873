import static org.junit.Assert.*;
import org.junit.Test;
import java.io.IOException;
import java.util.Scanner;

public class TextGraphTest1 {

    @Test
    public void testReadTextFile_ValidFilePath() {
        TextGraph textGraph = new TextGraph();

        // Prompt the user to enter the file path
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the file path:");
        String filePath = scanner.nextLine();

        try {
            textGraph.readTextFile(filePath);
        } catch (IOException e) {
            fail("IOException should not be thrown for a valid file path.");
        }

        // TODO: Add assertions to check if the graph is properly populated after reading the text file.
    }
}
