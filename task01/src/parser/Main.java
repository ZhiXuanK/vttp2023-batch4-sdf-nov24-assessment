package parser;

import java.io.*;
import java.util.*;


public class Main {

    public static void main(String[] args) {
        
        if (!args[0].substring(args[0].lastIndexOf(".")).equals(".csv")){
            //System.out.println(args[0].substring(args[0].lastIndexOf(".")).equals(".csv"));
            System.err.println("Please input a CSV file");
            System.exit(0);
        }

        //create a reader to read csv file
        File file = new File(args[0]);
        try (Reader reader = new FileReader(file);
        BufferedReader br = new BufferedReader(reader);) {
            String[] line = br.readLine().split(",");

            //App - categories[0], Category - cateogories[1], Rating - categories[2]
            int[] columns = new int[3];

            //get columns where each category is located in
            for (int i = 0; i < line.length; i ++){
                if (line[i].equals("App")){
                    columns[0] = i;
                } else if (line[i].equals("Category")){
                    columns[1] = i;
                } else if (line[i].equals("Rating")){
                    columns[2] = i;
                }
            }

            
            List<Map<String, String>> data = new ArrayList<>();
            Set<String> catList = new HashSet<>();

            int count = 1;
            while (true){
                String temp = br.readLine();
                if (temp == null){
                    break;
                }
                line = temp.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                count += 1;
                Map<String, String> rows = new HashMap<>();
                for (int k = 0; k < 3; k ++){
                    for (int j = 0; j < line.length; j ++){
                        String val = line[k].toLowerCase();
                        if (columns[k] == j && j == 0){
                            rows.put("App", val);
                        } else if (columns[k] == j && j == 1){
                            rows.put("Category", val);
                            catList.add(val);
                        } else if (columns[k] == j && j == 2){
                            rows.put("Rating", val);
                        }
                    }
                }
                data.add(rows);
            }
            //data contains all the rows of the CSV grouped into lists of dictonaries
            //catList is a set containing all the categories

            Map<String, Object[]> analysis = new HashMap<>();
            

            for (String c:catList){
                //List<Object> catAnalysis = new ArrayList<>();
                Object[] catAnalysis = new Object[7];
                //[avg rating, highest name, highest rating, lowest name, lowest rating, total app, discarded]
                catAnalysis[2] = -5f;
                catAnalysis[4] = -5f;
                float totalRating = 0;
                float goodApp = 0;
                int discard = 0;
                for (int i = 0; i < data.size(); i++){
                    Map<String, String> currData = data.get(i);
                    if (currData.get("Category").equals(c)){
                        //check if the cateogory of the current row matches the category we are looking at
                        //check if rating is valid
                        if (isFloat(currData.get("Rating"))){
                            //check that rating has a valid float value
                            Float rating = Float.parseFloat(currData.get("Rating"));
                            totalRating += rating;
                            goodApp += 1;
                            //check if highest rating
                            if (rating > (float) catAnalysis[2]){
                                catAnalysis[1] = currData.get("App");
                                catAnalysis[2] = rating;
                            }
                            //check if lowest rating
                            if ((float)catAnalysis[4] < 0 || rating < (float) catAnalysis[4]){
                                catAnalysis[3] = currData.get("App");
                                catAnalysis[4] = rating;
                            }
                        } else {
                            discard += 1;
                        }
                    }
                }
                catAnalysis[0] = totalRating/goodApp;
                catAnalysis[5] = goodApp + discard;
                catAnalysis[6] = discard;

                analysis.put(c, catAnalysis);
            }

            analysis.forEach((k,v) -> System.out.printf("Category: %s \n   Highest: %s, (%f) \n   Lowest: %s, (%f) \n   Average: %f \n   Count: %f \n   Discarded: %d \n", k, v[1], v[2], v[3], v[4], v[0], v[5], v[6]));

            System.out.println("Total lines in file: " + count);




        } catch (Exception e) {
            e.printStackTrace();

        }
        
    }


    public static boolean isFloat(String s){
        try {
            Float.parseFloat(s);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
}
