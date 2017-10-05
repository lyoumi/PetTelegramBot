package dao;

import java.util.List;

public interface PetDataAccessObject {
    void create(String[] petData, String petKind, String chatId);
    List<List<String>> selectAll();
    List<List<String>> selectById(String chatId);
    void remove(String addID);
}
