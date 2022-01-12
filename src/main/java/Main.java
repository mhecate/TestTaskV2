import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        List<Transaction> list = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader("C:\\testovoezadanie\\Records.csv");
            CSVReader csvReader = new CSVReader(fileReader);
            String[] nextRecord;
            int row = 0;
            //System.out.print(inputDate);
            while ((nextRecord = csvReader.readNext()) != null) {
                if (row == 0) {
                    row++;
                    continue;
                }


                Transaction transaction = new Transaction();
                String date = nextRecord[0];

                    transaction.setDate(date);
                    transaction.setDesc(nextRecord[1]);
                    transaction.setDeposits(Double.parseDouble(nextRecord[2].replaceAll(",", "")));
                    transaction.setWithdrawls(Double.parseDouble(nextRecord[3].replaceAll(",", "")));
                    transaction.setBalance(Double.parseDouble(nextRecord[4].replaceAll(",", "")));
                    list.add(transaction);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int startIndex = 0;
        int endIndex = 0;

        Map<String, Map<String, Double>> map = list.stream()
                .collect(Collectors.groupingBy(Transaction::getDate,
                        Collectors.groupingBy(Transaction::getDesc,
                                Collectors.averagingDouble(Transaction::getWithdrawls))));
        Map<String, Map<String, Double>> map2 = list.stream()
                .collect(Collectors.groupingBy(Transaction::getDate,
                        Collectors.groupingBy(Transaction::getDesc,
                                Collectors.averagingDouble(Transaction::getWithdrawls))));
        System.out.println(map);

        /*try {
            File file = new File("copy1.csv");
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter("C:\\testovoezadanie\\TEST\\copy.csv");

            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputfile);

            String[] header = { "Date", "Type", "Min", "Max", "Avg" };
            writer.writeNext(header);
            writer.writeAll((Iterable<String[]>) map);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }


}
