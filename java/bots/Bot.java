package bots;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import pets.classes.Cat;
import pets.classes.Dog;
import pets.enums.Breed;
import pets.enums.PetEvent;
import pets.enums.PetSize;
import pets.enums.Sex;
import pets.interfaces.Pet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {


    private String chatId;
    private boolean checkWalk;
    private boolean checkFeed;
    private boolean menuTimeSettings;
    private boolean menuLocation;
    private boolean menuPhoto;
    private String[] petData;
    private String time = "00:00";
    private String petKind;
    private String dataBaseUrl = "jdbc:mysql://localhost:3306/TelegramPetBot?useUnicode=true&characterEncoding=utf8&verifyServerCertificate=false&useSSL=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private String userName = "root";
    private String password = "123";

    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private class InnerClass implements Runnable {

        @Override
        public void run() {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            String currentTime;
            Date date;

            while (true) {
                date = new Date();
                currentTime = date.getHours() + ":" + date.getMinutes();
                if (currentTime.equals(time)) {
                    checkFeed = false;
                    checkWalk = false;
                    while (!checkWalk) {
                        sendNewMessage("Pls, walk with your dog!");
                        try {
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (!checkFeed) {
                        sendNewMessage("Pls, feed your dog!");
                        try {
                            TimeUnit.MINUTES.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();
        chatId = message.getChatId().toString();

        InnerClass innerClass = new InnerClass();
        Thread thread = new Thread(innerClass);
        thread.start();

        if (message.hasText()) {
            String textMessage = message.getText();
            if (textMessage.equals("/start")) {
                sendNewMessage("Hello, " + message.getChat().getFirstName() + ", use /help");
            } else if (textMessage.equals("Walked")) {
                checkWalk = true;
                sendNewMessage("Ok, really thank you, " + message.getChat().getFirstName());
            } else if (textMessage.equals("Nourished")) {
                checkFeed = true;
                sendNewMessage("Ok, really thank you, " + message.getChat().getFirstName());
            } else if (textMessage.startsWith("/lost dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("/lost dog ", "");
                textMessage = textMessage + "; " + PetEvent.LOST.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/help lost dog")) {
                sendNewMessage("LOST FORMAT: /lost (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("/found dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("/found dog ", "");
                textMessage = textMessage + "; " + PetEvent.FOUND.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/help found dog")) {
                sendNewMessage("FOUND FORMAT: /found (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("/care dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("/care dog ", "");
                textMessage = textMessage + "; " + PetEvent.CARE.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/help care dog")) {
                sendNewMessage("CARE FORMAT: /care (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("/dead dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("/dead dog ", "");
                textMessage = textMessage + "; " + PetEvent.DEAD.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.startsWith("/lost cat ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("/lost cat ", "");
                textMessage = textMessage + "; " + PetEvent.LOST.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/help lost cat")) {
                sendNewMessage("LOST FORMAT: /lost (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("/found cat ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("/found cat ", "");
                textMessage = textMessage + "; " + PetEvent.FOUND.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/help found")) {
                sendNewMessage("FOUND FORMAT: /found (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("/care ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("/care cat ", "");
                textMessage = textMessage + "; " + PetEvent.CARE.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/help care")) {
                sendNewMessage("CARE FORMAT: /care (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            }  else if (textMessage.startsWith("/dead cat ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("/dead cat ", "");
                textMessage = textMessage + "; " + PetEvent.DEAD.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                for (int i = 0; i < localPetData.length; i++) {
                    tempPetData[i] = localPetData[i];
                }
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
            } else if (textMessage.equals("/show")) {

                List<String> names = new ArrayList<>();
                List<String> breeds = new ArrayList<>();
                List<String> pets = new ArrayList<>();
                List<String> sexes = new ArrayList<>();
                List<String> sizes = new ArrayList<>();
                List<String> colors = new ArrayList<>();
                List<String> addresses = new ArrayList<>();
                List<String> events = new ArrayList<>();
                List<String> dates = new ArrayList<>();

                Connection connection = null;
                try {
                    connection = DriverManager.getConnection(dataBaseUrl, userName, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String query = "SELECT Name, Pet, Breed, Sex, Size, Color, Address, Event, Date, COUNT(*) FROM Events GROUP BY UID";

                Statement stmt = null;

                try {
                    stmt = connection.createStatement();
                    ResultSet rs4 = stmt.executeQuery(query);

                    while (rs4.next()) {
                        names.add(rs4.getString(1));
                        pets.add(rs4.getString(2));
                        breeds.add(rs4.getString(3));
                        sexes.add(rs4.getString(4));
                        sizes.add(rs4.getString(5));
                        colors.add(rs4.getString(6));
                        addresses.add(rs4.getString(7));
                        events.add(rs4.getString(8));
                        dates.add(rs4.getString(9));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                for (int i = 0; i < names.size(); i++) {
                    sendNewMessage(names.get(i) + ", " + pets.get(i) + ", " + breeds.get(i) + ", " + sexes.get(i) + ", " + sizes.get(i) + ", " + colors.get(i) + ", " + addresses.get(i) + ", " + events.get(i) + ", " + dates.get(i));
                }
            } else if (textMessage.equals("/show my")){
                List<String> names = new ArrayList<>();
                List<String> breeds = new ArrayList<>();
                List<String> pets = new ArrayList<>();
                List<String> sexes = new ArrayList<>();
                List<String> sizes = new ArrayList<>();
                List<String> colors = new ArrayList<>();
                List<String> addresses = new ArrayList<>();
                List<String> events = new ArrayList<>();
                List<String> dates = new ArrayList<>();


                Connection connection = null;
                try {
                    connection = DriverManager.getConnection(dataBaseUrl, userName, password);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String query = "SELECT Name, Pet, Breed, Sex, Size, Color, Address, Event, Date, COUNT(*) FROM Events WHERE User=" + chatId + " GROUP BY UID";

                Statement stmt = null;
                try {
                    stmt = connection.createStatement();
                    ResultSet rs4 = stmt.executeQuery(query);

                    while (rs4.next()) {
                        names.add(rs4.getString(1));
                        pets.add(rs4.getString(2));
                        breeds.add(rs4.getString(3));
                        sexes.add(rs4.getString(4));
                        sizes.add(rs4.getString(5));
                        colors.add(rs4.getString(6));
                        addresses.add(rs4.getString(7));
                        events.add(rs4.getString(8));
                        dates.add(rs4.getString(9));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        stmt.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                for (int i = 0; i < names.size(); i++) {
                    sendNewMessage(names.get(i) + ", " + pets.get(i) + ", " + breeds.get(i) + ", " + sexes.get(i) + ", " + sizes.get(i) + ", " + colors.get(i) + ", " + addresses.get(i) + ", " + events.get(i) + ", " + dates.get(i));
                }

            } else if (textMessage.equals("/settings")) {
                menuTimeSettings = true;
                sendNewMessage("Pls, enter time....");
            } else if (menuTimeSettings) {
                time = textMessage;
            } else if (textMessage.equals("/help settings")) {
                sendNewMessage("FORMAT TIME: HH:MM");
            } else if (textMessage.equals("/help")){
                sendNewMessage("USE /help (lost/found/care/dead) (dog/cat) for details + \nUse /settings for setup time for watch for your dog or cat");
            } else if (message.getText().equals("No location")) {
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have photo?");
            } else if (message.getText().equals("No photo")) {
                menuPhoto = false;
                saveData();
            } else {
                sendNewMessage("Sry, i cant understand you....");
            }
        } else if (menuPhoto && message.hasPhoto()) {

            PhotoSize photoSize = getPhoto(message);
            String fp = getFilePath(photoSize);
            File file = downloadPhotoByFilePath(fp);
            BufferedImage image;
            try {

                image = ImageIO.read(file);

                File file1 = new File(String.valueOf(System.currentTimeMillis()));
                ImageIO.write(image, "jpg", file1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            petData[petData.length - 1] = "/home/pikachu/IdeaProjects/TestTelegramBot/" + file.getName();
            saveData();

        } else if (menuLocation && message.hasLocation()) {
            if (petData.length == 8)
                petData[5] = message.getLocation().getLatitude() + ", " + message.getLocation().getLongitude();
            else petData[4] = message.getLocation().getLatitude() + ", " + message.getLocation().getLongitude();

            sendNewMessage("Do you have photo?");
            menuPhoto = true;
            menuLocation = false;
        }

    }

    private PhotoSize getPhoto(Message message) {
        List<PhotoSize> photos = message.getPhoto();

        return photos.stream()
                .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                .findFirst()
                .orElse(null);
    }

    private String getFilePath(PhotoSize photo) {
        Objects.requireNonNull(photo);

        if (photo.hasFilePath()) {
            return photo.getFilePath();
        } else {
            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(photo.getFileId());
            try {
                org.telegram.telegrambots.api.objects.File file = getFile(getFileMethod);
                return file.getFilePath();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private java.io.File downloadPhotoByFilePath(String filePath) {
        try {
            return downloadFile(filePath);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveData() {

        Pet pet = null;


        if (petKind.equals("Собака")) {
            if (petData.length == 8) {
                pet = Dog.petFactoryWithName.createPetWithNameEvent(petData[0], Breed.valueOf(petData[1].toUpperCase()), Sex.valueOf(petData[2].toUpperCase()), petData[3], PetSize.valueOf(petData[4].toUpperCase()), petData[5], PetEvent.valueOf(petData[6]));
            } else if (petData.length == 7)
                pet = Dog.anonymousPetFactory.createAnonymousPet(Breed.valueOf(petData[0].toUpperCase()), Sex.valueOf(petData[1].toUpperCase()), petData[2], PetSize.valueOf(petData[3].toUpperCase()), petData[4], PetEvent.valueOf(petData[5]));
        } else if (petKind.equals("Кошка")) {
            if (petData.length == 8) {

                pet = Cat.petFactoryWithName.createPetWithNameEvent(petData[0], Breed.valueOf(petData[1].toUpperCase()), Sex.valueOf(petData[2].toUpperCase()), petData[3], PetSize.valueOf(petData[4].toUpperCase()), petData[5], PetEvent.valueOf(petData[6]));
            } else if (petData.length == 7)
                pet = Cat.anonymousPetFactory.createAnonymousPet(Breed.valueOf(petData[0].toUpperCase()), Sex.valueOf(petData[1].toUpperCase()), petData[2], PetSize.valueOf(petData[3].toUpperCase()), petData[4], PetEvent.valueOf(petData[5]));
        }

        if (petData[petData.length - 1] != null) {
            SendPhoto sendPhoto = new SendPhoto();
            petData[petData.length - 1] = petData[petData.length - 1].replaceAll(".tmp", ".jpeg");
            sendPhoto.setPhoto(petData[petData.length - 1]);
            sendPhoto.setChatId(chatId);
            pet.setPath(petData[petData.length - 1]);
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Properties connInfo = new Properties();
        connInfo.put("user", "root");
        connInfo.put("password", "123");
        connInfo.put("useUnicode", "true"); // (1)
        connInfo.put("charSet", "UTF8");
        try (Connection connection = DriverManager.getConnection(dataBaseUrl, connInfo)) {
            Statement statement = connection.createStatement();
            statement.execute("SET NAMES utf8");
            statement.execute("SET collation_connection='utf8_general_ci'");
            statement.execute("INSERT INTO Events (UID, User, Name, Pet, Breed, Sex, Size, Color, Address, Event, Date, Path) VALUES (UUID(), '" + chatId + "', '" + petData[0] + "', '" + petKind + "', '" + Breed.valueOf(petData[1].toUpperCase()) + "', '" + Sex.valueOf(petData[2].toUpperCase()) + "', '" + petData[3] + "', '" + PetSize.valueOf(petData[4].toUpperCase()) + "', '" + petData[5] + "', '" + PetEvent.valueOf(petData[6]) + "', '" + new Date() + "', '" + petData[7] + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sendNewMessage(pet.toString());

    }

    private void sendNewMessage(String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public String getBotToken() {
        return "386471901:AAFfpReaAU1AISROfCTxrCe_HZR9Wf4MWJs";
    }

    @Override
    public void onClosing() {

    }
}