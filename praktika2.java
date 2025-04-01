import java.io.*;

/**
 * Клас, що містить параметри приміщення та результати обчислень.
 * Реалізує серіалізацію.
 */
class RoomData implements Serializable {
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
}

/**
 * Клас для демонстрації серіалізації та десеріалізації.
 */
class RoomSerializer {
    public static void serializeData(RoomData data, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Дані збережені у файл " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static RoomData deserializeData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (RoomData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

/**
 * Клас для тестування обчислень та серіалізації.
 */
class RoomComputationTest {
    public static void main(String[] args) {
        // Бінарні значення довжини, ширини та висоти
        String lengthBinary = "101";  // 5
        String widthBinary = "100";   // 4
        String heightBinary = "11";   // 3
        
        RoomData room = new RoomData(lengthBinary, widthBinary, heightBinary);
        room.compute();
        
        System.out.println("Периметр: " + room.getPerimeter());
        System.out.println("Площа: " + room.getArea());
        System.out.println("Об'єм: " + room.getVolume());

        String filename = "roomData.ser";
        RoomSerializer.serializeData(room, filename);

        RoomData restoredRoom = RoomSerializer.deserializeData(filename);
        if (restoredRoom != null) {
            System.out.println("Відновлені дані:");
            System.out.println("Периметр: " + restoredRoom.getPerimeter());
            System.out.println("Площа: " + restoredRoom.getArea());
            System.out.println("Об'єм: " + restoredRoom.getVolume());
        }
    }
}