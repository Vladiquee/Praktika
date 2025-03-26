public class praktika1 {
    public static void main(String[] args) {
        if (args.length == 0) {
            // Если аргументы не переданы, выводим сообщение
            System.out.println("No arguments provided.");
        } else {
            // Если аргументы переданы, выводим их
            System.out.println("Arguments:");
            for (int i = 0; i < args.length; i++) {
                System.out.println("Arg " + (i + 1) + ": " + args[i]);
            }
        }
    }
}
