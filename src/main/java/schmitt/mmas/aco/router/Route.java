package schmitt.mmas.aco.router;

import schmitt.mmas.aco.path.PathListener;
import schmitt.mmas.aco.path.PathSolver;
import schmitt.mmas.graph.Graph;
import schmitt.mmas.graph.Node;

public class Route extends Thread implements PathListener {

    private Graph graph;

    private Node sourceNode;

    private Node targetNode;

    private PathSolver pathSolver;

    private Integer[] bestRoute;

    private Double bestCost;

    private double pheromone;

    private double total;

    public Route(Graph graph, Node sourceNode, Node targetNode) {
        this.graph = graph;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        pathSolver = new PathSolver(graph, graph.getNode(sourceNode.getId()), graph.getNode(targetNode.getId()));
        pathSolver.setPathListener(this);
    }

    public void setup() {
        pathSolver.setup();
    }

    @Override
    public void run() {
        pathSolver.solve();
    }

    @Override
    public void onBestRouteFound(Integer[] route, Double cost) {
        this.bestRoute = route;
        this.bestCost = cost;
    }

    public Integer[] getBestRoute() {
        return bestRoute;
    }

    public void setBestRoute(Integer[] bestRoute) {
        this.bestRoute = bestRoute;
    }

    public double getBestCost() {
        return bestCost;
    }

    public void setBestCost(double bestCost) {
        this.bestCost = bestCost;
    }

    public double getPheromone() {
        return pheromone;
    }

    public void setPheromone(double pheromone) {
        this.pheromone = pheromone;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(Node sourceNode) {
        this.sourceNode = sourceNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(Node targetNode) {
        this.targetNode = targetNode;
    }

    public boolean finished() {
        return pathSolver.isFinished();
    }
}
