import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Основний клас для керування обчисленнями
 */
class ComputationManager {
    private List<Double> data = Collections.synchronizedList(new ArrayList<>());

    public void addData(double value) {
        data.add(value);
    }

    public List<Double> getData() {
        return data;
    }

    /**
     * Паралельний пошук мінімального значення
     */
    public double findMin() {
        return data.parallelStream().min(Double::compareTo).orElse(Double.NaN);
    }

    /**
     * Паралельний пошук максимального значення
     */
    public double findMax() {
        return data.parallelStream().max(Double::compareTo).orElse(Double.NaN);
    }

    /**
     * Паралельне обчислення середнього значення
     */
    public double computeAverage() {
        return data.parallelStream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
    }

    /**
     * Паралельний відбір значень за критерієм (наприклад, більше 10)
     */
    public List<Double> filterByCriterion(double threshold) {
        return data.parallelStream().filter(value -> value > threshold).collect(Collectors.toList());
    }
}

/**
 * Інтерфейс команди
 */
interface Task {
    void execute();
}

/**
 * Клас, що реалізує завдання обчислення статистики
 */
class StatsTask implements Task {
    private ComputationManager manager;

    public StatsTask(ComputationManager manager) {
        this.manager = manager;
    }

    @Override
    public void execute() {
        System.out.println("Статистична обробка даних...");
        System.out.println("Мінімум: " + manager.findMin());
        System.out.println("Максимум: " + manager.findMax());
        System.out.println("Середнє значення: " + manager.computeAverage());
    }
}

/**
 * Клас управління чергою завдань (Worker Thread)
 */
class TaskQueue {
    private final BlockingQueue<Task> taskQueue = new LinkedBlockingQueue<>();
    private final ExecutorService workerPool = Executors.newFixedThreadPool(2); // Два робочих потоки

    public TaskQueue() {
        for (int i = 0; i < 2; i++) {
            workerPool.execute(this::processTasks);
        }
    }

    public void addTask(Task task) {
        try {
            taskQueue.put(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void processTasks() {
        while (true) {
            try {
                Task task = taskQueue.take();
                task.execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdown() {
        workerPool.shutdown();
    }
}

/**
 * Основний клас для тестування
 */
public class praktika6 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ComputationManager manager = new ComputationManager();
        TaskQueue taskQueue = new TaskQueue();

        while (true) {
            System.out.println("\nМеню:");
            System.out.println("1 - Додати число");
            System.out.println("2 - Запустити статистичну обробку");
            System.out.println("3 - Відфільтрувати за критерієм (>10)");
            System.out.println("4 - Вийти");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Введіть число:");
                    double value = scanner.nextDouble();
                    manager.addData(value);
                    break;

                case 2:
                    taskQueue.addTask(new StatsTask(manager));
                    break;

                case 3:
                    System.out.println("Результати фільтрації (>10): " + manager.filterByCriterion(10));
                    break;

                case 4:
                    taskQueue.shutdown();
                    System.out.println("Програма завершена.");
                    return;

                default:
                    System.out.println("Невірний вибір.");
            }
        }
    }
}