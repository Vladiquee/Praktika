import java.io.*;
import java.util.*;

/**
 * Інтерфейс команди
 */
interface Command {
    void execute();
    void undo();
}

/**
 * Клас для обчислень з підтримкою Singleton
 */
class SolverSingleton {
    private static SolverSingleton instance;
    private List<ComputationData> computations = new ArrayList<>();

    private SolverSingleton() {}

    public static SolverSingleton getInstance() {
        if (instance == null) {
            instance = new SolverSingleton();
        }
        return instance;
    }

    public void addComputation(ComputationData data) {
        computations.add(data);
    }

    public void removeLastComputation() {
        if (!computations.isEmpty()) {
            computations.remove(computations.size() - 1);
        }
    }

    public List<ComputationData> getComputations() {
        return computations;
    }
}

/**
 * Команда для додавання нового обчислення
 */
class ComputeCommand implements Command {
    private SolverSingleton solver = SolverSingleton.getInstance();
    private ComputationData data;

    public ComputeCommand(double value) {
        this.data = new ComputationData(value);
        data.setResult(Math.sqrt(value));
    }

    @Override
    public void execute() {
        solver.addComputation(data);
    }

    @Override
    public void undo() {
        solver.removeLastComputation();
    }
}

/**
 * Макрокоманда (група команд)
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
 * Клас для збереження/відновлення даних
 */
class Demo {
    public static void serializeData(List<ComputationData> computations, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(computations);
            System.out.println("Дані збережені.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<ComputationData> deserializeData(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<ComputationData>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}

/**
 * Інтерфейс для відображення результатів
 */
interface Displayable {
    void display(List<ComputationData> computations);
}

/**
 * Відображення у текстовому вигляді
 */
class TextDisplay implements Displayable {
    @Override
    public void display(List<ComputationData> computations) {
        for (ComputationData data : computations) {
            System.out.println("Вхідне значення: " + data.getInput() + ", Результат: " + data.getResult());
        }
    }
}

/**
 * Фабрика для створення об'єктів відображення
 */
abstract class DisplayFactory {
    public abstract Displayable createDisplay();
}

class TextDisplayFactory extends DisplayFactory {
    @Override
    public Displayable createDisplay() {
        return new TextDisplay();
    }
}

/**
 * Клас тестування
 */
public class praktika5 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SolverSingleton solver = SolverSingleton.getInstance();
        Deque<Command> undoStack = new ArrayDeque<>();
        DisplayFactory factory = new TextDisplayFactory();
        Displayable display = factory.createDisplay();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1 - Додати обчислення");
            System.out.println("2 - Скасувати останню операцію (Undo)");
            System.out.println("3 - Виконати макрокоманду");
            System.out.println("4 - Показати результати");
            System.out.println("5 - Вийти");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Введіть число:");
                    double value = scanner.nextDouble();
                    Command computeCommand = new ComputeCommand(value);
                    computeCommand.execute();
                    undoStack.push(computeCommand);
                    break;

                case 2:
                    if (!undoStack.isEmpty()) {
                        undoStack.pop().undo();
                        System.out.println("Остання операція скасована.");
                    } else {
                        System.out.println("Немає операцій для скасування.");
                    }
                    break;

                case 3:
                    MacroCommand macroCommand = new MacroCommand();
                    System.out.println("Скільки операцій додати в макрокоманду?");
                    int count = scanner.nextInt();
                    for (int i = 0; i < count; i++) {
                        System.out.println("Введіть число:");
                        value = scanner.nextDouble();
                        Command cmd = new ComputeCommand(value);
                        macroCommand.addCommand(cmd);
                    }
                    macroCommand.execute();
                    undoStack.push(macroCommand);
                    System.out.println("Макрокоманда виконана.");
                    break;

                case 4:
                    System.out.println("Результати:");
                    display.display(solver.getComputations());
                    break;

                case 5:
                    System.out.println("Програма завершена.");
                    return;

                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }
}

/**
 * Клас, що зберігає вхідне значення та результат обчислення
 */
class ComputationData implements Serializable {
    private static final long serialVersionUID = 1L;
    private double input;
    private double result;

    public ComputationData(double input) {
        this.input = input;
    }

    public double getInput() {
        return input;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}