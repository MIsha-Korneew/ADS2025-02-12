package by.it.group410901.korneew.lesson06;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class C_LongNotUpSubSeq {

    public static void main(String[] args) throws FileNotFoundException {
        InputStream stream = C_LongNotUpSubSeq.class.getResourceAsStream("dataC.txt");
        C_LongNotUpSubSeq instance = new C_LongNotUpSubSeq();
        instance.findLongestNonIncreasingSubsequence(stream);
    }

    void findLongestNonIncreasingSubsequence(InputStream stream) throws FileNotFoundException {
        Scanner scanner = new Scanner(stream);
        int n = scanner.nextInt(); // Читаем длину последовательности
        int[] numbers = new int[n];

        // Читаем саму последовательность
        for (int i = 0; i < n; i++) {
            numbers[i] = scanner.nextInt();
        }

        int[] dp = new int[n]; // Массив для хранения длины подпоследовательности
        int[] previousIndex = new int[n]; // Массив для хранения предыдущих индексов
        int maxLength = 0; // Максимальная длина невозрастающей подпоследовательности
        int lastIndex = -1; // Индекс последнего элемента в последовательности

        // Инициализация массивов
        for (int i = 0; i < n; i++) {
            dp[i] = 1; // Каждое число само по себе является подпоследовательностью
            previousIndex[i] = -1; // Изначально нет предшественников
        }

        // Динамическое программирование для нахождения длины подпоследовательности
        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                if (numbers[i] <= numbers[j] && dp[i] < dp[j] + 1) {
                    dp[i] = dp[j] + 1; // Обновляем длину подпоследовательности
                    previousIndex[i] = j; // Запоминаем индекс предшествующего элемента
                }
            }
            // Обновляем максимальную длину и индекс последнего элемента
            if (dp[i] > maxLength) {
                maxLength = dp[i];
                lastIndex = i;
            }
        }

        // Восстанавливаем индексы наибольшей невозрастающей подпоследовательности
        List<Integer> indices = new ArrayList<>();
        while (lastIndex != -1) {
            indices.add(lastIndex + 1); // Индексы начинаются с 1
            lastIndex = previousIndex[lastIndex];
        }

        // Печатаем результат
        System.out.println(maxLength); // Длина подпоследовательности
        for (int i = indices.size() - 1; i >= 0; i--) {
            System.out.print(indices.get(i) + " "); // Индексы в порядке возрастания
        }
        System.out.println(); // Переход на новую строку
    }
}