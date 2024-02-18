package ru.naumkin.console.file.manager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Scanner;

public class UserHandler {

    File currentDirectory;

    public UserHandler() {
        this.currentDirectory = new File("C:\\");
        listenUserMessages();
    }

    private void listenUserMessages() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\n" + currentDirectory + ">");
            String userText = scanner.nextLine();
            if (userText.startsWith("ls")) {
                if (userText.startsWith("ls i")) {
                    printDetailedInformationAboutDirectoryFiles(currentDirectory);
                    continue;
                }
                printFilesFromDirectory(currentDirectory);
            }
            else if (userText.equals("cd..")) {
                goToParent();
            }
            else if (userText.startsWith("cd ")) {
                goToDirectory(userText);
            }
            else if (userText.startsWith("mkdir ")) {
                createDirectory(userText, currentDirectory);
            }
            else if (userText.startsWith("rm ")) {
                deleteFile(userText, currentDirectory.getAbsolutePath());
            }
            else if (userText.startsWith("mv ")) { // mv C:\1\2\2.1\2.2.txt C:\1\2.2.txt  , // mv C:\1\2\2.1 C:\1\2.1
                moveFile(userText);
            }
            else if (userText.startsWith("cp ")) { // cp C:\1\2\2.1\2.2.txt C:\1\2.2.txt
                copyFile(userText);
            }
            else if (userText.startsWith("finfo ")) {
                printFileInformation(userText, currentDirectory);
            }
            else if (userText.startsWith("help")) {
                printCommands();
            }
            else if (userText.startsWith("exit")) {
                break;
            } else {
                System.out.println("Введите \"help\" для вывода списка команд");
            }
        }
    }

    private void goToParent() {
        if (currentDirectory.getAbsolutePath().equals("C:\\")) {
            return;
        }
        currentDirectory = currentDirectory.getParentFile();
    }


    private void goToDirectory(String userText) {
        String[] userTextElements = userText.split(" ", 2);
        File[] fileList = Objects.requireNonNull(currentDirectory.listFiles());
        for (File f : fileList) {
            if (f.getName().equals(userTextElements[1])) {
                if (f.isFile()) {
                    System.out.println("укажите директорию, а не файл");
                    return;
                }
                String newPath = currentDirectory.getPath() + "\\" + f.getName();
                currentDirectory = new File(newPath);
                return;
            }
        }
    }

    private static void printCommands() {
        System.out.println("ls – распечатать список файлов текущего каталога\n" +
                "ls i – подробная информация о файлах\n" +
                "cd [path] – переход в указанную поддиректорию. cd .. – переход в родительский каталог\n" +
                "mkdir [name] – создание новой директории с указанным именем\n" +
                "mv [source] [destination] – переименовать/перенести файл или директорию\n" +
                "cp [source] [destination] – скопировать файл \n" +
                "finfo [filename] – получить подробную информацию о файле \n" +
                "help – вывод в консоль всех поддерживаемых команд\n" +
                "exit – завершить работу");
    }

    private void printFileInformation(String userText, File currentDirectory) {
        String[] textElements = userText.split(" ", 2);
        String fileName = textElements[1];
        String path = currentDirectory + "\\" + fileName;
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("файла с таким именем не существует в этой директории");
            return;
        }
        System.out.println("Name:" + file.getName() + " ,Size:" +
                file.length() + " ,Last Modified:" + convertDate(file));
    }


    private static void createDirectory(String userText, File currentDirectory) {
        String[] elements = userText.split(" ", 2);
        String nameOfTheFutureDirectory = elements[1];
        String path = currentDirectory + "\\" + nameOfTheFutureDirectory;
        File file = new File(path);
        if (file.mkdir()) {
            System.out.println(">>>вы создали директорию " + file.getPath());
            return;
        }
        System.out.println(">>>Не возможно создать директорию " + nameOfTheFutureDirectory);//создать причину почему нельзя
    }


    private void deleteFile(String userText, String pathOfCurrentDirectory) {
        String[] messageElements = userText.split(" ", 2);
        String fileName = messageElements[1];
        File fileToBeDeleted;

        if (fileName.startsWith("C:\\")) {
            fileToBeDeleted = new File(fileName);
        } else {
            String path = pathOfCurrentDirectory + "\\" + fileName;
            fileToBeDeleted = new File(path);
        }

        if (fileToBeDeleted.delete()) {
            System.out.printf("файл " + "%s" + " удалён", fileName);
            if (fileToBeDeleted.getAbsolutePath().equals(currentDirectory.getAbsolutePath())) {
                currentDirectory = new File(currentDirectory.getParent());
            }
        } else {
            System.out.printf("файл " + "%s" + " не получилось удалить", fileName);
        }
    }

    private void moveFile(String userText) {
        String[] textElements = userText.split(" ", 3);
        String source = textElements[1];
        String destination = textElements[2];
        File fileToMove = new File(source);
        if (fileToMove.renameTo(new File(destination))) {
            System.out.println("файл был перемещён");
        } else {
            System.out.println("не удалось переместить файл");
        }
    }

    private void copyFile(String userText) {
        String[] messageElements = userText.split(" ", 3);
        String source = messageElements[1];
        String destination = messageElements[2];
        Path soursePath = Paths.get(source).toAbsolutePath();
        Path destPath = Paths.get(destination).toAbsolutePath();
        try {
            Files.copy(soursePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            if (destPath.toFile().exists()) {
                System.out.println("файл скопирован");
            } else {
                System.out.println("не удалось скопировать файл");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printFilesFromDirectory(File directory) {
        if (directory.isFile()) {
            System.out.println("Не удалось распечатать список файлов, необходимо указать путь к папке , а не к файлу.");
            return;
        }
        File[] fileList = directory.listFiles();
        if (fileList == null) {
            throw new NullPointerException();
        }
        if (fileList.length == 0) {
            System.out.println("В папке нету элементов");
        }
        System.out.println("\n>>>Список файлов директории: ");
        for (File file : fileList) {
            if (file.isHidden()) {
                continue;
            }
            System.out.println(file.getName());
        }
    }

    public void printDetailedInformationAboutDirectoryFiles(File directory) {
        File[] fileList = directory.listFiles();
        if (fileList == null || fileList.length == 0) {
            System.out.println("В указанной директории нету файлов ");
            return;
        }
        System.out.println("\n>>>Список файлов директории: ");
        for (File file : fileList) {
            if (file.isHidden()) {
                continue;
            }
            System.out.printf("Name:%s, Size:%s  bytes, Last Modified: %s\n", file.getName(), file.length(), convertDate(file));
        }
    }

    public String convertDate(File file) {
        long fileLastModifiedDate = file.lastModified();
        Date date = new Date(fileLastModifiedDate);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        return simpleDateFormat.format(date);
    }
}
