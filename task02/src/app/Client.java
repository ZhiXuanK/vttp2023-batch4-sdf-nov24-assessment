package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.*;
import java.util.stream.Collectors;

public class Client {

    public static void main(String[] args) throws UnknownHostException, IOException {

        Socket socket = null;

        if (args.length == 0) {
            socket = new Socket("localhost", 3000);
        } else if (args.length == 1) {
            socket = new Socket("localhost", Integer.parseInt(args[0]));
        } else if (args.length == 2) {
            socket = new Socket(args[0], Integer.parseInt(args[1]));
        }

        //open buffered reader and writer
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        String reqId = null;
        Integer itemCount = null;
        Float budget = null;
        List<Product> productData = new ArrayList<>();
        String line = "";
        outer:
        while (true) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            String[] input = line.split(":");
            switch (input[0]) {
                case "request_id":
                    reqId = input[1];
                    break;
                case "item_count":
                    itemCount = Integer.parseInt(input[1].trim());
                    break;
                case "budget":
                    budget = Float.parseFloat(input[1].trim());
                    break;
                case "prod_list":
                    int counting = 0;
                    //prod_id, title, price, rating
                    String[] product = new String[4];
                    while (counting < itemCount) {
                        line = br.readLine();
                        input = line.split(":");
                        if (input[0].equals("prod_start")) {
                            product = new String[4];
                        } else if (input[0].equals("prod_id")) {
                            product[0] = input[1].trim();
                        } else if (input[0].equals("title")) {
                            product[1] = input[1].trim();
                        } else if (input[0].equals("price")) {
                            product[2] = input[1].trim();
                        } else if (input[0].equals("rating")) {
                            product[3] = input[1].trim();
                        } else if (input[0].equals("prod_end")) {
                            productData.add(new Product(Integer.parseInt(product[0].trim()), product[1], Float.parseFloat(product[2]), Float.parseFloat(product[3])));
                            counting += 1;
                        }
                    }
                    break outer;
            }
        }//above is code to read the file and load content into a List of product
        Comparator<Product> comparator = Comparator.comparing(Product::getRating).thenComparing(Product::getPrice);

        List<Product> sortedProduct = productData.stream().sorted(comparator).collect(Collectors.toList());

        List<Integer> itemIdPurchased = new ArrayList<>();
        Float spent = 0f;

        for (Product p:sortedProduct){
            if (p.getPrice() > budget){
                continue;
            } else {
                itemIdPurchased.add(p.getProdId());
                budget -= p.getPrice();
                spent += p.getPrice();
            }
        } //above is to sort products and determine which to purchase
        String itemString = "";

        for (int p:itemIdPurchased){
            itemString = itemString + String.valueOf(p) + ", ";
        } 
        itemString = itemString.substring(0, itemString.length() - 1);
        bw.write("request_id: " + reqId +"\n");
        bw.flush();
        bw.write("name: Kang Zhi Xuan\n" );
        bw.flush();
        bw.write("email: kangzhixuan@u.nus.edu\n");
        bw.flush();
        bw.write("items: " + itemString +"\n");
        bw.flush();
        bw.write("spent: " + String.valueOf(spent) + "\n");
        bw.flush();
        bw.write("remaining: " + String.valueOf(budget) + "\n");
        bw.flush();
        bw.write("client_end \n");
        bw.flush();

        System.out.println(br.readLine());

        bw.close();
        osw.close();
        os.close();
        br.close();
        isr.close();
        is.close();

        socket.close();

    }

}
