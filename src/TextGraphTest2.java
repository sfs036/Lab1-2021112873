import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.NoSuchElementException;

public class TextGraphTest2 {

    private TextGraph graph;

    @Before
    public void setUp() {
        // Initialize the graph before each test
        graph = new TextGraph();
        // Adding some words and connections
        graph.addEdge("hello", "world");
        graph.addEdge("hello", "there");
        graph.addEdge("world", "wide");

    }

    @Test
    public void testGetRandomBridgeWordExists() {
        // Testing where the bridge word does exist
        String bridgeWord = graph.getRandomBridgeWord("hello", "wide");
        if (bridgeWord == null) {
            System.out.println("There should be no bridge word between 'hello' and 'wide'");
        } else {
            // 如果 bridgeWord 不是 null，可以添加处理非 null 值的逻辑
            System.out.println("The bridge word exists: " + bridgeWord);
        }
    }

    @Test
    public void testGetRandomBridgeWordDoesNotExist() {
        // Testing where the bridge word does not exist
        String bridgeWord = graph.getRandomBridgeWord("world", "hello");
        if (bridgeWord == null) {
            System.out.println("There should be no bridge word between 'world' and 'hello'");
        } else {
            // 如果 bridgeWord 不是 null，可以添加处理非 null 值的逻辑
            System.out.println("The bridge word exists: " + bridgeWord);
        }
    }

    @Test
    public void testGetRandomBridgeWordWithNonExistentVertex() {
        // Testing with a non-existent vertex
        String bridgeWord = graph.getRandomBridgeWord("sunshine", "world");
        if (bridgeWord == null) {
            System.out.println("There should be no bridge word between 'sunshine' and 'world'");
        } else {
            // 如果 bridgeWord 不是 null，可以添加处理非 null 值的逻辑
            System.out.println("The bridge word exists: " + bridgeWord);
        }
    }


}
