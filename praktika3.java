import java.io.*;
import java.util.*;

/**
 * Інтерфейс для об'єктів, які можуть виводити результати обчислень.
 */
interface Displayable {
    void display();
}

/**
 * Базовий клас для зберігання параметрів приміщення та їх обчислень.
 */
class RoomData implements Serializable, Displayable {
    private static final long serialVersionUID = 1L;

    private int length;
    private int width;
    private int height;
    private int perimeter;
    private int area;
    private int volume;

    public RoomData(String lengthBinary, String widthBinary, String heightBinary) {
        this.length = Integer.parseInt(lengthBinary, 2);
        this.width = Integer.parseInt(widthBinary, 2);
        this.height = Integer.parseInt(heightBinary, 2);
    }

    public void compute() {
        this.perimeter = 2 * (length + width);
        this.area = length * width;
        this.volume = length * width * height;
    }

    public int getPerimeter() { return perimeter; }
    public int getArea() { return area; }
    public int getVolume() { return volume; }

    @Override
    public void display() {
        System.out.println("Периметр: " + perimeter);
        System.out.println("Площа: " + area);
        System.out.println("Об'єм: " + volume);
    }
}

/**
 * Інтерфейс для фабричних класів.
 */
interface RoomFactory {
    RoomData createRoom(String lengthBinary, String widthBinary, String heightBinary);
}

/**
 * Конкретна фабрика для створення об'єктів RoomData.
 */
class ConcreteRoomFactory implements RoomFactory {
    @Override
    public RoomData createRoom(String lengthBinary, String widthBinary, String heightBinary) {
        return new RoomData(lengthBinary, widthBinary, heightBinary);
    }
}

/**
 * Клас для серіалізації та десеріалізації.
 */
class RoomSerializer {
    public static void serializeData(List<RoomData> data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Дані збережені у файл " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static List<RoomData> deserializeData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<RoomData>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

/**
 * Клас для створення різних форматів відображення результатів.
 */
class TextRoomDisplay implements Displayable {
    private RoomData roomData;

    public TextRoomDisplay(RoomData roomData) {
        this.roomData = roomData;
    }

    @Override
    public void display() {
        System.out.println("Периметр: " + roomData.getPerimeter());
        System.out.println("Площа: " + roomData.getArea());
        System.out.println("Об'єм: " + roomData.getVolume());
    }
}

/**
 * Головний клас для тестування програми.
 */
class RoomComputationTest {
    public static void main(String[] args) {
        RoomFactory factory = new ConcreteRoomFactory();
        List<RoomData> roomList = new ArrayList<>();

        // Створення кімнат
        roomList.add(factory.createRoom("101", "100", "11"));  // 5x4x3
        roomList.add(factory.createRoom("110", "101", "10"));  // 6x5x2

        // Обчислення параметрів
        for (RoomData room : roomList) {
            room.compute();
        }

        // Виведення результатів
        for (RoomData room : roomList) {
            Displayable display = new TextRoomDisplay(room);
            display.display();
        }

        // Серіалізація
        String filename = "roomDataList.ser";
        RoomSerializer.serializeData(roomList, filename);

        // Десеріалізація
        List<RoomData> restoredRooms = RoomSerializer.deserializeData(filename);
        System.out.println("Відновлені дані:");
        for (RoomData room : restoredRooms) {
            Displayable display = new TextRoomDisplay(room);
            display.display();
        }
    }
}

