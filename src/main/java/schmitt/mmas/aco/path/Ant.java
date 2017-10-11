package schmitt.mmas.aco.path;

import schmitt.mmas.graph.Edge;
import schmitt.mmas.graph.Node;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class Ant {

    private Globals _globals;

    private Stack<Node> route;

    private Set<Node> visited;

    private double cost;

    public Ant(Globals globals) {
        _globals = globals;
        route = new Stack<>();
        visited = new HashSet<>();
        cost = Double.MAX_VALUE;
    }

    public void heuristicTour() {
        visited = new HashSet<>();
        route = new Stack<>();
        cost = Double.MAX_VALUE;
        Node current = _globals.sourceNode;
        route.add(current);
        visited.add(current);
        while (current != _globals.targetNode) {
            Node nextNode = selectNextHeuristicNode(current);
            if(nextNode == null) {
                route.pop();
                current = route.peek();
            } else {
                route.push(nextNode);
                visited.add(nextNode);
                if(nextNode == _globals.targetNode) {
                    break;
                } else {
                    current = nextNode;
                }
            }
            if(route.size() > _globals.lifeTime) {
                Ant ant = _globals.nnAnt.clone();
                this.setRoute(ant.getRoute());
                this.setVisited(ant.getVisited());
                break;
            }
        }
        calculateCost();
    }

    public void nnTour() {
        visited = new HashSet<>();
        route = new Stack<>();
        cost = Double.MAX_VALUE;
        Node current = _globals.sourceNode;
        route.add(current);
        visited.add(current);
        while (current != _globals.targetNode) {
            Node nextNode = selectNextNearNode(current);
            if(nextNode == null) {
                route.pop();
                current = route.peek();
            } else {
                route.push(nextNode);
                visited.add(nextNode);
                if(nextNode == _globals.targetNode) {
                    break;
                } else {
                    current = nextNode;
                }
            }
        }
        calculateCost();
    }

    private Node selectNextNearNode(Node currentNode) {
        if(currentNode.getEdges().isEmpty()) return null;
        double maxGain = 0.0;
        Node nextNode = null;
        for(Edge edge : currentNode.getEdges()) {
            if(!visited.contains(edge.getTo()) && _globals.HEURISTIC(edge) >= maxGain) {
                nextNode = edge.getTo();
                maxGain = _globals.HEURISTIC(edge);
            }
        }
        return nextNode;
    }

    private Node selectNextHeuristicNode(Node currentNode) {
        if(currentNode.getEdges().isEmpty()) return null;
        Edge edges[] = currentNode.getEdges().toArray(new Edge[] {});
        double[] probabilities = new double[currentNode.getEdges().size()];
        double cumulativeSum = 0.0;
        for(int i = 0; i < edges.length; i++) {
            if(visited.contains(edges[i].getTo())) {
                probabilities[i] = 0.0;
            } else {
                probabilities[i] = edges[i].getTotal();
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
            if(i == edges.length) {
                return null;
            } else {
                return edges[i].getTo();
            }
        }
    }

    public void calculateCost() {
        cost = 0.0;
        for(int i = 0; i < route.size() - 1; i++) {
            cost += _globals.calculateDistanceInMeters(route.get(i), route.get(i + 1));
        }
    }

    public Ant clone() {
        Ant ant = new Ant(_globals);
        ant.setCost(this.getCost());
        for(int i = 0; i < getRoute().size(); i++) {
            ant.getRoute().push(getRoute().get(i));
        }
        for(Node node : getVisited()) {
            ant.getVisited().add(node);
        }
        return ant;
    }

    public Stack<Node> getRoute() {
        return route;
    }

    public void setRoute(Stack<Node> route) {
        this.route = route;
    }

    public Set<Node> getVisited() {
        return visited;
    }

    public void setVisited(Set<Node> visited) {
        this.visited = visited;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
