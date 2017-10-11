package schmitt.mmas.aco.router;

import schmitt.mmas.graph.Graph;
import schmitt.mmas.graph.Node;
import schmitt.mmas.utils.Timer;

import java.util.ArrayList;
import java.util.List;

public class Globals {

    public Timer timer;

    public Graph graph;

    public Node sourceNode;

    public List<Node> targetNodes;

    public RouteManager routeManager;

    public Ant[] ants;

    public Ant bestSoFar;

    public Ant restartBestAnt;

    public int numberAnts;

    public int iteration;

    public int restartFoundBestIteration;

    public int foundBestIteration;

    public double branchFactor;

    public double maxTime; // In seconds

    public double lambda;

    public double trailMax;

    public double trailMin;

    public double alpha;

    public double beta;

    public double rho;

    public int uGb;

    public Globals() {
        targetNodes = new ArrayList<>();
        routeManager = new RouteManager(this);
        timer = new Timer();
        numberAnts = 30;
        alpha = 1.0;
        beta = 2.0;
        rho = 0.02;
        maxTime = 10.0;
        branchFactor = 1.0001;
        lambda = 0.05;
        iteration = 0;
        restartFoundBestIteration = 0;
        foundBestIteration = 0;
        uGb = 25;
    }

    public double HEURISTIC(Ant ant) {
        return 1.0 / ant.getCost();
    }

}
