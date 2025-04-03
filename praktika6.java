import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

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
    private static CommandManager instance;
    private Stack<Command> commandHistory = new Stack<>();

    private CommandManager() {}

    public static CommandManager getInstance() {
        if (instance == null) {
            instance = new CommandManager();
        }
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
    private RoomData room;
    private List<RoomData> roomList;

    public AddRoomCommand(RoomData room, List<RoomData> roomList) {
        this.room = room;
        this.roomList = roomList;
    }

    @Override
    public void execute() {
        room.compute();
        roomList.add(room);
    }

    @Override
    public void undo() {
        roomList.remove(room);
    }
}

/**
 * Макрокоманда для виконання декількох команд одночасно.
 */
class MacroCommand implements Command {
    private List<Command> commands = new ArrayList<>();

    public void addCommand(Command command) {
        commands.add(command);
    }

    @Override
    public void execute() {
        for (Command command : commands) {
            command.execute();
        }
    }

    @Override
    public void undo() {
        for (int i = commands.size() - 1; i >= 0; i--) {
            commands.get(i).undo();
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
 * Клас Worker Thread для обробки задач.
 */
class TaskWorker {
    private ExecutorService executor = Executors.newFixedThreadPool(4);

    public void submitTask(Runnable task) {
        executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }
}

/**
 * Головний клас для тестування програми з діалоговим інтерфейсом.
 */
class RoomComputationTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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
                Command addRoom = new AddRoomCommand(room, roomList);
                commandManager.executeCommand(addRoom);
            } else if (choice == 2) {
                commandManager.undoLastCommand();
            } else if (choice == 3) {
                System.out.println("Результати обчислень:");
                for (RoomData room : roomList) {
                    room.display();
                }
            } else if (choice == 4) {
                worker.submitTask(() -> {
                    int min = roomList.stream().mapToInt(RoomData::getArea).min().orElse(0);
                    int max = roomList.stream().mapToInt(RoomData::getArea).max().orElse(0);
                    double avg = roomList.stream().mapToInt(RoomData::getArea).average().orElse(0);
                    System.out.println("Мінімальна площа: " + min);
                    System.out.println("Максимальна площа: " + max);
                    System.out.println("Середня площа: " + avg);
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

