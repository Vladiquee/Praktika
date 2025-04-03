import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

/**
 * Інтерфейс для об'єктів, які можуть бути відображені.
 */
interface Displayable {
    void display();
}

/**
 * Інтерфейс для об'єктів, які можуть виконувати команди.
 */
interface Command {
    void execute();
    void undo();
}

/**
 * Клас для збереження історії команд.
 */
class CommandManager {
    private static final CommandManager instance = new CommandManager();
    private final Deque<Command> commandHistory = new ArrayDeque<>();

    private CommandManager() {}

    public static CommandManager getInstance() {
        return instance;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);
    }

    public void undoLastCommand() {
        if (!commandHistory.isEmpty()) {
            commandHistory.pop().undo();
        } else {
            System.out.println("Немає команд для скасування.");
        }
    }
}

/**
 * Команда для додавання кімнати.
 */
class AddRoomCommand implements Command {
    private final RoomData room;
    private final List<RoomData> roomList;

    public AddRoomCommand(RoomData room, List<RoomData> roomList) {
        this.room = room;
        this.roomList = roomList;
    }

    @Override
    public void execute() {
        room.compute();
        synchronized (roomList) {
            roomList.add(room);
        }
    }

    @Override
    public void undo() {
        synchronized (roomList) {
            roomList.remove(room);
        }
    }
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
        try {
            this.length = Integer.parseInt(lengthBinary, 2);
            this.width = Integer.parseInt(widthBinary, 2);
            this.height = Integer.parseInt(heightBinary, 2);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Неправильний формат двійкових чисел.", e);
        }
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
 * Клас для текстового відображення кімнати.
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
 * Клас Worker Thread для обробки задач.
 */
class TaskWorker {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public void submitTask(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}

/**
 * Головний клас для тестування програми з діалоговим інтерфейсом.
 */
class RoomComputationTest {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            RoomFactory factory = new TableRoomFactory();
            List<RoomData> roomList = Collections.synchronizedList(new ArrayList<>());
            CommandManager commandManager = CommandManager.getInstance();
            TaskWorker worker = new TaskWorker();

            while (true) {
                System.out.println("Оберіть опцію: \n1 - Додати кімнату \n2 - Скасувати останню дію \n3 - Відобразити кімнати \n4 - Паралельна обробка \n5 - Вийти");
                int choice = scanner.nextInt();
                scanner.nextLine();

                if (choice == 1) {
                    System.out.println("Введіть параметри кімнати у двійковій системі (довжина ширина висота через пробіл):");
                    String[] input = scanner.nextLine().split(" ");
                    RoomData room = factory.createRoom(input[0], input[1], input[2]);
                    commandManager.executeCommand(new AddRoomCommand(room, roomList));
                } else if (choice == 2) {
                    commandManager.undoLastCommand();
                } else if (choice == 3) {
                    synchronized (roomList) {
                        System.out.println("Результати обчислень:");
                        roomList.forEach(RoomData::display);
                    }
                } else if (choice == 4) {
                    worker.submitTask(() -> {
                        synchronized (roomList) {
                            int min = roomList.stream().mapToInt(RoomData::getArea).min().orElse(0);
                            int max = roomList.stream().mapToInt(RoomData::getArea).max().orElse(0);
                            double avg = roomList.stream().mapToInt(RoomData::getArea).average().orElse(0);
                            System.out.println("Мінімальна площа: " + min);
                            System.out.println("Максимальна площа: " + max);
                            System.out.println("Середня площа: " + avg);
                        }
                    });
                } else if (choice == 5) {
                    worker.shutdown();
                    break;
                } else {
                    System.out.println("Невірний вибір.");
                }
            }
        }
    }
}
