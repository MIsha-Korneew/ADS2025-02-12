package by.it.group410901.korneew.lesson02;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class C_GreedyKnapsack {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.currentTimeMillis();
        InputStream inputStream = C_GreedyKnapsack.class.getResourceAsStream("greedyKnapsack.txt");
        double costFinal = new C_GreedyKnapsack().calc(inputStream);
        long finishTime = System.currentTimeMillis();
        System.out.printf("Общая стоимость %f (время %d)", costFinal, finishTime - startTime);
    }

    double calc(InputStream inputStream) throws FileNotFoundException {
        Scanner input = new Scanner(inputStream);
        int n = input.nextInt();
        int W = input.nextInt();
        Item[] items = new Item[n];
        for (int i = 0; i < n; i++) {
            items[i] = new Item(input.nextInt(), input.nextInt());
        }

        for (Item item : items) {
            System.out.println(item);
        }
        System.out.printf("Всего предметов: %d. Рюкзак вмещает %d кг.\n", n, W);

        Arrays.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                double ratio1 = (double) item1.cost / item1.weight;
                double ratio2 = (double) item2.cost / item2.weight;
                return Double.compare(ratio2, ratio1); // Сортировка по убыванию
            }
        });

        double result = 0;
        for (Item item : items) {
            if (W <= 0) {
                break;
            }
            if (item.weight <= W) {
                result += item.cost;
                W -= item.weight;
            } else {
                result += item.cost * ((double) W / item.weight);
                W = 0; // Рюкзак полон
            }
        }

        System.out.printf("Удалось собрать рюкзак на сумму %f\n", result);
        return result;
    }

    private static class Item {
        int cost;
        int weight;

        Item(int cost, int weight) {
            this.cost = cost;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "cost=" + cost +
                    ", weight=" + weight +
                    '}';
        }
    }
}