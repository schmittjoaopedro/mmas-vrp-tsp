package schmitt.mmas.view;

import schmitt.mmas.aco.path.PathListener;
import schmitt.mmas.aco.router.RouteListener;
import schmitt.mmas.graph.Edge;
import schmitt.mmas.graph.Graph;
import schmitt.mmas.graph.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Set;

/**
 * Thanks: http://www1.cs.columbia.edu/~bert/courses/3137/hw3_files/GraphDraw.java
 */
public class Visualizer extends JFrame implements PathListener, RouteListener {

    private int viewWidth;
    private int viewHeight;
    private int width;
    private int height;
    private double scaleW;
    private double scaleH;

    private ArrayList<ViewNode> nodes;
    private ArrayList<ViewEdge> edges;
    private ArrayList<ViewEdge> route;

    private Graph graph;

    private JLabel stats;

    private Integer[] bestRoute;

    private Set<Integer> landmarks;

    private boolean drawing = false;

    public Visualizer(Graph graph, Set<Integer> landmarks) {
        super();
        this.graph = graph;
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.stats = new JLabel();
        this.pack();
        this.setLocationRelativeTo(null);
        this.add(stats, BorderLayout.SOUTH);
        //this.setSize(800, 600);
        this.landmarks = landmarks;
        this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        this.setVisible(true);
        try {
            Thread.sleep(1000);
        } catch (Exception ex) {
        }
        nodes = new ArrayList<ViewNode>();
        edges = new ArrayList<ViewEdge>();
        route = new ArrayList<ViewEdge>();

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent evt) {
                Component c = (Component) evt.getSource();
                viewWidth = c.getWidth();
                viewHeight = c.getHeight();
                width = 1;
                height = 1;
                scaleW = viewWidth / Math.abs(graph.getUpperX() - graph.getLowerX());
                scaleH = viewHeight / Math.abs(graph.getUpperY() - graph.getLowerY());
                scaleW *= .9;
                scaleH *= .9;
                draw(bestRoute);
            }
        });

        viewWidth = this.getWidth();
        viewHeight = this.getHeight();
        width = 1;
        height = 1;
        scaleW = viewWidth / Math.abs(graph.getUpperX() - graph.getLowerX());
        scaleH = viewHeight / Math.abs(graph.getUpperY() - graph.getLowerY());
        scaleW *= .9;
        scaleH *= .9;
    }

    public void draw(Integer[] tour) {
        this.nodes.clear();
        this.edges.clear();
        this.route.clear();
        for (Node node : graph.getNodes()) {
            int x = (int) (scaleW * (node.getX() - graph.getLowerX()));
            int y = (int) (scaleH * (graph.getUpperY() - node.getY()));
            this.addNode(String.valueOf(node.getId()), x, y);
            for (Edge edges : node.getEdges()) {
                this.addEdge(node.getId(), edges.getTo().getId());
            }
        }
        if(tour != null) {
            bestRoute = tour;
            for (int i = 0; i < tour.length - 1; i++) {
                this.addRoute(tour[i], tour[i + 1]);
            }
        }
        this.repaint();
    }

    public void setStat(String text) {
        this.stats.setText(text);
    }

    class ViewNode {
        int x, y;
        String name;

        public ViewNode(String myName, int myX, int myY) {
            x = myX;
            y = myY;
            name = myName;
        }
    }

    class ViewEdge {
        int i, j;

        public ViewEdge(int ii, int jj) {
            i = ii;
            j = jj;
        }
    }

    // Add a node at pixel (x,y)
    public void addNode(String name, int x, int y) {
        nodes.add(new ViewNode(name, x, y));
    }

    // Add an ViewEdge between nodes i and j
    public void addEdge(int i, int j) {
        edges.add(new ViewEdge(i, j));
    }

    // Add an ViewEdge between nodes i and j
    public void addRoute(int i, int j) {
        route.add(new ViewEdge(i, j));
    }

    // Clear and repaint the nodes and edges
    public synchronized void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        if (edges != null) {
            ViewEdge edgesV[] = edges.toArray(new ViewEdge[] {});
            ViewEdge routesV[] = route.toArray(new ViewEdge[] {});
            ViewNode nodeV[] = nodes.toArray(new ViewNode[] {});
            FontMetrics f = g.getFontMetrics();
            g.setColor(Color.black);
            for (ViewEdge e : edgesV) {
                g2.setStroke(new BasicStroke(1));
                g.drawLine(nodeV[e.i].x, nodeV[e.i].y, nodeV[e.j].x, nodeV[e.j].y);
            }
            g.setColor(Color.blue);
            for (ViewEdge e : routesV) {
                g2.setStroke(new BasicStroke(3));
                g.drawLine(nodeV[e.i].x, nodeV[e.i].y, nodeV[e.j].x, nodeV[e.j].y);
            }
            for (ViewNode n : nodeV) {
                if(landmarks != null && landmarks.contains(Integer.parseInt(n.name))) {
                    g.setColor(Color.red);
                    g.drawOval(n.x - 4, n.y - 4, 8, 8);
                } else {
                    g.setColor(Color.black);
                    g.drawOval(n.x - 1, n.y - 1, 2, 2);
                }
            }
        }
    }

    @Override
    public void onBestRouteFound(Integer[] route, Double cost) {
        this.draw(route);
        this.setStat("Cost = " + cost);
        try {
            Thread.sleep(500);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBestTour(Integer[] tour, Double cost) {
        if(!this.drawing) {
            try {
                this.drawing = true;
                this.draw(tour);
                this.setStat("Cost = " + cost);
                Thread.sleep(500);
            } catch (Exception ex) {
            } finally {
                this.drawing = false;
            }
        }
    }
}
