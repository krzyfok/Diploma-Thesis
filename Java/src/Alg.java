import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.random.*;
import java.io.FileWriter;
import java.io.IOException;


public class Alg {
    static class MemoryMonitor implements Runnable {
        volatile boolean running = true;
        long maxMem = 0;
        public void run() {
            Runtime rt = Runtime.getRuntime();
            while (running) {
                long used = rt.totalMemory() - rt.freeMemory();
                if (used > maxMem) maxMem = used;
                try { Thread.sleep(100); } catch (InterruptedException e) {}
            }
        }
        void stop() { running = false; }
    }
    public void startAlg(String fileName,int iter,ArrayList<ArrayList<Integer>> weights, int populationSize, int generationLimit, double mR, double cR, int elite, String selection, String mutation, String crossover,int seed, String initial, int tSize)
    {
        System.gc();


        Xoshiro256PlusPlus rng ;

        int size = weights.size();
        double bestPathFitnnes=0;
        int bestPathCost=0;
        int eliteSize= elite*populationSize/100;
        if (eliteSize % 2 != 0) {
            eliteSize++;
        }
        int newPopSize =populationSize-eliteSize;
        int generationNum=0;
        Path bestPath = null;


        if(seed ==0)
        {
            rng = new Xoshiro256PlusPlus(ThreadLocalRandom.current().nextLong());
        }
         else {

             rng = new Xoshiro256PlusPlus(seed);
        }


        ArrayList<Path> population = new ArrayList<>(populationSize);
        ArrayList<Path> newPopulation = new ArrayList<>(populationSize);
        if (initial.equals("hybrid")) {
            for (int i = 0; i < size; i++) {
                population.add(new Path(weights, i));
            }
            for (int i = population.size(); i < populationSize; i++) {

                population.add(new Path(weights,rng));
            }
        }
        else
        {
            for (int i = 0; i < populationSize; i++) {
                population.add(new Path(weights,rng));
            }
        }
        MemoryMonitor m = new MemoryMonitor();
        new Thread(m).start();
        long startTime = System.nanoTime();

        while(generationNum<generationLimit)
        {

            generationNum+=1;


            for(int i =0;i<populationSize;i++)
            {
                population.get(i).calcFitness(weights);

            }
            population.sort(Comparator.comparingDouble((Path p) -> p.fitness).reversed());
            if(bestPathFitnnes<population.get(0).fitness){
                bestPathFitnnes=population.get(0).fitness;
                bestPath = population.get(0);
                bestPathCost=bestPath.calcCost(weights);

            }


            if(selection.equals("tournament"))
            {
                newPopulation =tournamentSelection(population,newPopSize,tSize,rng);
            }
            else {
                newPopulation =rouletteSelection(population,newPopSize,rng);
            }

            for(int i =0;i<newPopSize;i+=2) {

                if(rng.nextDouble()<cR) {
                    ArrayList<Path> children;
                    if(crossover.equals("OX")) {
                        children = crossingOX(newPopulation.get(i), newPopulation.get(i + 1),rng);
                    }
                    else {
                        children = twoPointCrossover(newPopulation.get(i), newPopulation.get(i + 1),rng);
                       ;
                   }
                    newPopulation.set(i, children.get(0));
                    newPopulation.set(i+1, children.get(1));
                }


                if(mutation.equals("swap")) {
                    newPopulation.set(i, swapMutation(mR, newPopulation.get(i),rng));
                    newPopulation.set(i+1, swapMutation(mR, newPopulation.get(i + 1),rng));
                }
                else
                {
                    newPopulation.set(i, inversionMutation(mR, newPopulation.get(i),rng));
                    newPopulation.set(i+1, inversionMutation(mR, newPopulation.get(i + 1),rng));
                }
            }

            int difference = populationSize-newPopSize;

            for(int i=0;i<difference;i++)
            {

                newPopulation.add(population.get(i));
            }
            population=newPopulation;
        }

        long stopTime = System.nanoTime();
        double timeFinal = (stopTime-startTime)/ 1_000_000.0;
        m.stop();
        System.out.println("Max Memory: " + m.maxMem / (1024*1024) + " MB");

        System.out.println("Best cost: " + bestPathCost);
        System.out.println("TIME: "+timeFinal);


        File file = new File(fileName);
        try (FileWriter writer = new FileWriter(file, true)) {


            if (file.length() == 0) {
                writer.append("lp.;Time(ms);Cost;seed;populationSize;initialization;crossing;mutation;selection;cR;mR;eliteSize;generationLimit;tSize;Mem(Mb)\r\n");
            }


                writer.append(String.format("%d;%f;%d;%d;%d;%s;%s;%s;%s;%f;%f;%d;%d;%d;%.3f\n", iter, timeFinal, bestPathCost,seed,populationSize,initial,crossover,mutation,selection,cR,mR,eliteSize,generationLimit,tSize,m.maxMem / (1024.0*1024.0)));



        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<Path> crossingOX(Path parent1, Path parent2,Xoshiro256PlusPlus rng)
    {
        int size = parent1.cities.size();


        Path child1 = new Path(size);
        Path child2 = new Path(size);
        ArrayList<Path> result = new ArrayList<>();
        BitSet child1Set = new BitSet(size);
        BitSet child2Set= new BitSet(size);

        int crossPoint1 = rng.nextInt()%size;
        int crossPoint2 = rng.nextInt()%size;
        while (crossPoint1 == crossPoint2) crossPoint2 = rng.nextInt()%size;
        if (crossPoint1 > crossPoint2) {
            int temp = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = temp;
        }


        for (int i = crossPoint1; i <= crossPoint2; i++) {
            child1.cities.set(i, parent1.cities.get(i));
            child2.cities.set(i, parent2.cities.get(i));
            child1Set.set(child1.cities.get(i));
            child2Set.set(child2.cities.get(i));
        }



        int indexP = (crossPoint2 + 1) % size;
        int indexC = (crossPoint2 + 1) % size;
        while (indexC != crossPoint1) {
            int city = parent2.cities.get(indexP);
            if (!child1Set.get(city)) {
                child1.cities.set(indexC, city);
                child1Set.set(city);
                indexC = (indexC + 1) % size;
            }
            indexP = (indexP + 1) % size;
        }



        indexP = (crossPoint2 + 1) % size;
        indexC = (crossPoint2 + 1) % size;
        while (indexC != crossPoint1) {
            int city = parent1.cities.get(indexP);
            if (!child2Set.get(city)) {
                child2.cities.set(indexC, city);
                child2Set.set(city);
                indexC = (indexC + 1) % size;
            }
            indexP = (indexP + 1) % size;
        }

        result.add(child1);
        result.add(child2);

        return result;
    }

    private ArrayList<Path> twoPointCrossover(Path parent1, Path parent2,Xoshiro256PlusPlus rng)
    {
        int size = parent1.cities.size();


        Path child1 = new Path(size);
        Path child2 = new Path(size);
        ArrayList<Path> result = new ArrayList<>();

        int crossPoint1 = rng.nextInt()%size;
        int crossPoint2 = rng.nextInt()%size;
        while (crossPoint1 == crossPoint2) crossPoint2 = rng.nextInt()%size;
        if (crossPoint1 > crossPoint2) {
            int temp = crossPoint1;
            crossPoint1 = crossPoint2;
            crossPoint2 = temp;
        }
        BitSet child1Set = new BitSet(size);
        BitSet child2Set= new BitSet(size);
        for (int i = 0; i < size; i++) {

            if (i <= crossPoint1 || i >= crossPoint2) {
                int city1 = parent1.cities.get(i);
                int city2 = parent2.cities.get(i);

                child1.cities.set(i, city1);
                child2.cities.set(i, city2);

                child1Set.set(city1);
                child2Set.set(city2);
            }
        }


        int positionC1 = crossPoint1+1;
        int positionC2 = crossPoint1+1;
        for(int i= 0;i<size;i++)
        {
            int value = parent2.cities.get(i);
            if(!child1Set.get(value))
            {
                child1.cities.set(positionC1,value);
                child1Set.set(value);
                positionC1++;
            }
            value = parent1.cities.get(i);
            if(!child2Set.get(value))
            {
                child2.cities.set(positionC2,value);
                child2Set.set(value);
                positionC2++;
            }
        }

        result.add(child1);
        result.add(child2);

        return result;

    }




    private Path swapMutation(double mR,Path individual,Xoshiro256PlusPlus rng)
    {   int size = individual.cities.size();


        if(rng.nextDouble()<mR) {
            int p1 = rng.nextInt()%size;
            int p2 = rng.nextInt()%size;
            Collections.swap(individual.cities, p1, p2);
        }
        return  individual;

    }

    private Path inversionMutation(double mR,Path individual, Xoshiro256PlusPlus rng)
    {
        int size = individual.cities.size();

        if(rng.nextDouble()<mR) {

            int p1 = rng.nextInt()%size;
            int p2 = rng.nextInt()%size;

            if(p1>p2)
            {
                int temp = p1;
                p1=p2;
                p2=temp;
            }

            while(p1<p2)
            {
                Collections.swap(individual.cities, p1, p2);
                p1++;
                p2--;
            }
        }


        return individual;
    }



    private ArrayList<Path> rouletteSelection(ArrayList<Path>population, int newPopSize,Xoshiro256PlusPlus rng)
    {
        ArrayList<Path> selected = new ArrayList<>(newPopSize);
        double fitnessSum =0.0;

        for (Path p : population){
            fitnessSum+=p.fitness;}

        for (int i = 0 ;i<newPopSize;i ++)
        {
            double r =rng.nextDouble() * fitnessSum;
            double partialSum =0.0;

            for(Path p :population){
                partialSum+=p.fitness;
                if(partialSum>=r){
                     selected.add(p);
                    break;}
            }
        }
        return selected;

    }

    private ArrayList<Path> tournamentSelection(ArrayList<Path>population, int newPopSize, int tournamentSize, Xoshiro256PlusPlus rng)
    {
        ArrayList<Path> selected = new ArrayList<>(newPopSize);
        int popSize = population.size();

        for (int i = 0; i < newPopSize; i++) {
            double bestValue = 0;
            Path bestPath = null;

            for (int j = 0; j < tournamentSize; j++) {
                int index = rng.nextInt()%popSize;
                Path drawn = population.get(index);
                double fitness = drawn.fitness;
                if (fitness > bestValue) {
                    bestValue = fitness;
                    bestPath = drawn;
                }
            }
            selected.add(bestPath);
        }
        return selected;
    }
}
