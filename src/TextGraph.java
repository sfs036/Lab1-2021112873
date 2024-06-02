import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.jgrapht.GraphPath;
import com.mxgraph.view.mxGraph;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.mxFastOrganicLayout;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import com.mxgraph.util.mxCellRenderer;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
// 第二次
// 第三次
public class TextGraph {
    private Graph<String, DefaultWeightedEdge> graph;

    public TextGraph() {
        graph = new DirectedWeightedMultigraph<>(DefaultWeightedEdge.class);
    }

    public void readTextFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            processLine(line);
        }
     
        reader.close();
    }

    private void processLine(String line) {
        String[] words = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            addEdge(words[i], words[i + 1]);
        }
    }

    private void addEdge(String from, String to) {
        graph.addVertex(from);
        graph.addVertex(to);
        DefaultWeightedEdge edge = graph.addEdge(from, to);
        if (edge != null) {
            graph.setEdgeWeight(edge, 1.0);
        } else {
            edge = graph.getEdge(from, to);
            graph.setEdgeWeight(edge, graph.getEdgeWeight(edge) + 1);
        }
    }


    public void showDirectedGraph() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800, 600);

        mxGraph mxGraph = new mxGraph();
        Object parent = mxGraph.getDefaultParent();

        mxGraph.getModel().beginUpdate();
        try {
            Map<String, Object> vertices = new HashMap<>();
            for (String vertex : graph.vertexSet()) {
                vertices.put(vertex, mxGraph.insertVertex(parent, null, vertex, 0, 0, 80, 30));
            }
            for (DefaultWeightedEdge edge : graph.edgeSet()) {
                mxGraph.insertEdge(parent, null, graph.getEdgeWeight(edge), vertices.get(graph.getEdgeSource(edge)), vertices.get(graph.getEdgeTarget(edge)));
            }
        } finally {
            mxGraph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(mxGraph);
        graphComponent.setConnectable(false);
        graphComponent.getGraph().setAllowDanglingEdges(false);

        // 应用力导向图布局
        mxFastOrganicLayout layout = new mxFastOrganicLayout(mxGraph);
        layout.setForceConstant(120); // 提高力常数以增加节点间距
        layout.setMinDistanceLimit(10); // 设置最小距离限制
        layout.setMaxDistanceLimit(100); // 可以根据需要调整
        layout.setInitialTemp(200); // 设置较高的初始温度
        layout.setUseBoundingBox(false); // 禁用边界框限制以允许节点自由移动
        layout.execute(parent);


        // 保存图像
        BufferedImage image = mxCellRenderer.createBufferedImage(mxGraph, null, 1, java.awt.Color.WHITE, true, null);
        try {
            ImageIO.write(image, "PNG", new File("graph.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.add(graphComponent);
        frame.setVisible(true);
    }




    public String queryBridgeWords(String word1, String word2) {
        if (!graph.containsVertex(word1) || !graph.containsVertex(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }
        Set<String> bridgeWords = new HashSet<>();
        for (DefaultWeightedEdge edge1 : graph.outgoingEdgesOf(word1)) {
            String middle = graph.getEdgeTarget(edge1);
            if (graph.containsEdge(middle, word2)) {
                bridgeWords.add(middle);
            }
        }
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + word1 + " to " + word2 + "!";
        }
        return "The bridge words from " + word1 + " to " + word2 + " are: " + String.join(", ", bridgeWords);
    }

    public String generateNewText(String inputText) {
        String[] words = inputText.replaceAll("[^a-zA-Z ]", " ").toLowerCase().split("\\s+");
        StringBuilder result = new StringBuilder(words[0]);
        for (int i = 0; i < words.length - 1; i++) {
            String bridge = getRandomBridgeWord(words[i], words[i + 1]);
            if (bridge != null) {
                result.append(" ").append(bridge);
            }
            result.append(" ").append(words[i + 1]);
        }
        return result.toString();
    }

    private String getRandomBridgeWord(String word1, String word2) {
        if (!graph.containsVertex(word1)) return null;
        List<String> bridgeWords = new ArrayList<>();
        for (DefaultWeightedEdge edge1 : graph.outgoingEdgesOf(word1)) {
            String middle = graph.getEdgeTarget(edge1);
            if (graph.containsEdge(middle, word2)) {
                bridgeWords.add(middle);
            }
        }
        if (bridgeWords.isEmpty()) return null;
        Random rand = new Random();
        return bridgeWords.get(rand.nextInt(bridgeWords.size()));
    }

    public String calcShortestPath(String word1, String word2) {
        if (!graph.containsVertex(word1)) {
            return "No " + word1 + " in the graph!";
        }

        if (word2 == null || word2.isEmpty()) {
            return calcAllShortestPathsFromSource(word1);
        }

        if (!graph.containsVertex(word2)) {
            return "No " + word2 + " in the graph!";
        }

        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        GraphPath<String, DefaultWeightedEdge> path = dijkstraAlg.getPath(word1, word2);

        if (path == null) {
            return "No path from " + word1 + " to " + word2 + "!";
        }

        return "Shortest path from " + word1 + " to " + word2 + ": " + String.join(" -> ", path.getVertexList()) + " (weight " + path.getWeight() + ")";
    }

    private String calcAllShortestPathsFromSource(String source) {
        DijkstraShortestPath<String, DefaultWeightedEdge> dijkstraAlg = new DijkstraShortestPath<>(graph);
        SingleSourcePaths<String, DefaultWeightedEdge> paths = dijkstraAlg.getPaths(source);
        StringBuilder results = new StringBuilder();

        for (String vertex : graph.vertexSet()) {
            if (!vertex.equals(source)) {
                GraphPath<String, DefaultWeightedEdge> path = paths.getPath(vertex);
                if (path != null) {
                    results.append("Shortest path from " + source + " to " + vertex + ": "
                            + String.join(" -> ", path.getVertexList()) + " (weight " + path.getWeight() + ")\n");
                } else {
                    results.append("No path from " + source + " to " + vertex + "!\n");
                }
            }
        }

        return results.toString();
    }



    public String randomWalk() throws IOException {  // 在方法签名中声明异常
        List<String> nodes = new ArrayList<>(graph.vertexSet());
        if (nodes.isEmpty()) return "";
        Random rand = new Random();
        String current = nodes.get(rand.nextInt(nodes.size()));
        StringBuilder result = new StringBuilder(current);
        Set<String> visitedEdges = new HashSet<>();

        System.out.println("Press 's' to stop the walk at any time.");

        while (true) {
            Set<DefaultWeightedEdge> edges = graph.outgoingEdgesOf(current);
            if (edges.isEmpty()) {
                System.out.println("No outgoing edges from " + current + ", stopping walk.");
                break;
            }
            List<DefaultWeightedEdge> edgeList = new ArrayList<>(edges);
            DefaultWeightedEdge nextEdge = edgeList.get(rand.nextInt(edgeList.size()));
            String next = graph.getEdgeTarget(nextEdge);
            String edge = current + " -> " + next;
            if (visitedEdges.contains(edge)) {
                System.out.println("Edge " + edge + " already visited, stopping walk.");
                break;
            }
            visitedEdges.add(edge);
            result.append(" -> ").append(next);
            current = next;

            // 检查用户是否请求停止遍历
            if (System.in.available() > 0) {
                char input = (char) System.in.read();
                if (input == 's' || input == 'S') {
                    System.out.println("Walk stopped by user.");
                    break;
                }
            }
        }

        // 将结果写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("randomWalkResult.txt"))) {
            writer.write(result.toString());
        } // 异常由 try-with-resources 自动处理

        return result.toString();
    }


    public static void main(String[] args) throws IOException {
        TextGraph tg = new TextGraph();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the path of the text file:");
        String filePath = scanner.nextLine();
        tg.readTextFile(filePath);

        System.out.println("Directed graph created.");
        tg.showDirectedGraph();

        while (true) {
            System.out.println("Choose an option: 1) Query bridge words 2) Generate new text 3) Shortest path 4) Random walk 5) Exit");
            int option = scanner.nextInt();
            scanner.nextLine();  // consume the newline

            switch (option) {
                case 1:
                    System.out.println("Enter the first word:");
                    String word1 = scanner.nextLine();
                    System.out.println("Enter the second word:");
                    String word2 = scanner.nextLine();
                    System.out.println(tg.queryBridgeWords(word1, word2));
                    break;
                case 2:
                    System.out.println("Enter the new text:");
                    String inputText = scanner.nextLine();
                    System.out.println(tg.generateNewText(inputText));
                    break;
                case 3:
                    System.out.println("Enter the first word:");
                    word1 = scanner.nextLine();
                    System.out.println("Enter the second word:");
                    word2 = scanner.nextLine();
                    System.out.println(tg.calcShortestPath(word1, word2));
                    break;
                case 4:
                    System.out.println(tg.randomWalk());
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
