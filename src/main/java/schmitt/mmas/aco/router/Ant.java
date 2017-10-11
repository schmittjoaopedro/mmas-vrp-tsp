package schmitt.mmas.aco.router;

import schmitt.mmas.graph.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Ant {

    private Globals _globals;

    private Stack<Node> tour;

    private Set<Node> visited;

    private double cost;

    public Ant(Globals globals) {
        tour = new Stack<>();
        visited = new HashSet<>();
        _globals = globals;
    }

    public void nnTour() {
        tour = new Stack<>();
        visited = new HashSet<>();
        tour.push(_globals.sourceNode);
        visited.add(_globals.sourceNode);
        Node currentNode = _globals.sourceNode;
        while(tour.size() != _globals.targetNodes.size()) {
            Node nextNode = selectNextNearNode(currentNode);
            if(nextNode == null) {
                cost = Double.MAX_VALUE;
                break;
            } else {
                tour.push(nextNode);
                visited.add(nextNode);
                currentNode = nextNode;
            }
        }
        tour.push(_globals.sourceNode);
        computeCost();
    }

    private Node selectNextNearNode(Node currentNode) {
        Route[] routes = _globals.routeManager.getRoutes(currentNode.getId()).toArray(new Route[] {});
        Route selectedRoute = null;
        for(int i = 0; i < routes.length; i++) {
            if(!visited.contains(routes[i].getTargetNode()) &&
                    (selectedRoute == null || routes[i].getBestCost() < selectedRoute.getBestCost())) {
               selectedRoute = routes[i];
            }
        }
        return selectedRoute.getTargetNode();
    }

    public void heuristicTour() {
        tour = new Stack<>();
        visited = new HashSet<>();
        tour.push(_globals.sourceNode);
        visited.add(_globals.sourceNode);
        Node currentNode = _globals.sourceNode;
        while(tour.size() != _globals.targetNodes.size()) {
            Node nextNode = selectNextHeuristicNode(currentNode);
            if(nextNode == null) {
                cost = Double.MAX_VALUE;
                return;
            } else {
                tour.push(nextNode);
                visited.add(nextNode);
                currentNode = nextNode;
            }
        }
        tour.push(_globals.sourceNode);
        computeCost();
    }

    private Node selectNextHeuristicNode(Node currentNode) {
        Route[] routes = _globals.routeManager.getRoutes(currentNode.getId()).toArray(new Route[] {});
        Double[] probabilities = new Double[routes.length];
        Double cumulativeSum = 0.0;
        for(int i = 0; i < routes.length; i++) {
            if(visited.contains(routes[i].getTargetNode())) {
                probabilities[i] = 0.0;
            } else {
                probabilities[i] = routes[i].getTotal();
                cumulativeSum += probabilities[i];
            }
        }
        if(cumulativeSum <= 0.0) {
            return null;
        } else {
            double rand = Math.random() * cumulativeSum;
            int i = 0;
            double partialSum = probabilities[i];
            while (partialSum <= rand) {
                i++;
                partialSum += probabilities[i];
            }
            if(i == routes.length) {
                return null;
            } else {
                return routes[i].getTargetNode();
            }
        }
    }

    public void computeCost() {
        cost = 0.0;
        for(int i = 0; i < tour.size() - 1; i++) {
            cost += _globals.routeManager.getRoute(tour.get(i).getId(), tour.get(i + 1).getId()).getBestCost();
        }
    }

    public Stack<Node> getTour() {
        return tour;
    }

    public Set<Node> getVisited() {
        return visited;
    }

    public double getCost() {
        return cost;
    }

    public void setTour(Stack<Node> tour) {
        this.tour = tour;
    }

    public void setVisited(Set<Node> visited) {
        this.visited = visited;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Ant clone() {
        Ant ant = new Ant(_globals);
        ant.setCost(this.getCost());
        for(int i = 0; i < getTour().size(); i++) {
            ant.getTour().push(getTour().get(i));
        }
        for(Node node : getVisited()) {
            ant.getVisited().add(node);
        }
        return ant;
    }
}
