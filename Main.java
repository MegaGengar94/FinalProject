import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String FURNITURE_FILE = "data/furniture.txt";
    private static final String ARCHIVED_FILE = "data/archived_furniture.txt";

    private static ArrayList<Furniture> furnitureList = new ArrayList<>();
    private static Furniture[] archivedFurniture = new Furniture[100]; // Fixed-size array
    private static int archivedCount = 0;
    private static Scanner scanner = new Scanner(System.in);

    // Inner class to represent measurements in feet and inches
    static class Measurement {
        int feet, inches;

        public Measurement(int feet, int inches) {
            if (inches >= 12) {
                this.feet = feet + inches / 12;
                this.inches = inches % 12;
            } else {
                this.feet = feet;
                this.inches = inches;
            }
        }

        @Override
        public String toString() {
            return feet + " ft " + inches + " in";
        }

        public String toFileString() {
            return feet + "," + inches;
        }

        public static Measurement fromFileString(String str) {
            String[] parts = str.split(",");
            return new Measurement(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    // Furniture class
    static class Furniture {
        String name;
        Measurement length, width, height;

        public Furniture(String name, Measurement length, Measurement width, Measurement height) {
            this.name = name;
            this.length = length;
            this.width = width;
            this.height = height;
        }

        @Override
        public String toString() {
            return String.format("%-20s | L: %-10s | W: %-10s | H: %-10s",
                    name, length, width, height);
        }

        public String toFileString() {
            return name + "|" + length.toFileString() + "|" + width.toFileString() + "|" + height.toFileString();
        }

        public static Furniture fromFileString(String line) {
            String[] parts = line.split("\\|");
            String name = parts[0];
            Measurement l = Measurement.fromFileString(parts[1]);
            Measurement w = Measurement.fromFileString(parts[2]);
            Measurement h = Measurement.fromFileString(parts[3]);
            return new Furniture(name, l, w, h);
        }
    }

    public static void main(String[] args) {
        createDataDirectory();
        loadData();

        while (true) {
            displayMenu();
            int choice = getMenuChoice();

            try {
                switch (choice) {
                    case 1 -> addFurniture();
                    case 2 -> viewCurrentFurniture();
                    case 3 -> archiveFurniture();
                    case 4 -> viewArchivedFurniture();
                    case 5 -> measureRoomAndFit();
                    case 6 -> {
                        saveData();
                        System.out.println("Goodbye! Data saved.");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("   ROOM & FURNITURE MEASUREMENT SYSTEM");
        System.out.println("=".repeat(50));
        System.out.println("1. Add Furniture");
        System.out.println("2. View Current Furniture");
        System.out.println("3. Archive Furniture (Move to Archive)");
        System.out.println("4. View Archived Furniture");
        System.out.println("5. Measure Room & Check Fit");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static int getMenuChoice() {
        return getValidInteger("Enter choice (1-6): ", 1, 6);
    }

    // Recursive input validation for integers within range
    private static int getValidInteger(String prompt, int min, int max) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            int value = Integer.parseInt(input);
            if (value < min || value > max) {
                System.out.println("Please enter a number between " + min + " and " + max + ".");
                return getValidInteger(prompt, min, max); // Recursive
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter digits only.");
            return getValidInteger(prompt, min, max); // Recursive
        }
    }

    private static int getValidPositiveInteger(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            int value = Integer.parseInt(input);
            if (value < 0) {
                System.out.println("Value cannot be negative.");
                return getValidPositiveInteger(prompt);
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Enter a valid number.");
            return getValidPositiveInteger(prompt);
        }
    }

    private static Measurement getMeasurement(String label) {
        System.out.println("Enter " + label + ":");
        int feet = getValidPositiveInteger("  Feet: ");
        int inches = getValidPositiveInteger("  Inches (0-11): ");
        if (inches > 11) {
            System.out.println("Inches must be 0-11. Auto-converting...");
            inches = inches % 12;
            feet += inches / 12;
        }
        return new Measurement(feet, inches);
    }

    private static void addFurniture() {
        System.out.print("Enter furniture name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        Measurement length = getMeasurement("Length");
        Measurement width = getMeasurement("Width");
        Measurement height = getMeasurement("Height");

        furnitureList.add(new Furniture(name, length, width, height));
        System.out.println("Furniture added successfully!");
    }

    private static void viewCurrentFurniture() {
        if (furnitureList.isEmpty()) {
            System.out.println("No furniture in current list.");
            return;
        }
        System.out.println("\nCurrent Furniture (ArrayList):");
        System.out.println("-".repeat(70));
        System.out.printf("%-20s | %-12s | %-12s | %-12s%n", "Name", "Length", "Width", "Height");
        System.out.println("-".repeat(70));
        for (Furniture f : furnitureList) {
            System.out.println(f);
        }
    }

    private static void archiveFurniture() {
        if (furnitureList.isEmpty()) {
            System.out.println("No furniture to archive.");
            return;
        }

        viewCurrentFurniture();
        int index = getValidInteger("Enter index to archive (0 to " + (furnitureList.size() - 1) + "): ",
                0, furnitureList.size() - 1);

        try {
            Furniture f = furnitureList.remove(index);
            if (archivedCount >= archivedFurniture.length) {
                System.out.println("Archive is full! Cannot add more.");
                furnitureList.add(index, f); // Restore
                return;
            }
            archivedFurniture[archivedCount++] = f;
            System.out.println(f.name + " archived successfully.");
        } catch (Exception e) {
            System.out.println("Error archiving: " + e.getMessage());
        }
    }

    private static void viewArchivedFurniture() {
        if (archivedCount == 0) {
            System.out.println("No archived furniture.");
            return;
        }
        System.out.println("\nArchived Furniture (Array):");
        System.out.println("-".repeat(70));
        System.out.printf("%-20s | %-12s | %-12s | %-12s%n", "Name", "Length", "Width", "Height");
        System.out.println("-".repeat(70));
        for (int i = 0; i < archivedCount; i++) {
            System.out.println(archivedFurniture[i]);
        }
    }

    private static void measureRoomAndFit() {
        System.out.println("=== Room Measurement ===");
        Measurement roomL = getMeasurement("Room Length");
        Measurement roomW = getMeasurement("Room Width");
        Measurement roomH = getMeasurement("Room Height");

        int roomAreaSqFt = roomL.feet * roomW.feet + (roomL.feet * roomW.inches + roomW.feet * roomL.inches) / 12;
        System.out.println("Room floor area: ~" + roomAreaSqFt + " sq ft");

        if (furnitureList.isEmpty()) {
            System.out.println("No furniture to fit.");
            return;
        }

        System.out.println("\nFurniture that fits in room (by length & width):");
        for (Furniture f : furnitureList) {
            boolean fits = (f.length.feet < roomL.feet || (f.length.feet == roomL.feet && f.length.inches <= roomL.inches)) &&
                           (f.width.feet < roomW.feet || (f.width.feet == roomW.feet && f.width.inches <= roomW.inches));
            System.out.println(f.name + " → " + (fits ? "FITS" : "TOO BIG"));
        }
    }

    // File Persistence
    private static void createDataDirectory() {
        new File("data").mkdirs();
    }

    private static void saveData() {
        saveFurnitureList();
        saveArchivedArray();
    }

    private static void saveFurnitureList() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FURNITURE_FILE))) {
            for (Furniture f : furnitureList) {
                writer.write(f.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving current furniture: " + e.getMessage());
        }
    }

    private static void saveArchivedArray() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVED_FILE))) {
            for (int i = 0; i < archivedCount; i++) {
                writer.write(archivedFurniture[i].toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving archived furniture: " + e.getMessage());
        }
    }

    private static void loadData() {
        loadFurnitureList();
        loadArchivedArray();
    }

    private static void loadFurnitureList() {
        File file = new File(FURNITURE_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    furnitureList.add(Furniture.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading furniture: " + e.getMessage());
        }
    }

    private static void loadArchivedArray() {
        File file = new File(ARCHIVED_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null && archivedCount < archivedFurniture.length) {
                if (!line.trim().isEmpty()) {
                    archivedFurniture[archivedCount++] = Furniture.fromFileString(line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading archived furniture: " + e.getMessage());
        }
    }
}