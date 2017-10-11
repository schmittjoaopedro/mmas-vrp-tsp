package schmitt.mmas.aco.path;

public interface PathListener {

    void onBestRouteFound(Integer[] route, Double cost);

}
