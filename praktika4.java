import java.io.*;
import java.util.*;
import java.util.Scanner;

/**
 * Інтерфейс для об'єктів, які можуть виводити результати обчислень.
 */
interface Displayable {
    void display();
}

/**
 * Абстрактний клас для представлення приміщення.
 */
abstract class RoomData implements Serializable, Displayable {
    protected int length;
    protected int width;
    protected int height;
    protected int perimeter;
    protected int area;
    protected int volume;

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
    public abstract void display();
}

/**
 * Клас, що розширює RoomData та надає можливість відображення у вигляді текстової таблиці.
 */
class TableRoomData extends RoomData {
    public TableRoomData(String lengthBinary, String widthBinary, String heightBinary) {
        super(lengthBinary, widthBinary, heightBinary);
    }

    @Override
    public void display() {
        System.out.printf("%-10s %-10s %-10s %-10s %-10s %-10s%n", "Довжина", "Ширина", "Висота", "Периметр", "Площа", "Об'єм");
        System.out.printf("%-10d %-10d %-10d %-10d %-10d %-10d%n", length, width, height, perimeter, area, volume);
    }
}

/**
 * Фабричний інтерфейс.
 */
interface RoomFactory {
    RoomData createRoom(String lengthBinary, String widthBinary, String heightBinary);
}

/**
 * Конкретна фабрика для створення об'єктів TableRoomData.
 */
class TableRoomFactory implements RoomFactory {
    @Override
    public RoomData createRoom(String lengthBinary, String widthBinary, String heightBinary) {
        return new TableRoomData(lengthBinary, widthBinary, heightBinary);
    }
}

/**
 * Головний клас для тестування програми з діалоговим інтерфейсом.
 */
class RoomComputationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RoomFactory factory = new TableRoomFactory();
        List<RoomData> roomList = new ArrayList<>();

        System.out.println("Введіть кількість кімнат:");
        int count = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < count; i++) {
            System.out.println("Введіть параметри кімнати у двійковій системі (довжина ширина висота через пробіл):");
            String[] input = scanner.nextLine().split(" ");
            RoomData room = factory.createRoom(input[0], input[1], input[2]);
            room.compute();
            roomList.add(room);
        }

        System.out.println("Результати обчислень:");
        for (RoomData room : roomList) {
            room.display();
        }
    }
}
