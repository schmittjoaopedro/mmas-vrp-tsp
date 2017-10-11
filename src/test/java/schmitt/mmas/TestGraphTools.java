package schmitt.mmas;

import org.junit.Test;
import schmitt.mmas.aco.path.PathSolver;
import schmitt.mmas.aco.path.Statistics;
import schmitt.mmas.aco.router.Route;
import schmitt.mmas.aco.router.RouteSolver;
import schmitt.mmas.graph.Graph;
import schmitt.mmas.graph.Node;
import schmitt.mmas.reader.JSONConverter;
import schmitt.mmas.utils.*;
import schmitt.mmas.utils.Timer;
import schmitt.mmas.view.Visualizer;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public class TestGraphTools {

    @Test
    public void testReaderJoinvilleJson() {
        ClassLoader classLoader = getClass().getClassLoader();
        String jsonFile = classLoader.getResource("joinville.json").getFile().toString();
        Graph graph = JSONConverter.readGraph(jsonFile);

        assertThat(graph).isNotNull();
        assertThat(graph.getNodesLength()).isEqualTo(2796);
        assertThat(graph.getEdgesLength()).isEqualTo(6178);

        /**
         * Test route only forward
         */

        assertThat(graph.getNode(868).getX()).isEqualTo(-26.32767076777676);
        assertThat(graph.getNode(868).getY()).isEqualTo(-48.864337217755704);
        assertThat(graph.getNode(868).getEdges().size()).isEqualTo(1);
        assertThat(graph.getEdge(0, 868)).isNull();
        assertThat(graph.getEdge(868, 0).getDistance()).isEqualTo(405.112);

        assertThat(graph.getNode(0).getX()).isEqualTo(-26.33074934774822);
        assertThat(graph.getNode(0).getY()).isEqualTo(-48.862193499405535);
        assertThat(graph.getNode(0).getEdges().size()).isEqualTo(2);

        assertThat(graph.getNode(1239).getX()).isEqualTo(-26.333280123769047);
        assertThat(graph.getNode(1239).getY()).isEqualTo(-48.861051884168056);
        assertThat(graph.getNode(1239).getEdges().size()).isEqualTo(1);
        assertThat(graph.getEdge(1239, 0)).isNull();
        assertThat(graph.getEdge(0, 1239).getDistance()).isEqualTo(303.545);

        assertThat(graph.getNode(1321).getX()).isEqualTo(-26.331491984385817);
        assertThat(graph.getNode(1321).getY()).isEqualTo(-48.862099435823396);
        assertThat(graph.getNode(1321).getEdges().size()).isEqualTo(1);
        assertThat(graph.getEdge(1321, 0)).isNull();
        assertThat(graph.getEdge(0, 1321).getDistance()).isEqualTo(83.107);

        /**
         * Test a route with a curve, to check if the distance is not Euclidean (220m).
         * Test if route is forward and backward
         */
        assertThat(graph.getNode(346).getX()).isEqualTo(-26.307000249035664);
        assertThat(graph.getNode(346).getY()).isEqualTo(-48.85931142850171);
        assertThat(graph.getNode(346).getEdges().size()).isEqualTo(3);

        assertThat(graph.getNode(358).getX()).isEqualTo(-26.306899479930838);
        assertThat(graph.getNode(358).getY()).isEqualTo(-48.861525741104614);
        assertThat(graph.getNode(358).getEdges().size()).isEqualTo(4);

        assertThat(graph.getEdge(346, 358).getDistance()).isEqualTo(306.948);
        assertThat(graph.getEdge(358, 346).getDistance()).isEqualTo(306.948);

    }

    @Test
    public void testVisualizerMap() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        String jsonFile = classLoader.getResource("joinville.json").getFile().toString();
        Graph graph = JSONConverter.readGraph(jsonFile);

        Visualizer visualizer = new Visualizer(graph, null);
        visualizer.draw(null);

        while(true) {
            Thread.sleep(100);
        }
    }

    @Test
    public void testSimpleHeuristicGraph() throws Exception {

        Graph graph = new Graph();
        graph.addNode(0, -8, -0);
        graph.addNode(1, -6, -1);
        graph.addNode(2, -3, -1);
        graph.addNode(3, -4, -3);
        graph.addNode(4, -1, -2);
        graph.addNode(5, -1, -3);
        graph.addNode(6, -1, -5);
        graph.addNode(7, -6, -5);
        graph.addNode(8, -3, -4);

        graph.addEdge(1, 0, 4.0);
        graph.addEdge(1, 2, 7.0);
        graph.addEdge(1, 3, 6.0);
        graph.addEdge(1, 7, 9.5);
        graph.addEdge(2, 1, 7.0);
        graph.addEdge(2, 4, 5.0);
        graph.addEdge(3, 2, 5.0);
        graph.addEdge(3, 4, 8.0);
        graph.addEdge(3, 7, 7.0);
        graph.addEdge(4, 5, 3.0);
        graph.addEdge(5, 2, 7.0);
        graph.addEdge(5, 6, 5.0);
        graph.addEdge(5, 7, 13.0);
        graph.addEdge(5, 8, 6.0);
        graph.addEdge(6, 7, 12.0);
        graph.addEdge(8, 3, 4.0);
        graph.addEdge(8, 6, 6.0);

        List<Node> target = new ArrayList<>();
        target.add(graph.getNode(1));
        target.add(graph.getNode(6));
        target.add(graph.getNode(3));
        RouteSolver routeSolver = new RouteSolver(graph, graph.getNode(1), target);
        routeSolver.setup();
    }

    @Test
    public void testSimpleHeuristicJoinville() throws Exception {
        //553, 1201, 43, 171, 103, 1980, 155, 2336

        ClassLoader classLoader = getClass().getClassLoader();
        String jsonFile = classLoader.getResource("joinville.json").getFile().toString();
        Graph graph = JSONConverter.readGraph(jsonFile);

        List<Node> target = new ArrayList<>();
        target.add(graph.getNode(553));
        target.add(graph.getNode(1201));
        target.add(graph.getNode(43));
        target.add(graph.getNode(171));
        target.add(graph.getNode(103));
        target.add(graph.getNode(1980));
        target.add(graph.getNode(155));
        target.add(graph.getNode(2336));
        target.add(graph.getNode(420));
        target.add(graph.getNode(310));
        target.add(graph.getNode(1500));
        target.add(graph.getNode(2100));
        target.add(graph.getNode(16));
        target.add(graph.getNode(720));
        target.add(graph.getNode(1700));

        Set<Integer> landmarks = new HashSet<>();
        for(int i = 0; i < target.size(); i++) landmarks.add(target.get(i).getId());
        Visualizer visualizer = new Visualizer(graph, landmarks);
        visualizer.draw(null);
        Thread.sleep(2000);

        Timer timer = new Timer();
        timer.startTimer();
        RouteSolver routeSolver = new RouteSolver(graph, graph.getNode(553), target);
        routeSolver.setup();
        routeSolver.setRouteListener(visualizer);
        routeSolver.solve();
        visualizer.draw(routeSolver.getResultRoute());

        visualizer.setStat("Cost = " + routeSolver.getResultCost());
        System.out.println("Finished in " + (timer.elapsedTime() / 1000.0));

        Stack<Node> tour = routeSolver.getBestSoFar().getTour();
        for(int i = 0; i < tour.size() - 1; i++) {
            String msg = tour.get(i).getId() + "->" + tour.get(i+1).getId() + " = ";
            Route route = routeSolver.getRouteManager().getRoute(tour.get(i).getId(), tour.get(i+1).getId());
            for(int node : route.getBestRoute()) {
                msg += node + "->";
            }
            System.out.println(msg.substring(0, msg.length() - 2));
        }

        while(true) {
            Thread.sleep(100);
        }

    }

    @Test
    public void testStatisticsHeuristicJoinville() throws Exception {
        //553->1201
        //43->171
        //103->1980
        //155->2336
        int[][] testCases = {
            {553 , 1201},
            {1201, 553 },
            {43  , 171 },
            {171 , 43  },
            {103 , 1980},
            {1980, 103 },
            {155 , 2336},
            {2336, 155 }
        };

        for(int t = 0; t < testCases.length; t++) {

            int fromId = testCases[t][0];
            int toId = testCases[t][1];

            ClassLoader classLoader = getClass().getClassLoader();
            String jsonFile = classLoader.getResource("joinville.json").getFile().toString();
            Graph graph = JSONConverter.readGraph(jsonFile);

            Map<Integer, Double[]> iterationMean = new TreeMap<>();
            Map<Integer, Double[]> iterationBest = new TreeMap<>();
            Map<Integer, Double[]> iterationWorst = new TreeMap<>();
            Map<Integer, Double[]> iterationBestSoFar = new TreeMap<>();
            int trialSize = 30;

            for (int i = 0; i < trialSize; i++) {
                //Ant Heuristic
                System.out.println("Trail " + (i + 1));
                LogFile.writeInFile(fromId, toId, "Trail " + (i + 1));
                PathSolver pathSolver = new PathSolver(graph, graph.getNode(fromId), graph.getNode(toId));
                pathSolver.solve();

                Statistics statistics = pathSolver.getStatistics();
                for (Integer iteration : statistics.getIterationMean().keySet()) {
                    //Mean
                    if (!iterationMean.containsKey(iteration)) {
                        iterationMean.put(iteration, new Double[trialSize]);
                    }
                    iterationMean.get(iteration)[i] = statistics.getIterationMean().get(iteration);
                    //Best
                    if (!iterationBest.containsKey(iteration)) {
                        iterationBest.put(iteration, new Double[trialSize]);
                    }
                    iterationBest.get(iteration)[i] = statistics.getIterationBest().get(iteration);
                    //Worst
                    if (!iterationWorst.containsKey(iteration)) {
                        iterationWorst.put(iteration, new Double[trialSize]);
                    }
                    iterationWorst.get(iteration)[i] = statistics.getIterationWorst().get(iteration);
                    //Best so far
                    if (!iterationBestSoFar.containsKey(iteration)) {
                        iterationBestSoFar.put(iteration, new Double[trialSize]);
                    }
                    iterationBestSoFar.get(iteration)[i] = statistics.getIterationBestSoFar().get(iteration);
                }
            }

            System.out.println("Final result");
            LogFile.writeInFile(fromId, toId, "Final result");
            for (Integer iteration : iterationMean.keySet()) {
                String msg = String.format("%05d, %05d, %05d, %05d, %05d,",
                        iteration,
                        (int) mean(iterationMean.get(iteration)),
                        (int) mean(iterationBest.get(iteration)),
                        (int) mean(iterationWorst.get(iteration)),
                        (int) mean(iterationBestSoFar.get(iteration)));
                System.out.println(msg);
                LogFile.writeInFile(fromId, toId, msg);
            }
        }
    }

    private double mean(Double[] values) {
        if(values.length == 0) return 0;
        double sum = values[0];
        for(int i = 1; i < values.length; i++) {
            sum += values[i];
        }
        return sum / values.length;
    }

}
