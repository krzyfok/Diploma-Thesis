import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
 //Etap 2
public class Main {
    public static void main(String[] args) {
        String[] files = {"ftv47"};
        int[] seeds = {
                    1, 4, 9, 16, 25, 36, 49, 64, 81, 100,
                    121, 144, 169, 196, 225, 256, 289, 324, 361, 400,
                    441, 484, 529, 576, 625, 676, 729, 784, 841, 900,
                    961, 1024, 1089, 1156, 1225, 1296, 1369, 1444, 1521, 1600,
                    1681, 1764, 1849, 1936, 2025, 2116, 2209, 2304, 2401, 2500
            };

        int generationLimit = 20000;
        int eliteSize = 5;
        double mR = 0.01;
        double cR = 0.95;
        int tournamentSize = 2;

        List<Map<String, Object>> combinations = new ArrayList<>();

// Ftv170
//        combinations.add(Map.of(
//                "initialization", "hybrid",
//                "selection", "roulette",
//                "crossing", "OX",
//                "mutation", "swap",
//                "populationSize", 1000
//        ));
//        combinations.add(Map.of(
//                "initialization", "hybrid",
//                "selection", "roulette",
//                "crossing", "OX",
//                "mutation", "inversion",
//                "populationSize", 1000
//        ));
//        combinations.add(Map.of(
//                "initialization", "hybrid",
//                "selection", "roulette",
//                "crossing", "OX",
//                "mutation", "swap",
//                "populationSize", 2000
//        ));
//        combinations.add(Map.of(
//                "initialization", "hybrid",
//                "selection", "roulette",
//                "crossing", "OX",
//                "mutation", "inversion",
//                "populationSize", 2000
//        ));
 //Ftv47
        combinations.add(Map.of(
                "initialization", "random",
                "selection", "roulette",
                "crossing", "OX",
                "mutation", "swap",
                "populationSize", 1000
        ));
        combinations.add(Map.of(
                "initialization", "random",
                "selection", "roulette",
                "crossing", "OX",
                "mutation", "swap",
                "populationSize", 2000
        ));
        combinations.add(Map.of(
                "initialization", "random",
                "selection", "roulette",
                "crossing", "OX",
                "mutation", "inversion",
                "populationSize", 2000
        ));




        for (String name : files) {
            Alg alg = new Alg();
            DataLoader data = new DataLoader();

            String fileName = name + ".atsp";
            ArrayList<ArrayList<Integer>> graph = data.loadData(fileName);

            int runCounter = 1;

            for (Map<String, Object> combo : combinations) {

                String resFileName = String.format(
                        "%s_init=%s_sel=%s_cross=%s_mut=%s_pop=%d.csv",
                        name,
                        combo.get("initialization"),
                        combo.get("selection"),
                        combo.get("crossing"),
                        combo.get("mutation"),
                        combo.get("populationSize")
                );

                System.out.println("Uruchamianie kombinacji parametrów do pliku: " + resFileName);

                for (int seed : seeds) {
                    System.out.printf(
                            " Run %3d | seed=%d%n",
                            runCounter,
                            seed
                    );

                    alg.startAlg(
                            resFileName,
                            runCounter,
                            graph,
                            (int) combo.get("populationSize"),
                            generationLimit,
                            mR,
                            cR,
                            eliteSize,
                            (String) combo.get("selection"),
                            (String) combo.get("mutation"),
                            (String) combo.get("crossing"),
                            seed,
                            (String) combo.get("initialization"),
                            tournamentSize
                    );

                    runCounter++;
                }
            }
        }
    }
}


//import java.io.FileInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Properties;
////Etap 1
//public class Main {
//    public static void main(String[] args) {
//        String[] files = {"ftv47"};
//        for (String name : files) {
//            Alg alg = new Alg();
//
//            DataLoader data = new DataLoader();
//
//
//            String fileName = name+".atsp";
//            ArrayList<ArrayList<Integer>> graph = data.loadData(fileName);
//
//            int[] populationSizes;
//            int generationLimit = 20000;
//            int eliteSize = 5;
//            double mR = 0.01;
//            double cR = 0.95;
//            int tournamentSize = 2;
//
//            if (fileName.contains("47")) {
//                populationSizes = new int[]{1000, 2000};
//            } else if (fileName.contains("170")) {
//                populationSizes = new int[]{1000, 2000};
//            } else if (fileName.contains("443")) {
//                populationSizes = new int[]{1000, 2000};
//            } else {
//                populationSizes = new int[]{1000};
//            }
//
//
//            String[] initializations = {"random", "hybrid"};
//            String[] selections = {"tournament", "roulette"};
//            String[] crossovers = {"OX", "TPX"};
//            String[] mutations = {"swap", "inversion"};
//            int[] seeds = {2, 3, 5, 8, 13, 21, 34, 55, 89, 144};
//
//
//            int runCounter = 1;
//            for (int popSize : populationSizes) {
//                for (String init : initializations) {
//                    for (String select : selections) {
//                        for (String cross : crossovers) {
//                            for (String mut : mutations) {
//                                for (int seed : seeds) {
//
//                                    System.out.printf(
//                                            " Run %3d | init=%-7s | sel=%-10s | cross=%-3s | mut=%-9s | seed=%d%n",
//                                            runCounter, init, select, cross, mut, seed
//                                    );
//
//                                    alg.startAlg(
//                                            name + "res.csv",
//                                            runCounter,
//                                            graph,
//                                            popSize,
//                                            generationLimit,
//                                            mR,
//                                            cR,
//                                            eliteSize,
//                                            select,
//                                            mut,
//                                            cross,
//                                            seed,
//                                            init,
//                                            tournamentSize
//                                    );
//
//                                    runCounter++;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//        }
//
//    }
//
//}