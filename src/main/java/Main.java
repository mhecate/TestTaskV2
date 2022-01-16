import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;


import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        //peremennaya dlya rasscheta vremeni ispolneniya koda
        long time = System.currentTimeMillis();

        //peremennaya dlya togo chto by zamerit vydelenie pamyati do i posle ispolneniya koda
        Runtime runtime = Runtime.getRuntime();
        long usedMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Used Memory before " + usedMemoryBefore + " mb");

        //list tranzakcii, kuda budut zapisyvat'sya dannye vo vremya chteniya dannyh c CSV
        List<Transaction> transactionList = new ArrayList<>();

        //chtenie i zapis dannyh v list
        try {
            FileReader fileReader = new FileReader("C:\\testovoezadanie\\Records.csv");
            CSVReader csvReader = new CSVReader(fileReader);

            String[] nextRecord;
            int row = 0;

            while ((nextRecord = csvReader.readNext()) != null) {
                if (row == 0) {
                    row++;
                    continue;
                }
                Transaction transaction = new Transaction();
                transaction.setDate(nextRecord[0]);
                transaction.setDesc(nextRecord[1]);
                transaction.setDeposits(Double.parseDouble(nextRecord[2].replaceAll(",", "")));
                transaction.setWithdrawls(Double.parseDouble(nextRecord[3].replaceAll(",", "")));
                transaction.setBalance(Double.parseDouble(nextRecord[4].replaceAll(",", "")));
                transactionList.add(transaction);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Collectors.summarizingDouble(Transaction::getWithdrawls) - sozdaetsya nabor  Mapa,
        // v kotoryi pomeshyaiutsya withdrawl dlya vseh description   po kazhdoi date
        // ya ispolzovala takoi hod, potomu cto dolgo obdumyvala chto u nas est dva klucha eto date i description
        // i reshila sozdat map v mape s kotoroi ya mogu ispolzovat eti kluchi
        // a DoubleSummaryStatistics daet vozmozhnost vypolnit raznye statisticheskie operacii nad naborom
        Map<String, Map<String, DoubleSummaryStatistics>> summaryStatisticMap = transactionList.stream()
                .collect(Collectors.groupingBy(Transaction::getDate,
                        Collectors.groupingBy(Transaction::getDesc,
                                Collectors.summarizingDouble(Transaction::getWithdrawls))));

        // tut vse dannye zapisyvaiutsya v csv file

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("C:\\testovoezadanie\\TEST\\src\\main\\resources\\result.csv");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            CSVWriter csvWriter = new CSVWriter(outputStreamWriter);

            csvWriter.writeNext(new String[]{"Date", "Type", "Min", "Max","Average"});

            for(Map.Entry<String, Map<String, DoubleSummaryStatistics>> firstKey: summaryStatisticMap.entrySet()){
                for(Map.Entry<String, DoubleSummaryStatistics> secondKey : firstKey.getValue().entrySet()) {
                   csvWriter.writeNext(new String[]{String.valueOf(firstKey.getKey()),
                           "Description = " + secondKey.getKey(),
                           String.valueOf(String.format("%,.2f",  secondKey.getValue().getMin())),
                           String.valueOf(String.format("%,.2f",secondKey.getValue().getMax())),
                           String.valueOf(String.format("%,.2f",secondKey.getValue().getAverage()))});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //rasschet vremeni dlya ispolneniya koda
        System.out.println((double) (System.currentTimeMillis() - time)+ " milliseconds");

        //rasschet vydelennoq pamyati
        long usedMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Memory increased: " + (usedMemoryAfter-usedMemoryBefore) + " mb");
    }
}
