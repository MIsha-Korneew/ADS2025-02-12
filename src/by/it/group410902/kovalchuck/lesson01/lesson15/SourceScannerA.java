package by.it.group410902.kovalchuck.lesson01.lesson15;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SourceScannerA {

    public static void main(String[] args) {
        //�������� ���� � �������� src �������� �������
        String src = System.getProperty("user.dir") + File.separator + "src" + File.separator;

        List<FileData> processedFiles = processJavaFiles(src);

        //������� ��������������� ����������
        printResults(processedFiles);
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
                            String content = readFileContent(path);
                            //���������, �� �������� �� ���� ������
                            if (!isTestFile(content)) {

                                String processedContent = processContent(content);
                                //�������� ������������� ����
                                String relativePath = srcPath.relativize(path).toString();
                                //��������� ������ ������������� �����������
                                int size = processedContent.getBytes(StandardCharsets.UTF_8).length;
                                //��������� ������ ����� � ���������
                                result.add(new FileData(relativePath, size));
                            }
                        } catch (IOException e) {
                            //���������� ������ ������ ������
                        }
                    });
        } catch (IOException e) {
            //���������� ������ ������ ����������
        }

        return result;
    }

    private static String readFileContent(Path path) throws IOException {
        //���������� UTF-8 � ���������� ������ ��� ��������� MalformedInputException
        Charset charset = StandardCharsets.UTF_8;
        try {
            //�������� ��������� ���� ��� �����
            return Files.readString(path, charset);
        } catch (IOException e) {
            //���� �������� ������, ������� ��������� ��� ������������������ ������ � �������������
            byte[] bytes = Files.readAllBytes(path);
            return new String(bytes, charset);
        }
    }

    private static boolean isTestFile(String content) {
        //���������, �������� �� ���� ��������� ������������
        return content.contains("@Test") || content.contains("org.junit.Test");
    }

    private static String processContent(String content) {
        //������� package � �������
        StringBuilder result = new StringBuilder();
        boolean inMultiLineComment = false; //���������� � ������������� �����������
        boolean inString = false; // ���������� ������ ������
        char prevChar = '\0'; //������ ��� ��������� �������������

        for (int i = 0; i < content.length(); i++) {
            char currentChar = content.charAt(i);

            //��������� ������ �������������� �����������
            if (!inString && !inMultiLineComment && currentChar == '/' && i + 1 < content.length() && content.charAt(i + 1) == '*') {
                inMultiLineComment = true;
                i++; //���������� ��������� ������
                continue;
            }

            //��������� ����� �������������� �����������
            if (inMultiLineComment && currentChar == '*' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                inMultiLineComment = false;
                i++; //���������� ��������� ������
                continue;
            }

            //���� ��������� � ������������� �����������, ���������� �������
            if (inMultiLineComment) {
                prevChar = currentChar;
                continue;
            }

            //��������� �����
            if (!inMultiLineComment && currentChar == '"' && prevChar != '\\') {
                inString = !inString;
            }

            //��������� ������������ ������������
            if (!inString && !inMultiLineComment && currentChar == '/' && i + 1 < content.length() && content.charAt(i + 1) == '/') {
                //���������� ��� ������� �� ����� ������
                while (i < content.length() && content.charAt(i) != '\n') {
                    i++;
                }
                //��������� ������ ����� ������ ��� ���������� ���������
                if (i < content.length()) {
                    result.append('\n');
                }
                continue;
            }

            //��������� ������, ���� �� ��������� � �����������
            if (!inMultiLineComment) {
                result.append(currentChar);
            }

            prevChar = currentChar;
        }

        String withoutComments = result.toString();

        //������ ������� package � import ������
        String[] lines = withoutComments.split("\\r?\\n");
        StringBuilder finalResult = new StringBuilder();

        for (String line : lines) {
            String trimmedLine = line.trim();
            //��������� ������ ������, �� ������������ � package ��� import
            if (!trimmedLine.startsWith("package") && !trimmedLine.startsWith("import")) {
                finalResult.append(line).append("\n");
            }
        }

        //������� ������� � ����� <33 � ������ � �����
        String processed = finalResult.toString();
        return removeLeadingTrailingControlChars(processed);
    }

    private static String removeLeadingTrailingControlChars(String text) {
        if (text.isEmpty()) return text;

        //������� ����������� ������� � ������ ������
        int start = 0;
        while (start < text.length() && text.charAt(start) < 33) {
            start++;
        }

        //���� ���� ����� ������� �� ����������� ��������
        if (start >= text.length()) return "";

        //������� ����������� ������� � ����� ������
        int end = text.length();
        while (end > start && text.charAt(end - 1) < 33) {
            end--;
        }

        return text.substring(start, end);
    }

    private static void printResults(List<FileData> files) {
        //��������� ����� �� ������� (�� �����������), � ��� ������ �������� - �� ����
        files.stream()
                .sorted(Comparator.comparingInt(FileData::getSize)
                        .thenComparing(FileData::getPath))
                .forEach(file -> System.out.println(file.getSize() + " " + file.getPath()));
    }

    //��������������� ����� ��� �������� ������ � �����
    private static class FileData {
        private final String path; //������������� ���� �����
        private final int size; //������ ������������� ����������� � ������

        public FileData(String path, int size) {
            this.path = path;
            this.size = size;
        }

        public String getPath() {
            return path;
        }

        public int getSize() {
            return size;
        }
    }
}