package schmitt.mmas.aco.path;

import schmitt.mmas.graph.Edge;
import schmitt.mmas.graph.Graph;
import schmitt.mmas.graph.Node;

import java.util.ArrayList;
import java.util.List;

public class PathSolver {

    private Globals _globals;

    private Statistics statistics;

    private PathListener pathListener;

    private boolean finished = false;

    public PathSolver(Graph graph, Node sourceNode, Node targetNode) {
        _globals = new Globals();
        _globals.graph = graph;
        _globals.sourceNode = sourceNode;
        _globals.targetNode = targetNode;
        statistics = new Statistics(_globals);
    }

    public void setup() {
        _globals.timer.startTimer();
        allocateAnts();
        restartMatrices();
        //System.out.println("Configuration took: " + _globals.timer.elapsedTime());
        initTry();
    }

    public void solve() {
        while(!terminateCondition()) {
            constructSolutions();
            updateStatistics();
            pheromoneTrailUpdate();
            searchControl();
            _globals.iteration++;
            //statistics.calculateStatistics();
        }
        System.out.println("Finished!!! " + _globals.sourceNode.getId() + "->" + _globals.targetNode.getId() + " " + _globals.bestSoFar.getCost() + " (" + _globals.iteration + ")");
        finished = true;
    }

    private void allocateAnts() {
        _globals.ants = new Ant[_globals.numberAnts];
        for(int i = 0; i < _globals.numberAnts; i++) {
            _globals.ants[i] = new Ant(_globals);
        }
        _globals.bestSoFar = new Ant(_globals);
        _globals.restartBestAnt = new Ant(_globals);
    }

    private void restartMatrices() {
        for(Edge edge : _globals.graph.getEdges()) {
            edge.setPheromone(0.0);
            edge.setTotal(0.0);
        }
    }

    private void initTry() {
        _globals.timer.startTimer();
        _globals.iteration = 0;
        _globals.restartFoundBestIteration = 0;
        _globals.foundBestIteration = 0;
        _globals.ants[0].nnTour();
        _globals.bestSoFar = _globals.ants[0];
        _globals.nnAnt = _globals.bestSoFar.clone();
        _globals.lifeTime = (int) (_globals.bestSoFar.getRoute().size() * 1.5);
        _globals.trailMax = 1.0 / (_globals.rho * _globals.ants[0].getCost());
        _globals.trailMin = _globals.trailMax / (2.0 * _globals.graph.getNodes().size());
        initPheromoneTrails(_globals.trailMax);
        computeTotalInformation();
        if(this.pathListener != null) {
            this.pathListener.onBestRouteFound(this.getResultRoute(), this.getResultCost());
        }
    }

    private void initPheromoneTrails(double trail) {
        for(Edge edge : _globals.graph.getEdges()) {
            edge.setPheromone(trail);
            edge.setTotal(trail);
        }
    }

    private void computeTotalInformation() {
        for(Edge edge : _globals.graph.getEdges()) {
            double value = Math.pow(edge.getPheromone(), _globals.alpha) * Math.pow(_globals.HEURISTIC(edge), _globals.beta);
            edge.setTotal(value);
        }
    }

    private boolean terminateCondition() {
        return  (_globals.timer.elapsedTime() > _globals.maxTime * 1000) ||
                (_globals.iteration > _globals.maxIterations) ||
                (_globals.iteration - _globals.foundBestIteration) > _globals.bestIntervalStop;
    }

    private void constructSolutions() {
        for(Ant ant : _globals.ants) {
            ant.heuristicTour();
        }
    }

    public void updateStatistics() {
        Ant iterationBestAnt = findBestAnt();
        if(iterationBestAnt.getCost() < _globals.bestSoFar.getCost()) {
            _globals.bestSoFar = iterationBestAnt.clone();
            _globals.restartBestAnt = iterationBestAnt.clone();
            _globals.foundBestIteration = _globals.iteration;
            _globals.restartFoundBestIteration = _globals.iteration;
            _globals.trailMax = 1.0 / (_globals.rho * _globals.bestSoFar.getCost());
            _globals.trailMin = _globals.trailMax / (2.0 * _globals.graph.getNodes().size());
            //System.out.println("\t\t" + _globals.sourceNode.getId() + "->" + _globals.targetNode.getId() + " best found = " + _globals.bestSoFar.getCost() + " at iteration " + _globals.iteration);
            if(this.pathListener != null) {
                this.pathListener.onBestRouteFound(getResultRoute(), getResultCost());
            }
        }
        if(iterationBestAnt.getCost() < _globals.restartBestAnt.getCost()) {
            _globals.restartBestAnt = iterationBestAnt.clone();
            _globals.restartFoundBestIteration = _globals.iteration;
        }
    }

    public Ant findBestAnt() {
        Ant bestAnt = _globals.ants[0];
        for(int i = 1; i < _globals.numberAnts; i++) {
            if(_globals.ants[i].getCost() < bestAnt.getCost()) {
                bestAnt = _globals.ants[i];
            }
        }
        return bestAnt;
    }

    private void pheromoneTrailUpdate() {
        pheromoneEvaporation();
        if(_globals.iteration % _globals.uGb == 0) {
            pheromoneUpdate(findBestAnt());
        } else if (_globals.uGb == 1 && (_globals.iteration - _globals.restartFoundBestIteration) > 50) {
            pheromoneUpdate(_globals.bestSoFar);
        } else {
            pheromoneUpdate(_globals.restartBestAnt);
        }
        checkPheromoneTrails();
        computeTotalInformation();
    }

    private void pheromoneEvaporation() {
        for(Edge edge : _globals.graph.getEdges()) {
            edge.setPheromone((1.0 - _globals.rho) * edge.getPheromone());
        }
    }

    private void pheromoneUpdate(Ant ant) {
        double dTau = 1.0 / ant.getCost();
        for(int i = 0; i < ant.getRoute().size() - 1; i++) {
            int fromId = ant.getRoute().get(i).getId();
            int toId = ant.getRoute().get(i + 1).getId();
            _globals.graph.getEdge(fromId, toId).setPheromone(dTau);
        }
    }

    private void checkPheromoneTrails() {
        for(Edge edge : _globals.graph.getEdges()) {
            if(edge.getPheromone() < _globals.trailMin) {
                edge.setPheromone(_globals.trailMin);
            }
            if(edge.getPheromone() > _globals.trailMax) {
                edge.setPheromone(_globals.trailMax);
            }
        }
    }

    private void searchControl() {
        if(_globals.iteration % 100 == 0) {
            double branchFactor = calculateBranchingFactor();
            //System.out.println("Branch factor = " + branchFactor + " at iteration " + _globals.iteration);
            if(branchFactor < _globals.branchFactor && (_globals.iteration - _globals.restartFoundBestIteration) > 250) {
                //System.out.println("Restarting System!");
                _globals.restartBestAnt = new Ant(_globals);
                initPheromoneTrails(_globals.trailMax);
                computeTotalInformation();
                _globals.restartFoundBestIteration = _globals.iteration;
            }
        }
    }

    private double calculateBranchingFactor() {
        double min, max, cutoff, avg = 0.0;
        List<Double> numBranches = new ArrayList<>();
        for(Node node : _globals.graph.getNodes()) {
            if(!node.getEdges().isEmpty()) {
                max = Double.MAX_VALUE * -1.0;
                min = Double.MAX_VALUE;
                for(Edge edges : node.getEdges()) {
                    if(edges.getPheromone() > max) {
                        max = edges.getPheromone();
                    }
                    if(edges.getPheromone() < min) {
                        min = edges.getPheromone();
                    }
                }
                cutoff = min + _globals.lambda * (max - min);
                double count = 0.0;
                for(Edge edges : node.getEdges()) {
                    if(edges.getPheromone() > cutoff) {
                        count += 1.0;
                    }
                }
                numBranches.add(count);
            }
        }
        for(Double branch : numBranches) {
            avg += branch;
        }
        return (avg / (numBranches.size() * 2.0));
    }

    public Integer[] getResultRoute() {
        Integer[] bestRoute = new Integer[_globals.bestSoFar.getRoute().size()];
        for(int i = 0; i < _globals.bestSoFar.getRoute().size(); i++) {
            bestRoute[i] = _globals.bestSoFar.getRoute().get(i).getId();
        }
        return bestRoute;
    }

    public double getResultCost() {
        return _globals.bestSoFar.getCost();
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public PathListener getPathListener() {
        return pathListener;
    }

    public void setPathListener(PathListener pathListener) {
        this.pathListener = pathListener;
    }

    public boolean isFinished() {
        return finished;
    }
}
