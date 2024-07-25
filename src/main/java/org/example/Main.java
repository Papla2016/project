package main.java.org.example;


import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    private static List<String> inputFiles = new ArrayList<>();
    private static String outputPath = "";
    private static String prefix = "";
    private static boolean appendMode = false;
    private static boolean shortStats = false;
    private static boolean fullStats = false;

    private static List<Integer> integers = new ArrayList<>();
    private static List<Double> floats = new ArrayList<>();
    private static List<String> strings = new ArrayList<>();

    public static void main(String[] args) {
        try {
            parseArguments(args);
            processFiles();
            writeOutputFiles();
            printStatistics();
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static void parseArguments(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-o":
                    outputPath = args[++i];
                    break;
                case "-p":
                    prefix = args[++i];
                    break;
                case "-a":
                    appendMode = true;
                    break;
                case "-s":
                    shortStats = true;
                    break;
                case "-f":
                    fullStats = true;
                    break;
                default:
                    inputFiles.add(args[i]);
            }
        }
        if (outputPath.isEmpty()) {
            outputPath = ".";
        }
    }

    private static void processFiles() throws IOException {
        for (String fileName : inputFiles) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    categorizeData(line);
                }
            } catch (IOException e) {
                System.err.println("Ошибка при чтении файла " + fileName + ": " + e.getMessage());
            }
        }
    }

    private static void categorizeData(String data) {
        if (data.matches("-?\\d+")) {
            integers.add(Integer.parseInt(data));
        } else if (data.matches("-?\\d*\\.\\d+([eE]-?\\d+)?")) {
            floats.add(Double.parseDouble(data));
        } else {
            strings.add(data);
        }
    }

    private static void writeOutputFiles() throws IOException {
        if (!integers.isEmpty()) {
            writeToFile("integers.txt", integers.stream().map(Object::toString).collect(Collectors.toList()));
        }
        if (!floats.isEmpty()) {
            writeToFile("floats.txt", floats.stream().map(Object::toString).collect(Collectors.toList()));
        }
        if (!strings.isEmpty()) {
            writeToFile("strings.txt", strings);
        }
    }

    private static void writeToFile(String fileName, List<String> data) throws IOException {
        Path filePath = Paths.get(outputPath, prefix + fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, appendMode ? StandardOpenOption.APPEND : StandardOpenOption.CREATE)) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    private static void printStatistics() {
        if (shortStats || fullStats) {
            System.out.println("Статистика:");
            if (!integers.isEmpty()) {
                printIntegerStatistics();
            }
            if (!floats.isEmpty()) {
                printFloatStatistics();
            }
            if (!strings.isEmpty()) {
                printStringStatistics();
            }
        }
    }

    private static void printIntegerStatistics() {
        System.out.println("Целые числа:");
        System.out.println("Количество: " + integers.size());
        if (fullStats) {
            System.out.println("Минимум: " + Collections.min(integers));
            System.out.println("Максимум: " + Collections.max(integers));
            System.out.println("Сумма: " + integers.stream().mapToInt(Integer::intValue).sum());
            System.out.println("Среднее: " + integers.stream().mapToInt(Integer::intValue).average().orElse(0));
        }
    }

    private static void printFloatStatistics() {
        System.out.println("Вещественные числа:");
        System.out.println("Количество: " + floats.size());
        if (fullStats) {
            System.out.println("Минимум: " + Collections.min(floats));
            System.out.println("Максимум: " + Collections.max(floats));
            System.out.println("Сумма: " + floats.stream().mapToDouble(Double::doubleValue).sum());
            System.out.println("Среднее: " + floats.stream().mapToDouble(Double::doubleValue).average().orElse(0));
        }
    }

    private static void printStringStatistics() {
        System.out.println("Строки:");
        System.out.println("Количество: " + strings.size());
        if (fullStats) {
            String min = strings.stream().min(Comparator.comparingInt(String::length)).orElse("");
            String max = strings.stream().max(Comparator.comparingInt(String::length)).orElse("");
            System.out.println("Самая короткая строка: " + min.length());
            System.out.println("Самая длинная строка: " + max.length());
        }
    }
}
