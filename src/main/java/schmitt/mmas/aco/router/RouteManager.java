package schmitt.mmas.aco.router;

import schmitt.mmas.graph.Graph;
import schmitt.mmas.graph.Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RouteManager {

    private Globals _globals;

    private Set<Route> routes;

    private Map<Integer, Set<Route>> routesIndex;

    private Map<String, Route> routesMapIndex;

    public RouteManager(Globals globals) {
        super();
        _globals = globals;
        routes = new HashSet<>();
        routesIndex = new HashMap<>();
        routesMapIndex = new HashMap<>();
    }

    public void addRoute(int from, int to) {
        String key = from + "->" + to;
        if(!routesMapIndex.containsKey(key)) {
            Graph graph = _globals.graph.clone();
            Route route = new Route(graph, _globals.graph.getNode(from), _globals.graph.getNode(to));
            routes.add(route);
            if(!routesIndex.containsKey(from)) {
                routesIndex.put(from, new HashSet<>());
            }
            routesIndex.get(from).add(route);
            routesMapIndex.put(key, route);
        }
    }

    public void removeRoute(int from, int to) {
        Route route = getRoute(from, to);
        routes.remove(route);
        routesIndex.get(from).remove(route);
        routesMapIndex.remove(from + "->" + to);
    }

    public Set<Route> getRoutes() {
        return routes;
    }

    public Set<Route> getRoutes(int from) {
        return routesIndex.get(from);
    }

    public Route getRoute(int from, int to) {
        return routesMapIndex.get(from + "->" + to);
    }
}
