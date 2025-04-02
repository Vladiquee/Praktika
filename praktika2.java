import java.io.*;
import java.util.Scanner;

/**
 * Class that contains room parameters and calculation results.
 * Implements serialization and uses transient fields.
 */
class RoomData implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int length;
    private int width;
    private int height;
    private transient int perimeter;
    private transient int area;
    private transient int volume;
    
    public RoomData(String lengthBinary, String widthBinary, String heightBinary) {
        this.length = Integer.parseInt(lengthBinary, 2);
        this.width = Integer.parseInt(widthBinary, 2);
        this.height = Integer.parseInt(heightBinary, 2);
        compute();
    }

    /**
     * Method for calculating room parameters.
     */
    public void compute() {
        this.perimeter = 2 * (length + width);
        this.area = length * width;
        this.volume = length * width * height;
    }
    
    public int getPerimeter() { return perimeter; }
    public int getArea() { return area; }
    public int getVolume() { return volume; }
}

/**
 * Class for demonstrating serialization and deserialization.
 */
class RoomSerializer {
    /**
     * Method for serializing an object to a file.
     * @param data RoomData object to serialize
     * @param filename File name for saving
     */
    public static void serializeData(RoomData data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Дані збережено у файл " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Method for deserializing an object from a file.
     * @param filename File name for reading
     * @return Restored RoomData object
     */
    public static RoomData deserializeData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            RoomData room = (RoomData) ois.readObject();
            room.compute(); // Restore transient fields
            return room;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

/**
 * Class for testing calculations and serialization.
 */
class RoomComputationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("Введіть довжину у двійковому форматі: ");
        String lengthBinary = scanner.nextLine();
        
        System.out.print("Введіть ширину у двійковому форматі: ");
        String widthBinary = scanner.nextLine();
        
        System.out.print("Введіть висоту у двійковому форматі: ");
        String heightBinary = scanner.nextLine();
        
        scanner.close();
        
        RoomData room = new RoomData(lengthBinary, widthBinary, heightBinary);
        
        System.out.println("==============================");
        System.out.println("Результати обчислень:");
        System.out.println(String.format("%-15s: %d", "Периметр", room.getPerimeter()));
        System.out.println(String.format("%-15s: %d", "Площа", room.getArea()));
        System.out.println(String.format("%-15s: %d", "Об'єм", room.getVolume()));
        System.out.println("==============================");

        String filename = "roomData.ser";
        RoomSerializer.serializeData(room, filename);

        RoomData restoredRoom = RoomSerializer.deserializeData(filename);
        if (restoredRoom != null) {
            System.out.println("Відновлені дані:");
            System.out.println("==============================");
            System.out.println(String.format("%-15s: %d", "Периметр", restoredRoom.getPerimeter()));
            System.out.println(String.format("%-15s: %d", "Площа", restoredRoom.getArea()));
            System.out.println(String.format("%-15s: %d", "Об'єм", restoredRoom.getVolume()));
            System.out.println("==============================");
        }
    }
}
