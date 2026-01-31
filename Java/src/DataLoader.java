import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;

public class DataLoader {
    ArrayList<ArrayList<Integer>> matrix ;

    public ArrayList<ArrayList<Integer>> loadData(String fileName)
    {   matrix = new ArrayList<>();
        int size = 0;

        try{
            File myData = new File (fileName);
            Scanner dataReader = new Scanner(myData);

            while(dataReader.hasNext())
            {
                String data = dataReader.next();
                if(data.equals("DIMENSION:"))
                {
                    size = dataReader.nextInt();

                }
                if(data.equals("EDGE_WEIGHT_SECTION"))
                {
                    for(int i =0;i<size;i++)
                    {
                        ArrayList<Integer> row = new ArrayList<>();
                        for( int j=0 ; j<size; j++)
                        {
                            int weight = dataReader.nextInt();
                            row.add(weight);

                        }
                        matrix.add(row);
                    }
                }

            }

        } catch (FileNotFoundException e) {

            throw new RuntimeException(e);
        }
    return matrix;
    }

}
