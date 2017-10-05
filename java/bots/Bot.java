package bots;

import dao.JDBCPetDataAccessObject;
import org.apache.log4j.Logger;
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
    private Logger log = Logger.getLogger(Bot.class);

    private JDBCPetDataAccessObject JDBCPetDataAccessObject = new JDBCPetDataAccessObject();

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
        chatId = message.getFrom().getId().toString();

        InnerClass innerClass = new InnerClass();
        Thread thread = new Thread(innerClass);
        thread.start();

        if (message.hasText()) {
            String textMessage = message.getText();
            if (textMessage.equals("/start")) {
                sendNewMessage("Hello, " + message.getChat().getFirstName() + ", use /help");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " is logged in");
            } else if (textMessage.equals("Walked")) {
                checkWalk = true;
                sendNewMessage("Ok, really thank you, " + message.getChat().getFirstName());
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + " with id " + message.getFrom().getId() + " walked");
            } else if (textMessage.equals("Nourished")) {
                checkFeed = true;
                sendNewMessage("Ok, really thank you, " + message.getChat().getFirstName());
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " nourished");
            } else if (textMessage.startsWith("lost dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("lost dog ", "");
                textMessage = textMessage + "; " + PetEvent.LOST.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " lost dog");
            } else if (textMessage.equals("help lost dog")) {
                sendNewMessage("LOST FORMAT: lost (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help lost fog");
            } else if (textMessage.startsWith("found dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("found dog ", "");
                textMessage = textMessage + "; " + PetEvent.FOUND.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() + " with id " +  message.getFrom().getId() + " found help");
            } else if (textMessage.equals("help found dog")) {
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help found dog");
                sendNewMessage("FOUND FORMAT: found (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("care dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("care dog ", "");
                textMessage = textMessage + "; " + PetEvent.CARE.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " care dog");
            } else if (textMessage.equals("help care dog")) {
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help care dog");
                sendNewMessage("CARE FORMAT: care (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
            } else if (textMessage.startsWith("dead dog ")) {
                petKind = "Собака";
                textMessage = textMessage.replaceAll("dead dog ", "");
                textMessage = textMessage + "; " + PetEvent.DEAD.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " found dead dog");
            } else if (textMessage.startsWith("lost cat ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("lost cat ", "");
                textMessage = textMessage + "; " + PetEvent.LOST.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " lost cat");
            } else if (textMessage.equals("help lost cat")) {
                sendNewMessage("LOST FORMAT: lost (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help lost cat");
            } else if (textMessage.startsWith("found cat ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("found cat ", "");
                textMessage = textMessage + "; " + PetEvent.FOUND.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " found cat");
            } else if (textMessage.equals("help found")) {
                sendNewMessage("FOUND FORMAT: found (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help found cat");
            } else if (textMessage.startsWith("care ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("care cat ", "");
                textMessage = textMessage + "; " + PetEvent.CARE.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " found care cat");
            } else if (textMessage.equals("help care")) {
                sendNewMessage("CARE FORMAT: care (кличка); порода; пол; расцветка; размер(большая, средняя, маленькая, mini, supermini), адрес");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help found care cat");
            } else if (textMessage.startsWith("dead cat ")) {
                petKind = "Кошка";
                textMessage = textMessage.replaceAll("dead cat ", "");
                textMessage = textMessage + "; " + PetEvent.DEAD.toString();
                String[] localPetData = textMessage.split("; ");
                String[] tempPetData = new String[localPetData.length + 1];
                System.arraycopy(localPetData, 0, tempPetData, 0, localPetData.length);
                petData = tempPetData;
                menuLocation = true;
                menuPhoto = true;
                sendNewMessage("Do you have location?");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " found dead cat");
            } else if (textMessage.equals("show")) {
                List <List<String>> resultList = JDBCPetDataAccessObject.selectAll();
                show(resultList);
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call show pets");
            } else if (textMessage.equals("show my")){
                List <List<String>> resultList = JDBCPetDataAccessObject.selectById(chatId);
                show(resultList);
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call show his pets");
            } else if (textMessage.equals("settings")) {
                menuTimeSettings = true;
                sendNewMessage("Pls, enter time....");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call settings");
            } else if (menuTimeSettings) {
                time = textMessage;
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " set up time " + time);
            } else if (textMessage.equals("help settings")) {
                sendNewMessage("FORMAT TIME: HH:MM");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help time setup");
            } else if (textMessage.equals("/help")){
                sendNewMessage("USE /help (lost/found/care/dead) (dog/cat) for details + \nUse /settings for setup time for watch for your dog or cat");
                log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " call help");
            } else if (message.getText().equals("No")) {
                if (menuLocation) {
                    menuLocation = false;
                    menuPhoto = true;
                    sendNewMessage("Do you have photo?");
                } else {
                    menuPhoto = false;
                    saveData();
                }
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
            log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " sent photo " + file.getName());

        } else if (menuLocation && message.hasLocation()) {
            if (petData.length == 8) {
                petData[5] = message.getLocation().getLatitude() + ", " + message.getLocation().getLongitude();
            }
            else petData[4] = message.getLocation().getLatitude() + ", " + message.getLocation().getLongitude();

            sendNewMessage("Do you have photo?");
            menuPhoto = true;
            menuLocation = false;
            log.info("User " + message.getFrom().getFirstName() + " " + message.getFrom().getLastName() +  " with id " + message.getFrom().getId() + " sent location " + petData[4]);
        }
    }

    private void show(List<List<String>> resultList){
        for (int i = 0; i < resultList.get(0).size(); i++) {
            String messageShow = "";
            for (int j = 0; j < resultList.size(); j++) {
                if(j < (resultList.size() - 1)) messageShow = messageShow.concat(resultList.get(j).get(i) + ", ");
                else messageShow = messageShow.concat(resultList.get(j).get(i));
            }
            sendNewMessage(messageShow);
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

        JDBCPetDataAccessObject.create(petData, petKind, chatId);

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