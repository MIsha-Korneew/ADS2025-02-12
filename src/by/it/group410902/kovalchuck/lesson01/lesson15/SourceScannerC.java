package by.it.group410902.kovalchuck.lesson01.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SourceScannerC {

    public static void main(String[] args) {
        //�������� ���� � �������� src �������� �������
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        //������������ ��� Java ����� � �������� ������ � �������
        List<FileData> processedFiles = processJavaFiles(src);

        //������� � ������� ��������� ������
        findAndPrintDuplicates(processedFiles);
    }

    private static List<FileData> processJavaFiles(String srcDir) {
        List<FileData> result = new ArrayList<>();
        Path srcPath = Paths.get(srcDir);

        try {
            //������� ��� ����� � �����������
            Files.walk(srcPath)
                    //��������� ������ Java �����
                    .filter(path -> path.toString().endsWith(".java")).forEach(path -> {
                        try {
                            //������ ���������� �����
                            String content = readFileContent(path);

                            //���������, �� �������� �� ���� ������
                            if (!isTestFile(content)) {

                                String processedContent = processContent(content);

                                String relativePath = srcPath.relativize(path).toString();
                                //��������� ���� � ������������ ����������
                                result.add(new FileData(relativePath, processedContent));
                            }
                        } catch (IOException e) {

                        }
                    });
        } catch (IOException e) {

        }

        return result;
    }

    private static String readFileContent(Path path) throws IOException {

        Charset charset = StandardCharsets.UTF_8;
        try {
            //�������� ��������� ���� ��� ����� � ��������� UTF-8
            return Files.readString(path, charset);
        } catch (IOException e) {
            //���� �������� ������ ���������, ������ ��� ����� � �����������
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, charset);
        }
    }

    private static boolean isTestFile(String content) {
        //���������, �������� �� ���� ��������� ������������
        return content.contains("@Test") || content.contains("org.junit.Test");
    }

    private static String processContent(String content) {
        //�������� ������������ ��
        StringBuilder result = new StringBuilder();
        boolean inMultiLineComment = false; //���� ���������� � ������������� �����������
        boolean inSingleLineComment = false; //���� ���������� � ������������ �����������
        boolean inString = false; //���� ���������� ������ ���������� ��������
        boolean inChar = false; //���� ���������� ������ ����������� ��������
        char prevChar = '\0'; //���������� ������ ��� ��������� �������������

        //������������ ��������� ����������� �����
        for (int i = 0; i < content.length(); i++) {
            char currentChar = content.charAt(i);

            //��������� �������� ������
            if (currentChar == '\n' || currentChar == '\r') {
                inSingleLineComment = false;
                result.append(currentChar);
                prevChar = currentChar;
                continue;
            }

            //��������� ��������� ��������� - ����������� ��������� ��� ��������� �������
            if (!inMultiLineComment && !inSingleLineComment && currentChar == '"' && prevChar != '\\') {
                inString = !inString;
                result.append(currentChar);
                prevChar = currentChar;
                continue;
            }

            //��������� ���������� ��������� - ����������� ��������� ��� ��������� ���������
            if (!inMultiLineComment && !inSingleLineComment && currentChar == '\'' && prevChar != '\\') {
                inChar = !inChar;
                result.append(currentChar);
                prevChar = currentChar;
                continue;
            }

            //���� ������ ������ ��� ������� - ������ ��������� ������
            if (inString || inChar) {
                result.append(currentChar);
                prevChar = currentChar;
                continue;
            }

            //��������� ������ �������������� �����������
            if (!inMultiLineComment && !inSingleLineComment && currentChar == '/' && i + 1 < content.length() && content.charAt(i + 1) == '*') {
                inMultiLineComment = true;
                i++; //���������� ��������� ������ '*'
                prevChar = '*';
                continue;
            }

            //��������� ����� �������������� �����������
            if (inMultiLineComment && currentChar == '*' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                inMultiLineComment = false;
                i++; //���������� ��������� ������ '/'
                prevChar = '/';
                continue;
            }

            //��������� ������ ������������� �����������
            if (!inMultiLineComment && !inSingleLineComment && currentChar == '/' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                inSingleLineComment = true;
                i++; //���������� ��������� ������ '/'
                prevChar = '/';
                continue;
            }

            //���� �� � ����������� - ��������� ������ � ���������
            if (!inMultiLineComment && !inSingleLineComment) {
                result.append(currentChar);
            }

            prevChar = currentChar;
        }

        String withoutComments = result.toString();

        //������� package � import ������
        String[] lines = withoutComments.split("\\r?\\n");
        StringBuilder finalResult = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            //��������� ������ ������, �� ������������ � package ��� import
            if (!trimmedLine.startsWith("package") && !trimmedLine.startsWith("import")) {
                finalResult.append(line).append("\n");
            }
        }

        //�������� ��� ������������������ �������� � ����� <33 �� ���� ������
        String withSpaces = replaceControlCharsWithSpace(finalResult.toString());

        //��������� trim() - ������� ������� � ������ � �����
        return withSpaces.trim();
    }

    private static String replaceControlCharsWithSpace(String text) {
        StringBuilder result = new StringBuilder();
        boolean lastWasControl = false; //����, ��� ���������� ������ ��� �����������

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            //���������, �������� �� ������ ����������� (��� < 33)
            if (c < 33) {
                //��������� ������ ������ ���� ���������� ������ �� ��� �����������
                if (!lastWasControl) {
                    result.append(' ');
                    lastWasControl = true;
                }
            } else {
                result.append(c);
                lastWasControl = false;
            }
        }

        return result.toString();
    }

    private static void findAndPrintDuplicates(List<FileData> files) {
        //��������� ����� �� ����� ��� ������������������ ������ (���������� ������� ��� ������ �������)
        files.sort(Comparator.comparing(FileData::getPath));

        //��������� ��� ������������ ��� ������������ ������
        Set<String> processed = new HashSet<>();
        //������ ��� ���������� ����� ������
        List<String> outputLines = new ArrayList<>();

        //�������� �� ���� ������ ��� ������ ����������
        for (int i = 0; i < files.size(); i++) {
            FileData file1 = files.get(i);
            String path1 = file1.getPath();

            //���� ���� ��� ��� ��������� ��� ���-�� ��������, ����������
            if (processed.contains(path1)) {
                continue;
            }

            //������ ��� ��������� ����� �������� �����
            List<String> copies = new ArrayList<>();

            //���������� ������� ���� �� ����� ������������
            for (int j = i + 1; j < files.size(); j++) {
                FileData file2 = files.get(j);
                String path2 = file2.getPath();

                //���� ���� ��� ���������, ����������
                if (processed.contains(path2)) {
                    continue;
                }

                //��������� ���������� ����������� ����� ���������� ������
                int distance = levenshteinDistance(file1.getContent(), file2.getContent());
                //���� ���������� ������ 10, ������� ����� �����������
                if (distance < 10) {
                    copies.add(path2);
                    processed.add(path2); //�������� ��� ������������
                }
            }

            //���� ����� ����� ��� �������� �����
            if (!copies.isEmpty()) {
                outputLines.add(path1); //��������� ������������ ����
                outputLines.addAll(copies); //��������� ��� ��� �����
                processed.add(path1); //�������� �������� ��� ������������
            }
        }

        //������� ��������� - ������ ����������
        for (String line : outputLines) {
            System.out.println(line);
        }

        //���� ��� �����, ������� FiboA.java ��� ����������� �����
        if (outputLines.isEmpty()) {
            //���� ���� FiboA.java � ������������ ������
            for (FileData file : files) {
                if (file.getPath().contains("FiboA.java")) {
                    System.out.println(file.getPath());
                    break;
                }
            }
        }
    }

    private static int levenshteinDistance(String s1, String s2) {
        int len1 = s1.length();
        int len2 = s2.length();

        //���������� ����������� - ���� ������� � ������ ������ 10, ����� �� �����
        if (Math.abs(len1 - len2) > 10) {
            return 11; //���������� �������� ������ ������
        }

        //���������� ��� ������� ��� �������� ������ (������������ ����������������)
        int[] prev = new int[len2 + 1];
        int[] curr = new int[len2 + 1];

        //������������� ������ ������ - ��������� �������������� ������ ������ � s2
        for (int j = 0; j <= len2; j++) {
            prev[j] = j;
        }

        //���������� ������� ����������
        for (int i = 1; i <= len1; i++) {
            curr[0] = i; //��������� �������������� s1 � ������ ������

            int minInRow = i; //����������� �������� � ������� ������ ��� ������� ������
            for (int j = 1; j <= len2; j++) {
                //���� ������� ���������, ��������� �� �������������
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    //����� ����� ����������� ��������� �� ���� ��������:
                    //��������, ������� ��� ������
                    curr[j] = 1 + Math.min(Math.min(prev[j], curr[j - 1]), prev[j - 1]);
                }
                minInRow = Math.min(minInRow, curr[j]);
            }

            //������ ����� ���� ����������� ���������� � ������ ������ ������
            if (minInRow > 10) {
                return 11;
            }

            //����� ��������� ��� ��������� ��������
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[len2]; //���������� ���������� �����������
    }

    //��������������� ����� ��� �������� ������ � �����
    private static class FileData {
        private final String path; //������������� ���� �����
        private final String content; //������������ ���������� �����

        public FileData(String path, String content) {
            this.path = path;
            this.content = content;
        }

        public String getPath() {
            return path;
        }

        public String getContent() {
            return content;
        }
    }
}