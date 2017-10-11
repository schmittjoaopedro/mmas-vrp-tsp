package schmitt.mmas.graph;

import java.util.*;

public class Graph {

    private List<Node> nodes;

    private Map<Integer, Node> nodesIndex;

    private Set<Edge> edges;

    private double upperX;

    private double lowerX;

    private double upperY;

    private double lowerY;

    public Graph() {
        this.nodes = new ArrayList<>();
        this.nodesIndex = new HashMap<>();
        edges = new HashSet<>();
        upperX = Double.MAX_VALUE * -1.0;
        lowerX = Double.MAX_VALUE;
        upperY = Double.MAX_VALUE * -1.0;
        lowerY = Double.MAX_VALUE;
    }

    public void addNode(int id, double x, double y) {
        if(!this.nodesIndex.containsKey(id)) {
            Node node = new Node();
            node.setId(id);
            node.setX(x);
            node.setY(y);
            this.nodes.add(node);
            this.nodesIndex.put(id, node);
            lowerX = Math.min(lowerX, x);
            upperX = Math.max(upperX, x);
            lowerY = Math.min(lowerY, y);
            upperY = Math.max(upperY, y);
        }
    }

    public Node getNode(int id) {
        return this.nodesIndex.get(id);
    }

    public void addEdge(int idFrom, int idTo, double distance) {
        Node from = getNode(idFrom);
        Node to = getNode(idTo);
        if(from != null && to != null) {
            Edge edge = new Edge();
            edge.setFrom(from);
            from.getEdges().add(edge);
            edge.setTo(to);
            edge.setDistance(distance);
            edges.add(edge);
        }
    }

    public Set<Edge> getEdges(int from) {
        return getNode(from).getEdges();
    }

    public Edge getEdge(int from, int to) {
        Set<Edge> edges = getEdges(from);
        for(Edge edge : edges) {
            if(edge.getTo().getId() == to) {
                return edge;
            }
        }
        return null;
    }

    public Set<Edge> getEdges() {
        return edges;
    }

    public int getNodesLength() {
        return nodes.size();
    }

    public int getEdgesLength() {
        return edges.size();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public double getUpperX() {
        return upperX;
    }

    public double getLowerX() {
        return lowerX;
    }

    public double getUpperY() {
        return upperY;
    }

    public double getLowerY() {
        return lowerY;
    }

    public Graph clone() {
        Graph graph = new Graph();
        for(Node node : this.getNodes()) {
            graph.addNode(node.getId(), node.getX(), node.getY());
        }
        for(Edge edge : this.getEdges()) {
            graph.addEdge(edge.getFrom().getId(), edge.getTo().getId(), edge.getDistance());
        }
        return graph;
    }
}
