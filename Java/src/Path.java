import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;
import java.util.random.RandomGenerator;

public class Path {
    public ArrayList<Integer> cities;
    public double fitness;
    public Path(ArrayList<ArrayList<Integer>> weights, Xoshiro256PlusPlus rng)
    {
        cities = new ArrayList<>();
        for(int i =0 ; i<weights.size(); i++)
        {
            cities.add(i);
        }
        shuffle(cities,rng);
        calcFitness(weights);
    }
    public Path(ArrayList<ArrayList<Integer>> weights, int index)
    {
        cities = new ArrayList<>();
        cities.add(index);
        int bestConnection = Integer.MAX_VALUE;
        int bestVertex=-1;
        int size = weights.size();
        for(int i=1;i<size;i++)
        {
            bestConnection = Integer.MAX_VALUE;
            bestVertex=-1;
            for(int k=0;k<size;k++)
            {
                if(!cities.contains(k) && bestConnection>weights.get(cities.get(i-1)).get(k))
                {
                    bestConnection = weights.get(cities.get(i-1)).get(k);
                    bestVertex=k;
                }

            }
            cities.add(bestVertex);
        }
        calcFitness(weights);
    }


    public Path(int size)
    {
        fitness=0;
        cities = new ArrayList<>(size);
        for(int i=0;i<size;i++)
        {
            cities.add(-1);
        }
    }

    public static void shuffle(ArrayList<Integer> list, Xoshiro256PlusPlus rng) {
        int n = list.size();
        for (int i = n - 1; i > 0; i--) {
            int j = (int) Math.floorMod(rng.nextLong(), i + 1);
            int temp = list.get(i);
            list.set(i, list.get(j));
            list.set(j, temp);
        }
    }


    public void calcFitness(ArrayList<ArrayList<Integer>> weights)
    {
        int result =0;
        for(int i =0;i<cities.size()-1;i++)
        {

            result+=weights.get(cities.get(i)).get(cities.get(i+1));
        }
        result +=weights.get(cities.get(cities.size()-1)).get(cities.get(0));;
        fitness = (double)1/result;


    }
    public int calcCost(ArrayList<ArrayList<Integer>> weights)
    {
        int result =0;
        for(int i =0;i<cities.size()-1;i++)
        {

            result+=weights.get(cities.get(i)).get(cities.get(i+1));
        }
        result +=weights.get(cities.get(cities.size()-1)).get(cities.get(0));
        return result;


    }


}
